package com.changgou.goods.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/sku")
@CrossOrigin
public class SkuController extends AbstractCoreController<Sku>{

    private SkuService  skuService;

    @Autowired
    public SkuController(SkuService  skuService) {
        super(skuService, Sku.class);
        this.skuService = skuService;
    }

    /**
     * feign专用 根据状态获取SKU的列表数据
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable(name = "status")String status){
        //select * from tb_sku where status =?
        List<Sku> skuList = skuService.findByStatus(status);
        return new Result<List<Sku>>(true, StatusCode.OK,"查询成功",skuList);
    }

    /*//根据sku_id查询sku列表数据
    @GetMapping(value = "/{id}")
    public Result<Sku> findBySkuId(@PathVariable(value = "id")Long id){
        return new Result<List<Sku>>();
    }*/
    //AbstractCoreController中已经有该方法


    //根据购买数量(num)和sku的(id) 减库存
    @GetMapping("/deleteSku")
    public Result deleteSku(@RequestParam(name = "num") Integer num,
                            @RequestParam(name = "id") Long id){
        Integer count = skuService.deleteSku(num,id);
        //就是在skuMapper中添加注解：update tb_sku set num=num-#{num} where id=#{id} and num>=#{num}
        if (count > 0 ){
            return new Result(true,StatusCode.OK,"扣减成功");
        }else {
            return new Result(false,StatusCode.ERROR,"扣减失败");
        }
    }

}
