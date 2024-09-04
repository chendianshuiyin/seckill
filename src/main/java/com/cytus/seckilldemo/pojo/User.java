package com.cytus.seckilldemo.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * Evangelion
 *
 * @author cytus
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID,手机号
     */
    private Long id;

    private String nickname;

    /**
     * 两次MD5加密，MD5（MD5（pass明文+固定salt）+salt）
     */
    private String password;

    private String slat;

    /**
     * 头像
     */
    private String heda;

    /**
     * 注册时间
     */
    private Date registerDate;

    /**
     * 最后一次登录时间
     */
    private Date lastLoginDate;

    /**
     * 登录次数
     */
    private Integer loginCount;


}
