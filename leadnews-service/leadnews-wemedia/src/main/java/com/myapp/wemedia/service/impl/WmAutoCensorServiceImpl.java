package com.myapp.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myapp.api.article.IArticleClient;
import com.myapp.common.aliyun.GreenImageScan;
import com.myapp.common.aliyun.GreenTextScan;
import com.myapp.common.tess4j.Tess4jClient;
import com.myapp.file.service.FileStorageService;
import com.myapp.model.article.dto.ArticleDto;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.pojo.WmChannel;
import com.myapp.model.wemedia.pojo.WmNews;
import com.myapp.model.wemedia.pojo.WmSensitive;
import com.myapp.model.wemedia.pojo.WmUser;
import com.myapp.util.common.SensitiveWordUtil;
import com.myapp.wemedia.mapper.WmChannelMapper;
import com.myapp.wemedia.mapper.WmNewsMapper;
import com.myapp.wemedia.mapper.WmSensitiveMapper;
import com.myapp.wemedia.mapper.WmUserMapper;
import com.myapp.wemedia.service.WmAutoCensorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.myapp.model.wemedia.constatnt.WemediaConstants.*;

@Service
@Slf4j
public class WmAutoCensorServiceImpl implements WmAutoCensorService {

    @Autowired
    private WmNewsMapper newsMapper;

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private WmChannelMapper channelMapper;

    @Autowired
    private WmUserMapper userMapper;


    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private WmSensitiveMapper sensitiveMapper;

    @Autowired
    private Tess4jClient tess4jClient;

    /**
     * 自媒体文章审核
     *
     * @param newsId 自媒体文章id
     */
    @Override
    @Transactional
    @Async
    public void autoCensorWmNews(Integer newsId) {
        if (newsId == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-newsId不能为空");
        }
        WmNews wmNews = newsMapper.selectById(newsId);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }
        // 如果文章不在提交（待审核）状态，则抛出异常
        if (!wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不在待审核状态");
        }
        Map<String, Object> contentMap = extractTextAndImage(wmNews);
        // 审核文章
        String text = (String) contentMap.get("text");
        Set<String> images = (Set<String>) contentMap.get("images");
        // 审核文章、图片失败
        if (!censorText(text, wmNews) || !censorImages(images, wmNews)) {
            log.debug("文章审核失败, id={}", wmNews.getId());
            return;
        }
        if (!processSensitiveWordCensor(text, wmNews)){
            log.debug("文章审核失败, id={}", wmNews.getId());
            return;
        }
        // 审核成功，保存文章到article_content
        log.debug("文章审核成功, id={}", wmNews.getId());
        ResponseResult result = saveAppArticle(wmNews);
        if (!result.getCode().equals(200)) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
        }
        // 回填article_id
        wmNews.setArticleId((Long) result.getData());
        updateWmNews(wmNews, WM_STATUS_PUBLISHED, "审核成功", WM_NEWS_ENABLE);
    }



    /**
     * 自管理的敏感词审核
     *
     * @param text
     * @param news
     * @return
     */
    private boolean processSensitiveWordCensor(String text, WmNews news) {
        boolean flag = true;
        List<String> sensitiveWords = sensitiveMapper.selectList(new LambdaQueryWrapper<WmSensitive>()
                        .select(WmSensitive::getSensitives))
                .stream()
                .map(WmSensitive::getSensitives)
                .collect(Collectors.toList());
        SensitiveWordUtil.initMap(sensitiveWords);
        Map<String, Integer> map = SensitiveWordUtil.matchWords(text);
        if (map.size() > 0) {
            flag = false;
            updateWmNews(news, WM_STATUS_CENSOR_FAIL, "当前文章存在违规内容: " + map.keySet(), WM_NEWS_DISABLE);
        }
        return flag;
    }

    /**
     * 保存app端相关的文章数据
     *
     * @param wmNews
     * @return
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, articleDto);
        // 封面类型
        articleDto.setLayout(wmNews.getType());
        WmChannel wmChannel = channelMapper.selectOne(new LambdaQueryWrapper<WmChannel>()
                .select(WmChannel::getName)
                .eq(WmChannel::getId, wmNews.getChannelId()));
        if (wmChannel != null) {
            articleDto.setChannelName(wmChannel.getName());
        }
        WmUser wmUser = userMapper.selectOne(new LambdaQueryWrapper<WmUser>()
                .select(WmUser::getName)
                .eq(WmUser::getId, wmNews.getUserId()));
        if (wmUser != null) {
            articleDto.setAuthorName(wmUser.getName());
        }
        if (wmNews.getArticleId() != null) {
            articleDto.setId(wmNews.getArticleId());
        }
        articleDto.setCreatedTime(new Date());
        return articleClient.saveArticle(articleDto);
    }

    private boolean censorImages(Set<String> images, WmNews wmNews) {
        AtomicBoolean flag = new AtomicBoolean(true);
        if (images == null || images.size() == 0) {
            return flag.get();
        }
        List<byte[]> imageList = new ArrayList<>();
        images.forEach(item -> {
            byte[] imgFile = fileStorageService.downLoadFile(item);
            // 本地ocr检验图片是否违规
            flag.set(localOcrCensor(wmNews, imgFile));
            if (!flag.get()){   // 如果违规，则直接停止其他图片的检测
                return;
            }
            imageList.add(imgFile);
        });
        // 检测本地ocr检测结果，如果有违规则直接返回审核失败
        if (!flag.get()){
            return flag.get();
        }
        try {
            Map result = greenImageScan.imageScan(imageList);
            flag.set(processCensorResult(result, wmNews));
        } catch (Exception e) {
            flag.set(false);
            log.error(e.getMessage());
        }
        return flag.get();
    }

    private boolean localOcrCensor(WmNews wmNews, byte[] imgFile){
        boolean flag = true;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imgFile);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            String result = tess4jClient.doOCR(bufferedImage);
            flag = processSensitiveWordCensor(result, wmNews);
        } catch (Exception e) {
            log.error("OCR失败：{}", e.getMessage());
        }
        return flag;
    }

    private boolean processCensorResult(Map result, WmNews wmNews) {
        if (result.get("suggestion").equals("block")) {
            updateWmNews(wmNews, WM_STATUS_CENSOR_FAIL, "当前文章存在违规内容", WM_NEWS_DISABLE);
            return false;
        }
        if (result.get("suggestion").equals("review")) {
            updateWmNews(wmNews, WM_STATUS_CENSOR_REVIEW, "当前文章需要正在人工审核", WM_NEWS_DISABLE);
            return false;
        }
        return true;
    }

    private boolean censorText(String text, WmNews wmNews) {
        // 审核结果标志，没问题为true
        boolean flag = true;
        try {
            Map result = greenTextScan.greenTextScan(text);
            flag = processCensorResult(result, wmNews);
        } catch (Exception e) {
            flag = false;
            log.error(e.getMessage());
        }
        return flag;
    }

    /**
     * 修改文章状态和拒绝理由
     *
     * @param wmNews 自媒体文章
     * @param status 自媒体状态
     * @param reason 拒绝原因
     */
    private void updateWmNews(WmNews wmNews, Short status, String reason, Short enable) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNews.setEnable(enable);
        newsMapper.updateById(wmNews);
    }

    /**
     * 1。从自媒体文章的内容中提取文本和图片
     * 2.提取文章的封面图片
     *
     * @param news 自媒体文章
     * @return
     */
    private Map<String, Object> extractTextAndImage(WmNews news) {
        StringBuilder text = new StringBuilder();
        Set<String> images = new HashSet<>();
        // 提取文章标题
        text.append(news.getTitle());
        // 提取内容
        if (StringUtils.hasLength(news.getContent())) {
            List<Map> maps = JSON.parseArray(news.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("image")) {
                    images.add((String) map.get("value"));
                }
                if (map.get("type").equals("text")) {
                    text.append(map.get("value"));
                }
            }
        }
        // 提取封面
        if (StringUtils.hasLength(news.getImages())) {
            String[] urls = news.getImages().split(",");
            images.addAll(Arrays.asList(urls));
        }
        // 封装返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("text", text.toString());
        resultMap.put("images", images);
        return resultMap;
    }
}
