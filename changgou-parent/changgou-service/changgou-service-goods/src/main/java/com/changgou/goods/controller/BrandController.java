package com.changgou.goods.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
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
@RequestMapping("/brand")
@CrossOrigin
public class BrandController extends AbstractCoreController<Brand>{

    private BrandService  brandService;

    @Autowired
    public BrandController(BrandService  brandService) {
        super(brandService, Brand.class);
        this.brandService = brandService;
    }

    /**
     * 根据三级分类ID查询对应的品牌数据
     * @param id 三级分类的id
     * @return
     */
    @GetMapping("/category/{id}")
    public Result<List<Brand>> findBrandByCategory(@PathVariable(name = "id")Integer id){
        List<Brand> brandList = brandService.findByCategory(id);
        return new Result<List<Brand>>(true, StatusCode.OK,"查询品牌列表成功",brandList);
    }

    //测试ip连接数的限流
    @GetMapping("/test")
    public Result testConne(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Result(true,StatusCode.OK,"ok");
    }

}
