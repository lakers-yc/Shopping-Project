package com.changgou.seckill.dao;
import com.changgou.seckill.pojo.SeckillGoods;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:admin
 * @Description:SeckillGoodsDao
 * @Date 2019/6/14 0:12
 *****/
@Repository
public interface SeckillGoodsMapper extends Mapper<SeckillGoods> {
}
