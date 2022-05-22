package com.changgou.user.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.user.dao.CitiesMapper;
import com.changgou.user.pojo.Cities;
import com.changgou.user.service.CitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:Cities业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class CitiesServiceImpl extends CoreServiceImpl<Cities> implements CitiesService {

    private CitiesMapper citiesMapper;

    @Autowired
    public CitiesServiceImpl(CitiesMapper citiesMapper) {
        super(citiesMapper, Cities.class);
        this.citiesMapper = citiesMapper;
    }
}
