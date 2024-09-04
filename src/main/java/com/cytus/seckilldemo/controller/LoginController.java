package com.cytus.seckilldemo.controller;

import com.cytus.seckilldemo.service.IUserService;
import com.cytus.seckilldemo.vo.LoginVo;
import com.cytus.seckilldemo.vo.RespBean;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/login")
@Slf4j //打印日志
public class LoginController {

    @Autowired
    private IUserService userService;

    @RequestMapping("/toLogin")
    public String toLogin()
    {
        // 页面跳转
        return "login";
    }

    @RequestMapping("/dologin")
    @ResponseBody //正常返回需要加这个，页面跳转不用加
    public RespBean dologin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response)
    {
        //正常返回
        //log.info("{}",loginVo); // 因为加了Sl4j注解因此可以用
        return userService.doLogin(loginVo, request, response);
    }
}
