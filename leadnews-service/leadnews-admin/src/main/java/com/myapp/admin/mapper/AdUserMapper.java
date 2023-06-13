package com.myapp.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myapp.model.admin.pojo.AdUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 管理员用户信息表 Mapper 接口
 * </p>
 *
 * @author luosa
 * @since 2023-06-12
 */
@Mapper
public interface AdUserMapper extends BaseMapper<AdUser> {

}
