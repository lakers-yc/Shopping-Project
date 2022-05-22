package com.changgou.user.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.user.dao.ProvincesMapper;
import com.changgou.user.pojo.Provinces;
import com.changgou.user.service.ProvincesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:Provinces业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class ProvincesServiceImpl extends CoreServiceImpl<Provinces> implements ProvincesService {

    private ProvincesMapper provincesMapper;

    @Autowired
    public ProvincesServiceImpl(ProvincesMapper provincesMapper) {
        super(provincesMapper, Provinces.class);
        this.provincesMapper = provincesMapper;
    }
}
