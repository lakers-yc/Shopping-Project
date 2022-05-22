package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SkuController {
    @Autowired
    private SkuService skuService;

    //从数据库中查询出来再导入ES中
    @GetMapping("/import")
    public Result importToES(){
        //1.调用feign查询商品微服务符合条件的sku的数据集合
        //2.将数据存储到ES服务器中
        skuService.importSku();
        return new Result(true, StatusCode.OK,"导入成功");
    }


    /**
     * 根据条件查询
     * @param searchMap 搜索的条件封装对象 包含要搜索的关键字，品牌名，规格选项，分类.....
     * @return 封装的数据对象 map 里面包含（当前页的记录 总页数，总记录数......）
     * required = false(防止报400错)--->会导致null，到实现类中会导致空指针异常
     */
    @PostMapping
    public Map<String,Object> search(@RequestBody(required = false) Map<String,String> searchMap){
        if (searchMap==null){
            searchMap = new HashMap<>();
        }
        return skuService.search(searchMap);
    }
}
