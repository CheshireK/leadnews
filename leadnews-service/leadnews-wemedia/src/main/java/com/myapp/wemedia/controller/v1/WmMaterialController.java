package com.myapp.wemedia.controller.v1;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.dto.WmMaterialDto;
import com.myapp.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {

    @Autowired
    private WmMaterialService materialService;

    /**
     * 图片上传
     * @param multipartFile 图片文件
     * @return 响应结果
     */
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile){
        return materialService.uploadPicture(multipartFile);
    }

    /**
     * 分页查询查询素材列表
     * @param dto 查询条件参数：
     * - 分页大小
     * - 第几页
     * - 是否收藏
     * @return
     */
    @PostMapping("/list")
    public ResponseResult list(@RequestBody WmMaterialDto dto){
        return materialService.listByCondition(dto);
    }

    /**
     * http://myvm.site:8081/wemedia/MEDIA/wemedia/api/v1/material/del_picture/71
     */
    @GetMapping("/del_picture/{id}")
    public ResponseResult deletePicture(@PathVariable Long id){
        return materialService.removePictureById(id);
    }
}
