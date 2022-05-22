package com.changgou.order.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.order.dao.UndoLogMapper;
import com.changgou.order.pojo.UndoLog;
import com.changgou.order.service.UndoLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:UndoLog业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class UndoLogServiceImpl extends CoreServiceImpl<UndoLog> implements UndoLogService {

    private UndoLogMapper undoLogMapper;

    @Autowired
    public UndoLogServiceImpl(UndoLogMapper undoLogMapper) {
        super(undoLogMapper, UndoLog.class);
        this.undoLogMapper = undoLogMapper;
    }
}
