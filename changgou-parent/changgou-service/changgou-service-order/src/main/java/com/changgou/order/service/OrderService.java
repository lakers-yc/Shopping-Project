package com.changgou.order.service;

import com.changgou.core.service.CoreService;
import com.changgou.order.pojo.Order;

/****
 * @Author:admin
 * @Description:Order业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface OrderService extends CoreService<Order> {


    Order add(Order order);
}
