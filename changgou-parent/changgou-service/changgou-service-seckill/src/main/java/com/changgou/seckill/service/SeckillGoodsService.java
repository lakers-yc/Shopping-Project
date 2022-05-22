package com.changgou.seckill.service;

import com.changgou.core.service.CoreService;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.Result;

import java.util.List;

/****
 * @Author:admin
 * @Description:SeckillGoods业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillGoodsService extends CoreService<SeckillGoods> {

    List<SeckillGoods> list(String time);

    SeckillGoods one(String time, Long id);

}
