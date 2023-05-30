package com.myapp.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myapp.model.user.pojo.ApUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApUserMapper extends BaseMapper<ApUser> {

}
