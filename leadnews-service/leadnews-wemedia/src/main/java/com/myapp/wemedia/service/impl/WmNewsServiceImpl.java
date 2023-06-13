package com.myapp.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.common.exception.CustomException;
import com.myapp.model.article.dto.ArticleStatusDto;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.wemedia.constatnt.WmNewsMessageConstants;
import com.myapp.model.common.dto.PageResponseResult;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.dto.WmNewsDto;
import com.myapp.model.wemedia.dto.WmNewsPageReqDto;
import com.myapp.model.wemedia.dto.WmNewsStatusDto;
import com.myapp.model.wemedia.pojo.WmMaterial;
import com.myapp.model.wemedia.pojo.WmNews;
import com.myapp.model.wemedia.pojo.WmNewsMaterial;
import com.myapp.model.wemedia.pojo.WmUser;
import com.myapp.util.thread.WmThreadLocalUtil;
import com.myapp.wemedia.mapper.WmMaterialMapper;
import com.myapp.wemedia.mapper.WmNewsMapper;
import com.myapp.wemedia.mapper.WmNewsMaterialMapper;
import com.myapp.wemedia.service.WmAutoCensorService;
import com.myapp.wemedia.service.WmNewsService;
import com.myapp.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.myapp.model.wemedia.constatnt.WemediaConstants.*;

@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {


    @Autowired
    private WmNewsMaterialMapper newsMaterialMapper;

    @Autowired
    private WmMaterialMapper materialMapper;

    @Autowired
    private WmAutoCensorService autoCensorService;

    @Autowired
    private WmNewsTaskService newsTaskService;

    @Override
    public ResponseResult listAll(WmNewsPageReqDto dto) {
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        dto.checkParam();
        // 构造查询条件
        LambdaQueryWrapper<WmNews> queryWrapper = new LambdaQueryWrapper<>();
        WmUser user = WmThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Integer userId = user.getId();
        queryWrapper.eq(WmNews::getUserId, userId)
                .eq(dto.getStatus() != null, WmNews::getStatus, dto.getStatus())
                .eq(dto.getChannelId() != null, WmNews::getChannelId, dto.getChannelId())
                .between(dto.getBeginPubDate() != null && dto.getEndPubDate() != null,
                        WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate())
                .like(dto.getKeyword() != null, WmNews::getTitle, dto.getKeyword())
                .orderByDesc(WmNews::getCreatedTime);


        // 构造分页条件
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        this.baseMapper.selectPage(page, queryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    @Transactional
    public ResponseResult submitNews(WmNewsDto dto) {
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        WmNews news = new WmNews();
        BeanUtils.copyProperties(dto, news);
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            String images = StringUtils.collectionToDelimitedString(dto.getImages(), ",");
            news.setImages(images);
        }
        if (Objects.equals(WM_NEWS_TYPE_AUTO, dto.getType())) {
            news.setType(null);
        }

        saveOrUpdateWmNews(news);

        // 如果是存为草稿则直接结束方法
        if (Objects.equals(dto.getStatus(), WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 提取内容中的图片
        List<String> imageUrls = extractContentImageUrls(dto);

        handleContentImages(news, imageUrls);

        handleCoverImages(dto, news, imageUrls);

        // 当事务commit后在执行异步方法
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // 审核文章
                        // autoCensorService.autoCensorWmNews(news.getId());
                        newsTaskService.addWmNewsToTask(news.getId(), news.getPublishTime());
                    }
                }
        );

        // autoCensorService.autoCensorWmNews(news.getId());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    @Transactional
    public ResponseResult removeWmNewsById(Long newsId) {
        if (newsId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章id不能为空");
        }
        WmNews wmNews = baseMapper.selectById(newsId);

        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }
        // 删除自媒体文章
        // 删除文章与图片的关系
        newsMaterialMapper.delete(new LambdaQueryWrapper<WmNewsMaterial>().eq(WmNewsMaterial::getNewsId, newsId));
        // 文章已发布，不能删除
        Short enable = wmNews.getEnable();
        if (enable != null && enable.equals(WM_NEWS_ENABLE)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章已发布，不能删除");
        }
        // 删除app文章
        Long articleId = wmNews.getArticleId();
        if (articleId != null) {
            // TODO 删除app文章
            ArticleStatusDto articleStatusDto = new ArticleStatusDto();
            articleStatusDto.setId(articleId);
            kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_DELETE_TOPIC, JSON.toJSONString(articleStatusDto));
        }
        baseMapper.deleteById(newsId);
        log.info("删除文章成功, id={}", newsId);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @Transactional
    public ResponseResult downOrUpWmNews(WmNewsStatusDto dto) {
        Integer newsId = dto.getId();
        Short enable = dto.getEnable();
        if (newsId == null || enable == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmNews wmNews = baseMapper.selectById(newsId);
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }
        if (!wmNews.getStatus().equals(WM_STATUS_PUBLISHED)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章不在发布状态，无法上架或下架");
        }
        // 修改自媒体文章上架状态
        if (enable == 1 || enable == 0) {
            LambdaUpdateWrapper<WmNews> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(WmNews::getEnable, enable)
                    .eq(WmNews::getId, newsId);
            baseMapper.update(wmNews, updateWrapper);
        }

        // 修改app文章上架状态
        // 使用kafka消息队列，异步通知app下架文章
        if (wmNews.getArticleId() != null){
            ArticleStatusDto statusDto = new ArticleStatusDto();
            statusDto.setId(wmNews.getArticleId());
            statusDto.setEnable(dto.getEnable());
            kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(statusDto));
            log.debug("向kafka发送消息，topic={},message={}",WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, statusDto);
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 保存自媒体文章
     *
     * @param news
     */
    private void saveOrUpdateWmNews(WmNews news) {
        WmUser user = WmThreadLocalUtil.getUser();
        news.setUserId(user.getId());
        news.setCreatedTime(new Date());
        news.setSubmitedTime(new Date());
        news.setEnable(WM_NEWS_DISABLE);
        if (news.getId() == null) {
            this.save(news);
        } else {
            newsMaterialMapper.delete(new LambdaQueryWrapper<WmNewsMaterial>()
                    .eq(WmNewsMaterial::getNewsId, news.getId()));
            this.updateById(news);
        }

    }

    /**
     * 提取内容中的图片地址
     *
     * @param dto
     * @return
     */
    private static List<String> extractContentImageUrls(WmNewsDto dto) {
        List<Map> maps = JSON.parseArray(dto.getContent(), Map.class);
        List<String> imageUrls = new ArrayList<>();
        for (Map map : maps) {
            if (map.get("type") != null && map.get("type").equals("image")) {
                String imageUrl = (String) map.get("value");
                imageUrls.add(imageUrl);
            }
        }
        // 文章中的图片也许会重复，需要去重处理
        return imageUrls.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 处理正文图片，并保存文章和图片的关系
     *
     * @param news
     * @param imageUrls
     */
    private void handleContentImages(WmNews news, List<String> imageUrls) {
        if (imageUrls != null && imageUrls.size() > 0) {
            saveRelationOfNewsAndMaterials(news, imageUrls, WM_CONTENT_REFERENCE);
        }
        log.debug("处理正文图片，并保存文章和图片的关系,正文id={},imageUrls={}", news.getId(), imageUrls);
    }

    /**
     * 处理封面图片，并保存封面和图片的关系
     *
     * @param dto
     * @param news
     * @param imageUrls
     */
    private void handleCoverImages(WmNewsDto dto, WmNews news, List<String> imageUrls) {
        // 设置封面图片
        // 自动
        List<String> images = dto.getImages();
        if (dto.getType().equals(WM_NEWS_TYPE_AUTO)) {
            // 多图
            if (imageUrls.size() >= 3) {
                news.setType(WM_NEWS_MANY_IMAGE);
                images = imageUrls.stream().limit(3).collect(Collectors.toList());
            }
            // 单图
            else if (imageUrls.size() >= 1) {
                news.setType(WM_NEWS_SINGLE_IMAGE);
                images = imageUrls.stream().limit(1).collect(Collectors.toList());
            }
            // 无图
            else news.setType(WM_NEWS_NONE_IMAGE);

            if (images != null && images.size() > 0) {
                news.setImages(StringUtils.collectionToDelimitedString(images, ","));
            }
            updateById(news);
        }
        if (images != null && images.size() > 0) {
            saveRelationOfNewsAndMaterials(news, images, WM_COVER_REFERENCE);
        }
        log.debug("处理封面图片，并保存封面和图片的关系,正文id={},imageUrls={}", news.getId(), imageUrls);
    }

    /**
     * 保存的图片和文章的关系
     *
     * @param news      自媒体文章
     * @param imageUrls 正文中提取的image url列表
     * @param type      图片素材 与文章的关系（封面，内容图片）
     */
    private void saveRelationOfNewsAndMaterials(WmNews news, List<String> imageUrls, Short type) {
        // 根据url list查询素材库，获取素材id list
        LambdaQueryWrapper<WmMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(imageUrls != null, WmMaterial::getUrl, imageUrls);
        List<WmMaterial> materialList = materialMapper.selectList(wrapper);
        // 素材查询出现问题，抛出异常，回滚状态
        if (materialList == null || materialList.isEmpty() || materialList.size() != imageUrls.size()) {
            throw new CustomException(AppHttpCodeEnum.PARAM_IMAGE_FORMAT_ERROR);
        }
        List<Integer> idList = materialList.stream().map(WmMaterial::getId).collect(Collectors.toList());

        // 保存内容图片与内容id的关系
        newsMaterialMapper.saveRelations(idList, news.getId(), type);
    }

}