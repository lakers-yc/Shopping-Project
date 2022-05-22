package com.changgou.seckill.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.service.SeckillGoodsService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/****
 * @Author:admin
 * @Description:SeckillGoods业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillGoodsServiceImpl extends CoreServiceImpl<SeckillGoods> implements SeckillGoodsService {

    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    public SeckillGoodsServiceImpl(SeckillGoodsMapper seckillGoodsMapper) {
        super(seckillGoodsMapper, SeckillGoods.class);
        this.seckillGoodsMapper = seckillGoodsMapper;
    }

    @Override
    public List<SeckillGoods> list(String time) {
        // Redis中根据Key获取秒杀商品列表
        return redisTemplate.boundHashOps("SeckillGoods_"+time).values();
    }

    @Override
    public SeckillGoods one(String time, Long id) {
        //从redis中获取某一个时间段对应的某一个商品的ID 对应的商品的数据
        return (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_"+time).get(id);
    }
}
