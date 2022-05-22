package com.changgou.seckill.service;

import com.changgou.core.service.CoreService;
import com.changgou.seckill.pojo.SeckillOrder;
import entity.SeckillStatus;

/****
 * @Author:admin
 * @Description:SeckillOrder业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillOrderService extends CoreService<SeckillOrder> {

    Boolean add(String time, Long id, String username);

    SeckillStatus query(String username);

    //删除订单信息 ===>如果支付失败，关闭微信的交易订单 恢复库存 删除预订单
    void deleteOrder(String username);
}
