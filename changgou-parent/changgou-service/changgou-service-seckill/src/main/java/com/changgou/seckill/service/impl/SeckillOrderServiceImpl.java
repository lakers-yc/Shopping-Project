package com.changgou.seckill.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.task.MultiThreadingCreateOrder;
import entity.IdWorker;
import entity.SeckillStatus;
import entity.SystemConstants;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/****
 * @Author:admin
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<SeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedissonClient redissonClient;


    @Autowired
    public SeckillOrderServiceImpl(SeckillOrderMapper seckillOrderMapper) {
        super(seckillOrderMapper, SeckillOrder.class);
        this.seckillOrderMapper = seckillOrderMapper;
    }


    @Override
    public Boolean add(String time, Long id, String username) {

        //过滤掉 已经在排队的用户  1.1 先+1  再判断   hincrby  key field incrment
        Long increment = redisTemplate.boundHashOps("UserQueueCount").increment(username, 1);
        if(increment>1){
            throw new RuntimeException("已经在排队中了");
        }

        //过滤掉--->已有的订单的用户抢单
        Object order = redisTemplate.boundHashOps("SeckillOrder").get(username);
        if(order!=null){
            //自定义一个异常类
            throw new RuntimeException("有未支付的订单");
        }

        //1.创建排队对象，存入Redis队列（List---leftPush）
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(), 1, id, time);
        redisTemplate.boundListOps("SeckillOrderQueue").leftPush(seckillStatus);

        //2.存储用户的抢单的状态,  页面轮询发送请求获取该用户的抢单的状态 【SeckillStatus】
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).put(username, seckillStatus);

        //3.异步多线程下单（spring的方式：1.启用注解  2.创建一类 交给spring容器 编写一个方法（下单的方法） 3.方法上修饰一个注解@Async 4.调用该方法）
        multiThreadingCreateOrder.createOrder();

        return true;
    }

    /**
     * 前端轮询查看秒杀抢单状态查询
     * @param username
     * @return
     */
    @Override
    public SeckillStatus query(String username) {
        return (SeckillStatus) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).get(username);
    }

    /**
     * 删除订单信息 ===>如果支付失败，关闭微信的交易订单 恢复库存 删除预订单
     * @param username
     */
    @Override
    public void deleteOrder(String username) {
        //判断如果支付失败 ==> 1.关闭微信的交易订单; 2.恢复库存 3.删除预订单:删除redis中的订单、清除重复排队标识、清除用户的抢单信息
        //1. todo  关闭微信订单 使用Httpclient 发送请求 关闭订单

        //2. 恢复库存
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
        SeckillGoods seckillGood = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).get(seckillStatus.getGoodsId());
        //如果库存为空，先从数据库根据id查询赋值给seckillGood
        if (seckillGood == null){
            seckillGood = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
        }
        //上锁
        RLock orderLock = redissonClient.getLock("rollback"+seckillStatus.getGoodsId());
        try {
            orderLock.lock(5, TimeUnit.SECONDS);
            seckillGood.setStockCount(seckillGood.getStockCount() + 1);
            redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).put(seckillStatus.getGoodsId(),seckillGood);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁
            orderLock.unlock();
        }

        //3. 删除redis中的订单;清除重复排队标识;清除用户的抢单信息
        redisTemplate.boundHashOps("SeckillOrder").delete(username);
        redisTemplate.boundHashOps("UserQueueCount").delete(username);
        redisTemplate.boundHashOps("UserQueueStatus").delete(username);
    }
}
