package com.changgou.user.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.user.pojo.Provinces;
import com.changgou.user.service.ProvincesService;
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
@RequestMapping("/provinces")
@CrossOrigin
public class ProvincesController extends AbstractCoreController<Provinces>{

    private ProvincesService  provincesService;

    @Autowired
    public ProvincesController(ProvincesService  provincesService) {
        super(provincesService, Provinces.class);
        this.provincesService = provincesService;
    }
}
