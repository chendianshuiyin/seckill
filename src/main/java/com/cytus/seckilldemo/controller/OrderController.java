package com.cytus.seckilldemo.controller;


import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.service.IOrderService;
import com.cytus.seckilldemo.vo.OrderDetailVo;
import com.cytus.seckilldemo.vo.RespBean;
import com.cytus.seckilldemo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;
    /*
     *订单详情
    */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, @RequestParam("orderId") Long orderId)
    {
        if(user == null)
        {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detailVo =  orderService.detail(orderId);
        return RespBean.success(detailVo);
    }
}
