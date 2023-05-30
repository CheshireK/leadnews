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
}
