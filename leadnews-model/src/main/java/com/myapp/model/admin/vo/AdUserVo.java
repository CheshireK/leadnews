package com.myapp.model.admin.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.myapp.model.admin.pojo.AdUser;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdUserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private AdUser user;

    private String token;
}
