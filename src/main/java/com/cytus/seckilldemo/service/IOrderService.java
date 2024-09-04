package com.cytus.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cytus.seckilldemo.pojo.Order;
import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.vo.GoodsVo;
import com.cytus.seckilldemo.vo.OrderDetailVo;

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
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goodsVo);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId,String path);
}
