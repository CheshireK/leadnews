package com.myapp.search.controller;

import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.search.dto.UserSearchDto;
import com.myapp.search.service.ApAssociateWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/associate")
public class SearchAssociateWordsController {
    @Autowired
    private ApAssociateWordService apAssociateWordService;

    /**
     * 加载用户搜索联想词
     * @param searchDto
     * @return
     */
    @PostMapping("/search")
    public ResponseResult search(@RequestBody UserSearchDto searchDto){
        return apAssociateWordService.listAssociateWord(searchDto);
    }
}
