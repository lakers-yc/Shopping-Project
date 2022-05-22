package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.pojo.ReturnOrder;
import com.changgou.order.service.ReturnOrderService;
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
@RequestMapping("/returnOrder")
@CrossOrigin
public class ReturnOrderController extends AbstractCoreController<ReturnOrder>{

    private ReturnOrderService  returnOrderService;

    @Autowired
    public ReturnOrderController(ReturnOrderService  returnOrderService) {
        super(returnOrderService, ReturnOrder.class);
        this.returnOrderService = returnOrderService;
    }
}
