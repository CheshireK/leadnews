package com.myapp.wemedia.controller.v1;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.dto.WmNewsDto;
import com.myapp.model.wemedia.dto.WmNewsPageReqDto;
import com.myapp.model.wemedia.dto.WmNewsStatusDto;
import com.myapp.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService newsService;

    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto){
        return  newsService.listAll(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody WmNewsDto dto){
        return newsService.submitNews(dto);
    }

    /**
     * http://myvm.site:8081/wemedia/MEDIA/wemedia/api/v1/news/del_news/6239
     */
    @GetMapping("/del_news/{newsId}")
    public ResponseResult delNewsById(@PathVariable Long newsId){
        return newsService.removeWmNewsById(newsId);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUpWmNews(@RequestBody WmNewsStatusDto dto){
        return newsService.downOrUpWmNews(dto);
    }
}