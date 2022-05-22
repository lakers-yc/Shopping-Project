package com.changgou;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //排除数据库相关
@EnableEurekaClient
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class,args);
    }

    @Autowired
    private Environment environment;

    /*===================普通订单的队列===================*/
    //创建队列
    @Bean
    public Queue queueOrder(){
        return new Queue(environment.getProperty("mq.pay.queue.order"));
    }
    //创建交互机 路由模式的交换机
    @Bean
    public DirectExchange createExchange(){
        return new DirectExchange(environment.getProperty("mq.pay.exchange.order"));
    }
    //创建绑定
    @Bean
    public Binding createBinding(){
        return BindingBuilder.bind(queueOrder()).to(createExchange()).with(environment.getProperty("mq.pay.routing.key"));
    }

    /*===================秒杀订单的队列===================*/
    //配置创建队列
    @Bean
    public Queue createSekillQueue(){
        // queue.order
        return new Queue(environment.getProperty("mq.pay.queue.seckillorder"));
    }

    //创建交换机
    @Bean
    public DirectExchange createSeckillExchange(){
        // exchange.order
        return new DirectExchange(environment.getProperty("mq.pay.exchange.seckillorder"));
    }
    // 绑定队列到交换机
    @Bean
    public Binding seckillbinding(){
        // routing key : queue.order
        String property = environment.getProperty("mq.pay.routing.seckillkey");
        return BindingBuilder.bind(createSekillQueue()).to(createSeckillExchange()).with(property);
    }

}
