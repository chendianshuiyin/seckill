package com.cytus.seckilldemo.config;

import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.service.IUserService;
import com.cytus.seckilldemo.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

/*
* 自定义用户参数
* */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private IUserService userService;
    @Override
    public boolean supportsParameter(MethodParameter parameter)
    {
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer macContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception
    {
        /*HttpServletRequest request =  webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(ticket)) {
            return null;
        }
        return  userService.getUserByCookie(ticket,request,response);*/
        return UserContext.getUser();
    }
}
