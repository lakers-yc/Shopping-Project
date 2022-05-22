package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "goods", path = "/sku")
public interface SkuFeign {

    //根据状态查询符合条件的sku的数据列表
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable(name = "status") String status);

    //根据sku_id查询sku列表数据
    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable(name = "id") Long id);


    //根据购买数量(num)和sku的(id) 减库存
    @GetMapping("/deleteSku")
    public Result deleteSku(@RequestParam(name = "num") Integer num,
                            @RequestParam(name = "id") Long id);
}
