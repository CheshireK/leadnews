package com.myapp.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myapp.model.wemedia.pojo.WmNews;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WmNewsMapper  extends BaseMapper<WmNews> {
    
}