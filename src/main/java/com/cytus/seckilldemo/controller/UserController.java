package com.cytus.seckilldemo.controller;


import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.rabbitmq.MQSender;
import com.cytus.seckilldemo.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * Evangelion
 *
 * @author cytus
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    /*
     *用户信息，专门用来测试
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user)
    {
        return  RespBean.success(user);
    }


//    /*
//    * 测试发送mq消息
//    * */
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq()
//    {
//        mqSender.send("Hello");
//    }
//
//    /*
//    * fanout模式
//    * */
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void mq01()
//    {
//        mqSender.send("Hello");
//    }
//
//    /*
//    * mqdirect模式
//    * */
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void mq02()
//    {
//        mqSender.send01("Hello,Red");
//    }
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void mq03()
//    {
//        mqSender.send02("Hello,Green");
//    }
}
