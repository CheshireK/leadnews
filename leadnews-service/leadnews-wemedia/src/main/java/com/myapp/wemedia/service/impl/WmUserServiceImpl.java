package com.myapp.wemedia.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.wemedia.dto.WmLoginDto;
import com.myapp.model.wemedia.dto.WmLoginResponseDto;
import com.myapp.model.wemedia.pojo.WmUser;
import com.myapp.util.common.AppJwtUtil;
import com.myapp.wemedia.mapper.WmUserMapper;
import com.myapp.wemedia.service.WmUserService;
import com.sun.istack.internal.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {
    @Override
    public ResponseResult login(WmLoginDto dto) {
        // 游客登录
        if (StringUtils.isEmpty(dto.getName()) || StringUtils.isEmpty(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "用户名或密码为空");
        }

        // 数据库查询用户，通过手机号
        WmUser user = baseMapper.selectOne(new LambdaQueryWrapper<WmUser>().eq(WmUser::getName, dto.getName()));

        // 没有查到用户 或者 密码错误
        if (user == null || !validate(user, dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户名或者密码错误");
        }
        user.setPassword("");
        user.setSalt("");
        WmLoginResponseDto loginResponseDto = new WmLoginResponseDto(AppJwtUtil.getToken(user.getId().longValue()), user);
        return ResponseResult.okResult(loginResponseDto);

    }

    private boolean validate(@NotNull WmUser user, @NotNull String password) {
        String salt = user.getSalt();
        password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        return Objects.equals(user.getPassword(), password);
    }
}
