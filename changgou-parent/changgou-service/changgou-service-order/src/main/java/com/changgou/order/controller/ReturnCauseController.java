package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.pojo.ReturnCause;
import com.changgou.order.service.ReturnCauseService;
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
@RequestMapping("/returnCause")
@CrossOrigin
public class ReturnCauseController extends AbstractCoreController<ReturnCause>{

    private ReturnCauseService  returnCauseService;

    @Autowired
    public ReturnCauseController(ReturnCauseService  returnCauseService) {
        super(returnCauseService, ReturnCause.class);
        this.returnCauseService = returnCauseService;
    }
}
