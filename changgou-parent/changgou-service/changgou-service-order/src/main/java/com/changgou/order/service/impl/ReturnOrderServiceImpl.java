package com.changgou.order.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.order.dao.ReturnOrderMapper;
import com.changgou.order.pojo.ReturnOrder;
import com.changgou.order.service.ReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:ReturnOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class ReturnOrderServiceImpl extends CoreServiceImpl<ReturnOrder> implements ReturnOrderService {

    private ReturnOrderMapper returnOrderMapper;

    @Autowired
    public ReturnOrderServiceImpl(ReturnOrderMapper returnOrderMapper) {
        super(returnOrderMapper, ReturnOrder.class);
        this.returnOrderMapper = returnOrderMapper;
    }
}
