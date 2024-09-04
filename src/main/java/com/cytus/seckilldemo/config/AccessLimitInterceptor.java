package com.cytus.seckilldemo.config;

import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.service.IUserService;
import com.cytus.seckilldemo.utils.CookieUtil;
import com.cytus.seckilldemo.vo.RespBean;
import com.cytus.seckilldemo.vo.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/*
* 限流的拦截器
* */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    /*
    * 实现preHandle方法
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if(handler instanceof HandlerMethod)
        {
            // 获取用户
            User user = getUser(request,response);
            UserContext.setUser(user);
            // 是一个HandlerMethod,在方法上的处理
            HandlerMethod hm = (HandlerMethod) handler;
            // 转换成AccessLimit注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null)
            {
                // 没有注解则为true
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin)
            {
                //需要登录
                if(user==null)
                {
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                key+=":"+user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count  = (Integer) valueOperations.get(key);
            if(count==null)
            {
                valueOperations.set(key,1,second, TimeUnit.SECONDS);
            } else if (count<maxCount) {
                valueOperations.increment(key);
            }else
            {
                render(response,RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    /*
    * 构建返回对象
    * */
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean error = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(error));
        out.flush();
        out.close();
    }

    /*
    * 获取当前登录用户
    * */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(ticket)) {
            return null;
        }
        return  userService.getUserByCookie(ticket,request,response);
    }
}
