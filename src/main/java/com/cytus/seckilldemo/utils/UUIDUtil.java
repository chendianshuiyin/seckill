package com.cytus.seckilldemo.utils;


import java.util.UUID;

/*
* 生成UUID
* */
public class UUIDUtil {
    public static String uudi()
    {
        return UUID.randomUUID().toString().replace("-","");
    }
}
