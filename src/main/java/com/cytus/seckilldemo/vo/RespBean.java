package com.cytus.seckilldemo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 公共返回对象
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object obj;

    /*
    * 成功返回结果
    * */
    public static RespBean success()
    {
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);
    }
    /*
    * 重载
    * */
    public  static RespBean success(Object obj)
    {
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), obj);
    }

    /*
    * 失败返回结果
    * 因为失败的返回码有多种情况，获取实际的返回码和消息
    * */
    public static RespBean error(RespBeanEnum respBeanEnum)
    {
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), null);
    }

    /*
    * 失败重载
    * */
    public static RespBean error(RespBeanEnum respBeanEnum, Object obj)
    {
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), obj);
    }
}
