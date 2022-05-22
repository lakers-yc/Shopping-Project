package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.pojo.UndoLog;
import com.changgou.order.service.UndoLogService;
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
@RequestMapping("/undoLog")
@CrossOrigin
public class UndoLogController extends AbstractCoreController<UndoLog>{

    private UndoLogService  undoLogService;

    @Autowired
    public UndoLogController(UndoLogService  undoLogService) {
        super(undoLogService, UndoLog.class);
        this.undoLogService = undoLogService;
    }
}
