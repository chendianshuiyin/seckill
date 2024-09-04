package com.cytus.seckilldemo.vo;


import com.cytus.seckilldemo.validator.isMobile;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/*
* 登录参数
* */
@Data
public class LoginVo {
    @NotNull
    @isMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
