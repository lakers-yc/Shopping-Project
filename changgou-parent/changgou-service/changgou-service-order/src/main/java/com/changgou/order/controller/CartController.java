package com.changgou.order.controller;

import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;


    //添加购物车 (要购买的商品的SKU的ID 购买的数量)
    @RequestMapping("/add")
    public Result add(Integer num,Long id){
        String username = tokenDecode.getUsername();
        cartService.add(num,id,username);
        return new Result(true,StatusCode.OK,"成功添加到购物车");
    }


    //购物车列表展示
    @GetMapping("/list")
    public Result<List<OrderItem>> list(){
        //获取登录用户名
        String username = tokenDecode.getUsername();
        //根据用户名查询该用户对应的购物车的列表
        List<OrderItem> cartList = cartService.list(username);
        return new Result(true,StatusCode.OK,"查询成功",cartList);
    }
}
