package com.changgou.seckill.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.service.SeckillGoodsService;
import entity.DateUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/seckillGoods")
@CrossOrigin
public class SeckillGoodsController extends AbstractCoreController<SeckillGoods> {

    private SeckillGoodsService seckillGoodsService;

    @Autowired
    public SeckillGoodsController(SeckillGoodsService seckillGoodsService) {
        super(seckillGoodsService, SeckillGoods.class);
        this.seckillGoodsService = seckillGoodsService;
    }

    //获取时间菜单
    @GetMapping("/menus")
    public List<Date> dateMenus() {
        return DateUtil.getDateMenus();
    }

    //根据时间区间查询 --> 秒杀商品列表数据
    @GetMapping("/list")
    public Result<List<SeckillGoods>> list(String time) {
        List<SeckillGoods> seckillGoods = seckillGoodsService.list(time);
        return new Result<>(true, StatusCode.OK, "秒杀商品列表数据查询成功", seckillGoods);
    }

    //获取某一商品详情
    @GetMapping("/one")
    public Result<SeckillGoods> one(String time,Long id){
        SeckillGoods seckillGoods = seckillGoodsService.one(time,id);
        return new Result<>(true, StatusCode.OK,"查询成功",seckillGoods);
    }
}
