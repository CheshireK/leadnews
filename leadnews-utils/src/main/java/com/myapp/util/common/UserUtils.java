package com.myapp.util.common;

import com.myapp.model.user.pojo.ApUser;
import com.sun.istack.internal.NotNull;
import org.springframework.util.DigestUtils;

import java.util.Objects;

public class UserUtils {
    public static boolean validate(@NotNull ApUser user,@NotNull String password){
        String salt = user.getSalt();
        password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        return Objects.equals(user.getPassword(), password);
    }

    /**
     * 验证密码
     * @param plainPwd 用户提交的密码
     * @param dbPwd 数据库中的用户密码
     * @param salt 加密
     * @return 密码是否正确
     */
    public static boolean validate(String plainPwd, String dbPwd, String salt){
        String decPwd = DigestUtils.md5DigestAsHex((plainPwd + salt).getBytes());
        return Objects.equals(dbPwd, decPwd);
    }
}
