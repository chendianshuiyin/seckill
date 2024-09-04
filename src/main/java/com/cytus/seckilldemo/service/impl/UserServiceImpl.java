package com.cytus.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cytus.seckilldemo.exception.GlobalException;
import com.cytus.seckilldemo.mapper.UserMapper;
import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.service.IUserService;
import com.cytus.seckilldemo.utils.CookieUtil;
import com.cytus.seckilldemo.utils.MD5Util;
import com.cytus.seckilldemo.utils.UUIDUtil;
import com.cytus.seckilldemo.utils.ValidatorUtil;
import com.cytus.seckilldemo.vo.LoginVo;
import com.cytus.seckilldemo.vo.RespBean;
import com.cytus.seckilldemo.vo.RespBeanEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * Evangelion
 *
 * @author cytus
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /*
    * 登录
    * */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        // 参数校验
//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password))
//        {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        if (!ValidatorUtil.isMobile(mobile))
//        {
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        // 数据库查询
        User user = userMapper.selectById(mobile);
        // 用户是否存在
        if (null == user)
        {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            // 换成异常
            throw  new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 用户密码是否正确
        if (!MD5Util.DBPassFromPass(password,user.getSlat()).equals(user.getPassword()))
        {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 登录成功后
        // 生成cookie,存入UUID
        String ticket = UUIDUtil.uudi();
        //request.getSession().setAttribute(ticket,user);
        // 将用户信息存入存入redis
        redisTemplate.opsForValue().set("user:"+ticket,user);
        CookieUtil.setCookie(request,response,"userTicket", ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket))
        {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:"+userTicket);
        if(user !=null)
        {
            CookieUtil.setCookie(request,response,"userTicket", userTicket);
        }
        return user;
    }

    /*
    * 更新用户密码
    * */
    @Override
    public RespBean updatePassword(String userTicket,String password,HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket,request,response);
        if(user == null)
        {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIT);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password,user.getSlat()));
        int result = userMapper.updateById(user);
        if(1==result)
        {
            redisTemplate.delete("user:"+userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
