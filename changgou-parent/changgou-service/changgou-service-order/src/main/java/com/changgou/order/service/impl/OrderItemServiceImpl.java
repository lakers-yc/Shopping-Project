package com.changgou.order.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:OrderItem业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OrderItemServiceImpl extends CoreServiceImpl<OrderItem> implements OrderItemService {

    private OrderItemMapper orderItemMapper;

    @Autowired
    public OrderItemServiceImpl(OrderItemMapper orderItemMapper) {
        super(orderItemMapper, OrderItem.class);
        this.orderItemMapper = orderItemMapper;
    }
}
