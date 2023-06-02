package com.myapp.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.file.service.FileStorageService;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.PageResponseResult;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.dto.WmMaterialDto;
import com.myapp.model.wemedia.pojo.WmMaterial;
import com.myapp.model.wemedia.pojo.WmNewsMaterial;
import com.myapp.util.thread.WmThreadLocalUtil;
import com.myapp.wemedia.mapper.WmMaterialMapper;
import com.myapp.wemedia.mapper.WmNewsMaterialMapper;
import com.myapp.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.myapp.model.common.constant.MaterialConstant.*;

@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {


    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private WmNewsMaterialMapper newsMaterialMapper;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }


        // String originalFilename = multipartFile.getOriginalFilename();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String filename = Optional.ofNullable(multipartFile.getOriginalFilename())
                .map(originalFilename -> uuid + originalFilename.substring(originalFilename.lastIndexOf(".")))
                .get();
        // .orElse(uuid + ".jpg");

        String fileUrl = null;
        try {
            fileUrl = fileStorageService.uploadImgFile("", filename, multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("用户上传失败，message: {}", e.getMessage());
        }

        Integer userId = WmThreadLocalUtil.getUser().getId();
        WmMaterial material = new WmMaterial();
        material.setUserId(userId);
        material.setUrl(fileUrl);
        material.setIsCollection(MATERIAL_NOT_COLLECTION);
        material.setType(MATERIAL_TYPE_IMAGE);
        material.setCreatedTime(new Date());
        baseMapper.insert(material);
        log.debug("用户上传成功，文件url：{}", fileUrl);
        return ResponseResult.okResult(material);
    }

    @Override
    public ResponseResult listByCondition(WmMaterialDto dto) {
        dto.checkParam();
        Integer userId = WmThreadLocalUtil.getUser().getId();
        // 构造查询条件
        LambdaQueryWrapper<WmMaterial> queryWrapper = new LambdaQueryWrapper<WmMaterial>()
                .eq(WmMaterial::getUserId, userId)
                .eq(dto.getIsCollection() != null && Objects.equals(dto.getIsCollection(), MATERIAL_COLLECTION),
                        WmMaterial::getIsCollection, dto.getIsCollection());
        // 构造分页条件
        Page<WmMaterial> page = new Page<>(dto.getPage(), dto.getSize());
        baseMapper.selectPage(page, queryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    @Transactional
    public ResponseResult removePictureById(Long id) {
        if (id==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"参数不合法");
        }
        LambdaQueryWrapper<WmNewsMaterial> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WmNewsMaterial::getMaterialId,id);
        List<WmNewsMaterial> materialList = newsMaterialMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(materialList)){
            log.debug("素材文件存在引用，无法删除,id={}",id);
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "图片存在引用，无法删除");
        }
        WmMaterial material = baseMapper.selectById(id);
        if (material == null){
            log.debug("素材文件不存在,id={}",id);
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"文件不存在");
        }
        if (StringUtils.hasLength(material.getUrl())){
            fileStorageService.delete(material.getUrl());
            log.debug("minio删除文件url: {}", material.getUrl());
            baseMapper.deleteById(id);
            log.debug("数据库删除文件, id={}, url={}", material.getId(), material.getUrl());
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文件删除失败");
    }
}