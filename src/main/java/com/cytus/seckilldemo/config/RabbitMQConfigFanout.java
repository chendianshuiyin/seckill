/*
package com.cytus.seckilldemo.config;

*/
/*
* RabbtMQ配置类
* *//*


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfigFanout {

    private static final String QUEUE01 = "queue_fanout01";
    private static final String QUEUE02 = "queue_fanout02";
    private static final String EXCHANGE = "fanoutExchange";

    */
/*
    * 添加一个队列，流转消息
    * *//*

    @Bean
    public Queue queue()
    {
        // 消息和队列都持久化才能持久化
        return new Queue("queue",true);
    }

    @Bean
    public Queue queue01()
    {
        return  new Queue(QUEUE01);
    }

    @Bean
    public Queue queue02()
    {
        return new Queue(QUEUE02);
    }

    @Bean
    public FanoutExchange fanoutExchange()
    {
        return  new FanoutExchange(EXCHANGE);
    }

    @Bean
    public Binding binding01()
    {
        return BindingBuilder.bind(queue01()).to(fanoutExchange());
    }

    @Bean
    public Binding binding02()
    {
        return BindingBuilder.bind(queue02()).to(fanoutExchange());
    }
}
*/
