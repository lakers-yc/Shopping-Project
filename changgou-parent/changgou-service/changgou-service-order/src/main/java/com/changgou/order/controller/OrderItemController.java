package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/orderItem")
@CrossOrigin
public class OrderItemController extends AbstractCoreController<OrderItem>{

    private OrderItemService  orderItemService;

    @Autowired
    public OrderItemController(OrderItemService  orderItemService) {
        super(orderItemService, OrderItem.class);
        this.orderItemService = orderItemService;
    }
}
