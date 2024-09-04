package com.cytus.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cytus.seckilldemo.exception.GlobalException;
import com.cytus.seckilldemo.mapper.OrderMapper;
import com.cytus.seckilldemo.pojo.Order;
import com.cytus.seckilldemo.pojo.SeckillGoods;
import com.cytus.seckilldemo.pojo.SeckillOrder;
import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.service.IGoodsService;
import com.cytus.seckilldemo.service.IOrderService;
import com.cytus.seckilldemo.service.ISeckillGoodsService;
import com.cytus.seckilldemo.service.ISeckillOrderService;
import com.cytus.seckilldemo.utils.MD5Util;
import com.cytus.seckilldemo.utils.UUIDUtil;
import com.cytus.seckilldemo.vo.GoodsVo;
import com.cytus.seckilldemo.vo.OrderDetailVo;
import com.cytus.seckilldemo.vo.RespBean;
import com.cytus.seckilldemo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    /*
    * 秒杀
    * */
    @Transactional // 事务注解
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 秒杀商品表减少库存
        SeckillGoods seckillGoods =  seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id",goodsVo.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        // 在设置库存时判断原来的库存结果是否合理
        boolean seckillGoodsResult =  seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = "+"stock_count-1")
                .eq("goods_id",goodsVo.getId()).gt("stock_count",0));
        if(seckillGoods.getStockCount()<1)
        {
            // 判断是否还有库存
            valueOperations.set("isStockEmpty:"+goodsVo.getId(),"0");
            return null;
        }

        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order); // 为什么这里直接用orderMapper插入而不是用orderservice接口保存
        // 当然service层调用Mapper是合理的
        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrderService.save(seckillOrder); // 这里又用了service接口
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goodsVo.getId(),seckillOrder);

        return order;
    }

    /*
    * 返回订单详情对象
    * */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId == null)
        {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setOrder(order);
        detailVo.setGoodsVo(goodsVo);
        return detailVo;
    }

    /*
    * 根据用户和秒杀的商品生成秒杀接口地址
    * */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uudi()+"123456");
        // 存在redis中
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    /*
    * 校验秒杀地址
    * */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if(user==null||goodsId<0|| StringUtils.isEmpty(path))
        {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:"+user.getId()+":"+goodsId);
        return path.equals(redisPath);
    }
}
