package com.myapp.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class LoginResponseDto implements Serializable {
    private String token;
}
