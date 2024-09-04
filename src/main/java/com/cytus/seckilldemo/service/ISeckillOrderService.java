package com.cytus.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cytus.seckilldemo.pojo.SeckillOrder;
import com.cytus.seckilldemo.pojo.User;

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
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
