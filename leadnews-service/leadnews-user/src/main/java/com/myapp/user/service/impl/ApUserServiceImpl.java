package com.myapp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.user.dto.LoginDto;
import com.myapp.model.user.dto.LoginResponseDto;
import com.myapp.model.user.pojo.ApUser;
import com.myapp.user.mapper.ApUserMapper;
import com.myapp.user.service.ApUserService;
import com.myapp.util.common.AppJwtUtil;
import com.myapp.util.common.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.myapp.model.common.constant.AppConstant.GUEST_ID;

@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    @Override
    public ResponseResult login(LoginDto loginDto) {
        // 游客登录
        if (StringUtils.isEmpty(loginDto.getPhone()) || StringUtils.isEmpty(loginDto.getPassword())){
            LoginResponseDto responseDto = new LoginResponseDto(AppJwtUtil.getToken(GUEST_ID));
            return ResponseResult.okResult(responseDto);
        }

        // 数据库查询用户，通过手机号
        ApUser user = this.getOne(
                new LambdaQueryWrapper<ApUser>()
                        .eq(ApUser::getPhone, loginDto.getPhone()));

        // 没有查到用户 或者 密码错误
        if(user==null || !UserUtils.validate(user, loginDto.getPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户名或者密码错误");
        }

        return ResponseResult.okResult(new LoginResponseDto(AppJwtUtil.getToken(user.getId().longValue())));

    }
}
