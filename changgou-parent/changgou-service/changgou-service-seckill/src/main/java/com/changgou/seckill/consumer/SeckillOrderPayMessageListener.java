package com.changgou.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import entity.SeckillStatus;
import entity.SystemConstants;
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
import java.util.concurrent.locks.Lock;

@Component
@RabbitListener(queues = "queue.seckillorder")
public class SeckillOrderPayMessageListener {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillOrderService seckillOrderService;

    //用于监听消息，并根据支付状态处理订单
    @RabbitHandler
    public void accept(String msg) {
        //1. 接收消息本身 转成MAP对象
        Map<String, String> map = JSON.parseObject(msg, Map.class);

        if (map.get("return_code").equals("SUCCESS")) {
            //map里面有attach，attach里面username
            Map<String, String> attachMap = JSON.parseObject(map.get("attach"), Map.class);
            String username = attachMap.get("username");

            //2.判断如果支付成功  将预订单从redis中 存储到Mysql
            if (map.get("result_code").equals("SUCCESS")) {
                //2.1 从redis中获取订单
                SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
                //2.2 增加补充mysql中相关信息(修改支付状态、支付时间、流水号等)
                seckillOrder.setStatus("1");
                String time_end = map.get("time_end");
                seckillOrder.setTransactionId(map.get("transaction_id"));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date paytime = simpleDateFormat.parse(time_end);
                    seckillOrder.setPayTime(paytime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //2.3 存储到mysql
                seckillOrderMapper.insertSelective(seckillOrder);
                //2... 删除redis中的订单;清除重复排队标识;清除用户的抢单信息
                redisTemplate.boundHashOps("SeckillOrder").delete(username);
                redisTemplate.boundHashOps("UserQueueCount").delete(username);
                redisTemplate.boundHashOps("UserQueueStatus").delete(username);


            } else {
                //3.判断如果支付失败 ==> 1.关闭微信的交易订单; 2.恢复库存 3.删除预订单:删除redis中的订单、清除重复排队标识、清除用户的抢单信息
                seckillOrderService.deleteOrder(username);
            }
        } else {
            System.out.println("通信失败");
        }
    }
}
