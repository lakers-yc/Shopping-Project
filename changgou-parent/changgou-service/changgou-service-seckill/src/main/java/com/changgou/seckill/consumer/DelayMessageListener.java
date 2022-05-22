package com.changgou.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import entity.SeckillStatus;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = "seckillQueue")
public class DelayMessageListener {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderService seckillOrderService;

    //配置监听类监听Queue2队列： 并根据支付状态处理订单
    @RabbitHandler
    public void accept(String msg) {
        //1.读取消息
        SeckillStatus seckillStatus = JSON.parseObject(msg, SeckillStatus.class);
        String username = seckillStatus.getUsername();

        //2.查看redis中是否有订单信息==> 如果有，说明没有支付 ===> 关闭微信支付，再关闭订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        if (seckillOrder != null){

            //判断如果支付失败 ==> 1.关闭微信的交易订单; 2.恢复库存 3.删除预订单:删除redis中的订单、清除重复排队标识、清除用户的抢单信息
            seckillOrderService.deleteOrder(username);
        }
    }
}
