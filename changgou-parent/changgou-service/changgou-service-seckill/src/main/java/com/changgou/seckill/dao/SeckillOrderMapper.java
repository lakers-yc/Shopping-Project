package com.changgou.seckill.dao;
import com.changgou.seckill.pojo.SeckillOrder;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:admin
 * @Description:SeckillOrderDao
 * @Date 2019/6/14 0:12
 *****/

@Repository
public interface SeckillOrderMapper extends Mapper<SeckillOrder> {
}
