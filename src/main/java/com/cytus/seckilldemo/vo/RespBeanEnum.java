package com.cytus.seckilldemo.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/*
* 公共返回对象枚举
* */
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    // 通用代码
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),
    // 登录模块
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    MOBILE_NOT_EXIT(500213,"手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214,"更新密码失败"),
    SESSION_ERROR(500215,"登录失效"),
    //秒杀
    EMPTY_STOCK(500500,"库存不足"),
    REPEAT_ERROR(500501,"商品限购一件"),
    REQUEST_ILLEGAL(500502,"请求非法,请重试"),
    ACCESS_LIMIT_REACHED(500504,"访问过于频繁，请稍后再试"),
    //订单模块5003
    ORDER_NOT_EXIST(500300,"订单信息不存在")
    ;
    private final Integer code;
    private final  String message;
}