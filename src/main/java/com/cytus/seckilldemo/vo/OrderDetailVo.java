package com.cytus.seckilldemo.vo;

/*
* 秒杀订单返回对象
* */

import com.cytus.seckilldemo.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private Order order;
    private GoodsVo goodsVo;
}
