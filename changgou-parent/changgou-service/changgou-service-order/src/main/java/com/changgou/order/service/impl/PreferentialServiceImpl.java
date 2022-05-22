package com.changgou.order.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.order.dao.PreferentialMapper;
import com.changgou.order.pojo.Preferential;
import com.changgou.order.service.PreferentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:Preferential业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class PreferentialServiceImpl extends CoreServiceImpl<Preferential> implements PreferentialService {

    private PreferentialMapper preferentialMapper;

    @Autowired
    public PreferentialServiceImpl(PreferentialMapper preferentialMapper) {
        super(preferentialMapper, Preferential.class);
        this.preferentialMapper = preferentialMapper;
    }
}
