package com.cytus.seckilldemo.validator;

import com.cytus.seckilldemo.vo.IsMobileValidator;

import java.lang.annotation.Target;

/*
* 验证手机号自定义注解
* */
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.TYPE_USE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
@jakarta.validation.Constraint(validatedBy = {IsMobileValidator.class}) // 校验规则类
public @interface isMobile {
    boolean required() default true;
    java.lang.String message() default "手机号码格式错误";

    java.lang.Class<?>[] groups() default {};

    java.lang.Class<? extends jakarta.validation.Payload>[] payload() default {};
}
