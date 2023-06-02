package com.myapp.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.dto.WmNewsDto;
import com.myapp.model.wemedia.dto.WmNewsPageReqDto;
import com.myapp.model.wemedia.pojo.WmNews;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 查询文章
     * @param dto
     * @return
     */
    ResponseResult listAll(WmNewsPageReqDto dto);

    ResponseResult submitNews(WmNewsDto dto);


    ResponseResult removeWmNewsById(Long newsId);
}