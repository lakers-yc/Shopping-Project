package com.changgou.goods.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.service.CategoryService;
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
@RequestMapping("/category")
@CrossOrigin
public class CategoryController extends AbstractCoreController<Category>{

    private CategoryService  categoryService;

    @Autowired
    public CategoryController(CategoryService  categoryService) {
        super(categoryService, Category.class);
        this.categoryService = categoryService;
    }

    /**
     *根据分类id 获取该分类下直接子分类列表 当ID为0时表示获取一级分类列表
     * @param pid
     * @return
     */

    @GetMapping("/list/{pid}")
    public Result<List<Category>> findByParentId(@PathVariable(name = "pid")Integer pid){
        List<Category> categoryList = categoryService.findByParentId(pid);
        return new Result<List<Category>>(true, StatusCode.OK,"查询成功",categoryList);
    }


}
