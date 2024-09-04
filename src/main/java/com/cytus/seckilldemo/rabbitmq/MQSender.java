package com.cytus.seckilldemo.rabbitmq;


/*
* 消息发送者
* */

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /*
    * 发消息
    * */
//    public void send(Object msg)
//    {
//        log.info("发消息："+msg);
//        rabbitTemplate.convertAndSend("fanoutExchange","",msg);
//    }
//
//    public void send01(Object msg)
//    {
//        log.info("发送红色消息："+msg);
//        rabbitTemplate.convertAndSend("directExchange","queue.red",msg);
//    }
//
//    public void send02(Object msg)
//    {
//        log.info("发送绿色消息："+msg);
//        rabbitTemplate.convertAndSend("directExchange","queue.green",msg);
//    }

    /*
    * 发送秒杀订单消息
    * */
    public void sendSeckillMessage(String message)
    {
        log.info("发送消息："+message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",message);
    }
}
