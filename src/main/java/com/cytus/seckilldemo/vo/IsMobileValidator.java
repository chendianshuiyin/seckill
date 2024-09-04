package com.cytus.seckilldemo.vo;

import com.cytus.seckilldemo.utils.ValidatorUtil;
import com.cytus.seckilldemo.validator.isMobile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.thymeleaf.util.StringUtils;

/*
* 手机号码校验规则
* */
public class IsMobileValidator implements ConstraintValidator<isMobile,String> {

    private boolean required = false;
    @Override
    public void initialize(isMobile constrainAnnotation)
    {
        required = constrainAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context)
    {
        if(required)
        {
            // 必填则直接返回校验结果
            return ValidatorUtil.isMobile(value);
        }else
        {
            if(StringUtils.isEmpty(value))
            {
                // 非必填，为空即正确
                return true;
            }else{
                // 不为空则需要校验
                return ValidatorUtil.isMobile(value);
            }

        }
    }
}
