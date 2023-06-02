package com.myapp.model.wemedia.dto;

import com.myapp.model.wemedia.pojo.WmUser;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class WmLoginResponseDto implements Serializable {
    private String token;
    private WmUser user;
}
