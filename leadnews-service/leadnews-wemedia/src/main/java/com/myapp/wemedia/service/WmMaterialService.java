package com.myapp.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.dto.WmMaterialDto;
import com.myapp.model.wemedia.pojo.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     * @param multipartFile 图片文件
     * @return 响应结果
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);


    ResponseResult listByCondition(WmMaterialDto dto);

    ResponseResult removePictureById(Long id);
}