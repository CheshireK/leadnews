package com.myapp.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.admin.mapper.AdUserMapper;
import com.myapp.admin.service.AdUserService;
import com.myapp.model.admin.dto.AdUserDto;
import com.myapp.model.admin.pojo.AdUser;
import com.myapp.model.admin.vo.AdUserVo;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.util.common.AppJwtUtil;
import com.myapp.util.common.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * <p>
 * 管理员用户信息表 服务实现类
 * </p>
 *
 * @author luosa
 * @since 2023-06-12
 */
@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {

    /**
     * 用户登录
     *
     * @param adUserDto 用户信息
     * @return 成功响应
     */
    @Override
    public ResponseResult login(AdUserDto adUserDto) {
        if (adUserDto == null
                || StringUtils.isEmpty(adUserDto.getName())
                || StringUtils.isEmpty(adUserDto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "登录信息不能为空");
        }

        AdUser user = this.getOne(new LambdaQueryWrapper<AdUser>().eq(AdUser::getName, adUserDto.getName()));
        // 检查账户状态
        if (user.getStatus().equals(1)
                || user.getStatus().equals(0)
                || !user.getStatus().equals(9)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH, "账户被禁用");
        }
        // 检查密码
        if (!UserUtils.validate(adUserDto.getPassword(), user.getPassword(), user.getSalt())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        // 获取token
        String token = AppJwtUtil.getToken(user.getId().longValue());

        user.setSalt(null);
        user.setPassword(null);
        AdUserVo adUserVo = new AdUserVo();
        adUserVo.setUser(user);

        adUserVo.setToken(token);
        return ResponseResult.okResult(adUserVo);
    }
}
