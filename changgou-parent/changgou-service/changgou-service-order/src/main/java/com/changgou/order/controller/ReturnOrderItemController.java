package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.pojo.ReturnOrderItem;
import com.changgou.order.service.ReturnOrderItemService;
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
@RequestMapping("/returnOrderItem")
@CrossOrigin
public class ReturnOrderItemController extends AbstractCoreController<ReturnOrderItem>{

    private ReturnOrderItemService  returnOrderItemService;

    @Autowired
    public ReturnOrderItemController(ReturnOrderItemService  returnOrderItemService) {
        super(returnOrderItemService, ReturnOrderItem.class);
        this.returnOrderItemService = returnOrderItemService;
    }
}
