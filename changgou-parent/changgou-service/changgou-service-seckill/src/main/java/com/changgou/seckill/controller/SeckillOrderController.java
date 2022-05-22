package com.changgou.seckill.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import entity.Result;
import entity.SeckillStatus;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/seckillOrder")
@CrossOrigin
public class SeckillOrderController extends AbstractCoreController<SeckillOrder>{

    private SeckillOrderService  seckillOrderService;

    @Autowired
    public SeckillOrderController(SeckillOrderService  seckillOrderService) {
        super(seckillOrderService, SeckillOrder.class);
        this.seckillOrderService = seckillOrderService;
    }

    //添加订单
    @GetMapping("/add")
    public Result add(String time,Long id){
        String username = "zhangsan";
        Boolean add = seckillOrderService.add(time, id, username);
        if (add){
            return new Result(true, StatusCode.OK,"排队成功,稍等");
        }else {
            return new Result(false, StatusCode.ERROR,"下单失败");
        }
    }

    //用户查询抢单的状态 添加一个字段 type
    @RequestMapping("/query")
    public Result<SeckillStatus> query(){
        //获取用户名
        String username="zhangsan";
        //查询该用户的抢单的状态
        SeckillStatus seckillStatus = seckillOrderService.query(username);
        seckillStatus.setType(2);//todo 优化  秒杀类型

        return new Result<SeckillStatus>(true,StatusCode.OK,"查询状态成功",seckillStatus);
    }
}
