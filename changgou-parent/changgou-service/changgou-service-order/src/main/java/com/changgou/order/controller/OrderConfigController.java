package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.pojo.OrderConfig;
import com.changgou.order.service.OrderConfigService;
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
@RequestMapping("/orderConfig")
@CrossOrigin
public class OrderConfigController extends AbstractCoreController<OrderConfig>{

    private OrderConfigService  orderConfigService;

    @Autowired
    public OrderConfigController(OrderConfigService  orderConfigService) {
        super(orderConfigService, OrderConfig.class);
        this.orderConfigService = orderConfigService;
    }
}
