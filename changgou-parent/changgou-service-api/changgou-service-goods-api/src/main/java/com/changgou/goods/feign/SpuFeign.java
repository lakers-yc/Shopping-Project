package com.changgou.goods.feign;

import com.changgou.goods.pojo.Spu;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "goods",path = "/spu")
public interface SpuFeign {

    //在根据sku中的spu_id, 查询spu中的一级二级三级分类
    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable(value = "id") Long id);
}
