package com.changgou.seckill.task;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import entity.IdWorker;
import entity.SeckillStatus;
import entity.SystemConstants;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Async//异步执行，底层是多线程方式
    public void createOrder() {
        System.out.println("准备睡会再下单");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //从Redis队列（List---rightPop）里面获取用户信息
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();
        if (seckillStatus == null){
            return;
        }
        String time = seckillStatus.getTime();
        String username = seckillStatus.getUsername();
        Long id = seckillStatus.getGoodsId();

        //创建分布式锁
        RLock lock = redissonClient.getLock("Mylock" + id);
        SeckillOrder order = null;

        try {
            //===============================上锁==========================================
            lock.lock(5, TimeUnit.SECONDS);
            //1.根据商品的id获取秒杀商品数据
            SeckillGoods seckillGood = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(id);
            if (seckillGood == null || seckillGood.getStockCount() <= 0) {
                throw new RuntimeException("已经没有库存");
            }
            //2.创建订单对象(SeckillOrder)
            order = new SeckillOrder();
            order.setId(idWorker.nextId());
            order.setSeckillId(id);
            order.setMoney(seckillGood.getCostPrice());
            order.setUserId(username);
            order.setCreateTime(new Date());
            order.setStatus("0");
            //3.将订单对象存入Redis
            redisTemplate.boundHashOps("SeckillOrder").put(username, order);
            //4.创建订单成功--->减少库存
            seckillGood.setStockCount(seckillGood.getStockCount() - 1);
            //5.然后判断当前商品是否还有库存(商品是最后一个)
            if (seckillGood.getStockCount() <= 0) {
                //如果库存为0 ---> 同步数据到mysql，然后删除Redis缓存中该商品
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGood);
                redisTemplate.boundHashOps("SeckillGoods_" + time).delete(id);
            } else {
                //如果有库存  ---> 则将数据重置到Reids中
                redisTemplate.boundHashOps("SeckillGoods_" + time).put(id, seckillGood);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            //没抢到锁，返回
            return;
        } finally {
            //===============================释放锁==========================================
            lock.unlock();
        }

        //最后：抢单成功，更新抢单状态【SeckillStatus】,排队->等待支付
        seckillStatus.setStatus(2);
        seckillStatus.setOrderId(order.getId());//订单号
        seckillStatus.setMoney(Float.valueOf(order.getMoney()));//金额
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).put(username,seckillStatus);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        System.out.println("下单时间："+simpleDateFormat.format(new Date()));

        //MQ发送延时消息 ==> 发送消息给延时队列Queue1:设置半个小时后过期，过期后将信息发送给队列
        rabbitTemplate.convertAndSend("delayQueue", (Object) JSON.toJSONString(seckillStatus), new MessagePostProcessor() {
        //rabbitTemplate.convertAndSend("delayQueue", (Object) username, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("10000000");
                return null;
            }
        });


        System.out.println("下单完成！ ");
    }
}
