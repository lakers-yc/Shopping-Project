package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.pojo.Preferential;
import com.changgou.order.service.PreferentialService;
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
@RequestMapping("/preferential")
@CrossOrigin
public class PreferentialController extends AbstractCoreController<Preferential>{

    private PreferentialService  preferentialService;

    @Autowired
    public PreferentialController(PreferentialService  preferentialService) {
        super(preferentialService, Preferential.class);
        this.preferentialService = preferentialService;
    }
}
