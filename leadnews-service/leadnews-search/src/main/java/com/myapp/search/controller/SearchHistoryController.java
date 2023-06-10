package com.myapp.search.controller;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.search.dto.HistorySearchDto;
import com.myapp.search.service.ApUserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history")
public class SearchHistoryController {

    @Autowired
    private ApUserSearchService apUserSearchService;
    /**
     * 加载用户搜索历史
     * 按创建时间的降序排列
     * @return 用户搜索历史列表
     */
    @PostMapping("/load")
    public ResponseResult loadUserSearchHistory(){
        return apUserSearchService.loadUserSearchHistory();
    }

    /**
     * 删除用户搜索历史
     * @param searchDto 用户搜索历史
     * @return
     */
    @PostMapping("/del")
    public ResponseResult deleteUserSearchHistory(@RequestBody HistorySearchDto searchDto){
        return apUserSearchService.deleteUserSearchHistory(searchDto);
    }
}
