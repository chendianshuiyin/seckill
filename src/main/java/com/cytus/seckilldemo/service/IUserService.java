package com.cytus.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.vo.LoginVo;
import com.cytus.seckilldemo.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * Evangelion
 *
 * @author cytus
 *
 */
public interface IUserService extends IService<User> {
    /*
    * 登录
    * */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
    /*根据Cookie获取用户
    * */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    RespBean updatePassword(String userTicket, String password,HttpServletRequest request, HttpServletResponse response);
}
