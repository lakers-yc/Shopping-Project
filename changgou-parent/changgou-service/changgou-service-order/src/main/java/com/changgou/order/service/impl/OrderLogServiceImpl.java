package com.changgou.order.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.order.dao.OrderLogMapper;
import com.changgou.order.pojo.OrderLog;
import com.changgou.order.service.OrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:OrderLog业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OrderLogServiceImpl extends CoreServiceImpl<OrderLog> implements OrderLogService {

    private OrderLogMapper orderLogMapper;

    @Autowired
    public OrderLogServiceImpl(OrderLogMapper orderLogMapper) {
        super(orderLogMapper, OrderLog.class);
        this.orderLogMapper = orderLogMapper;
    }
}
