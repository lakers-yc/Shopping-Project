package com.changgou.content.feign;

import com.changgou.content.pojo.Content;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "content",path = "/content")
public interface ContentFeign {
    //根据分类的id 获取分类下所有的广告列表数据
    @GetMapping("/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable(name = "id")Long id);
}
