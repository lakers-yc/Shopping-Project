package com.changgou.order.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderVo;
import com.changgou.order.service.OrderService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController extends AbstractCoreController<Order>{

    private OrderService  orderService;

    @Autowired
    private TokenDecode tokenDecode;

    @Autowired
    public OrderController(OrderService  orderService) {
        super(orderService, Order.class);
        this.orderService = orderService;
    }

    //创建订单
    @PostMapping("/add")
    public Result<OrderVo> order(@RequestBody Order order){
        //1.获取用户登录的用户名
        String username = tokenDecode.getUsername();
        order.setUsername(username);

        //2.添加数据到订单表和订单选表中(tb_order和tb_order_item)
        Order orderFromDb = orderService.add(order);

        //3.设置金额，订单号，支付类型（普通订单），并返回
        OrderVo orderVo = new OrderVo();
        orderVo.setTotalMoney(orderFromDb.getTotalMoney());
        orderVo.setId(orderFromDb.getId());
        orderVo.setType(1);
        return new Result<OrderVo>(true, StatusCode.OK,"下单成功",orderVo);
    }
}
