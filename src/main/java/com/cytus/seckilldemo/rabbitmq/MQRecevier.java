package com.cytus.seckilldemo.rabbitmq;

import com.cytus.seckilldemo.pojo.SeckillMessage;
import com.cytus.seckilldemo.pojo.SeckillOrder;
import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.service.IGoodsService;
import com.cytus.seckilldemo.service.IOrderService;
import com.cytus.seckilldemo.utils.JsonUtil;
import com.cytus.seckilldemo.vo.GoodsVo;
import com.cytus.seckilldemo.vo.RespBean;
import com.cytus.seckilldemo.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
* 消息消费者
* */
@Service
@Slf4j
public class MQRecevier {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

//    @RabbitListener(queues = "queue")
//    public void receive(Object msg)
//    {
//        log.info("接收消息："+msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg)
//    {
//        log.info("QUEUE01接收消息："+msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg)
//    {
//        log.info("QUEUE02接收消息："+msg);
//    }
//
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg)
//    {
//        log.info("QUEUE01接收消息："+msg);
//    }
//
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg)
//    {
//        log.info("QUEUE02接收消息："+msg);
//    }

    /*
    * 消费者处理秒杀任务
    * */
    @RabbitListener(queues = "seckillQueue")
    public void receiver(String message)
    {
        log.info("接收的消息："+message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message,SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount()<1)
        {
            // 库存不足
            return;
        }
        // 判断是否重复抢购
        // 判断是否重复抢购，从redis获取数据
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder != null)
        {
            // 重复抢购
            return;
        }
        // 下单操作
        orderService.seckill(user,goodsVo);
    }
}
