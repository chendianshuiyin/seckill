package com.cytus.seckilldemo.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)// 注解是运行时的
@Target(ElementType.METHOD) // 注解放在方法上
public @interface AccessLimit {
    int second();
    int maxCount();
    boolean needLogin() default true;

}
