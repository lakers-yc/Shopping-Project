package com.changgou.seckill.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 配置延时队列1.创建一个过期队列（Queue1） 2.接收消息的队列（ Queue2） 3.交换机
 * 1.Queue1：延时队列
 * 2.Queue2：真正监听消息的队列
 * 3.创建交换机
 */
@Component
public class QueueConfig {
    //1.Queue1: 延时队列
    @Bean
    public Queue delayQueue(){
        return QueueBuilder.durable("delayQueue")
                .withArgument("x-dead-letter-exchange","exchange")  // 消息超时进入死信队列，绑定死信队列交换机
                .withArgument("x-dead-letter-routing-key","seckillQueue")  // 绑定指定的routing-key
                .build();
    }

    //2.Queue2: 真正监听消息的队列
    @Bean
    public Queue seckillQueue(){
        return new Queue("seckillQueue",true);
    }

    //3.exchange: 交换机
    public DirectExchange exchange(){
        return new DirectExchange("exchange");
    }

    //交换机和队列绑定
    public Binding basicBinding(Queue seckillQueue,DirectExchange exchange){
        return BindingBuilder.bind(seckillQueue)
                .to(exchange)
                .with("seckillQueue");  //路由
    }
}
