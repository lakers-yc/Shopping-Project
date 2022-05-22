package com.changgou.content.controller;

import com.changgou.content.pojo.Content;
import com.changgou.content.service.ContentService;
import com.changgou.core.AbstractCoreController;
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
@RequestMapping("/content")
@CrossOrigin
public class ContentController extends AbstractCoreController<Content>{

    private ContentService  contentService;

    @Autowired
    public ContentController(ContentService  contentService) {
        super(contentService, Content.class);
        this.contentService = contentService;
    }
    /***
     * 根据categoryId查询广告集合
     */
    @GetMapping(value = "/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable Long id){
        //根据分类ID查询广告集合
        List<Content> contents = contentService.findByCategory(id);
        return new Result<List<Content>>(true,StatusCode.OK,"查询成功！",contents);
    }

    /*//实现接口 根据分类的id 获取分类下所有的广告列表数据
    //select * from tb_content where category_id = ?
    public Result<List<Content>> findByCategory(@PathVariable(name = "id")Long id){
        Content condition = new Content();
        condition.setCategoryId(id);
        List<Content> contentList = contentService.select(condition);
        return new Result<List<Content>>(true, StatusCode.OK,"查询成功",contentList);
    }*/
}
