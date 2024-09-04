package com.cytus.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cytus.seckilldemo.pojo.Goods;
import com.cytus.seckilldemo.vo.GoodsVo;

import java.util.List;

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
public interface IGoodsService extends IService<Goods> {

    /*
    * 获取秒杀商品列表
    * */
    List<GoodsVo> findGoodsVo();

    /*
    * 获取商品详情
    * */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
