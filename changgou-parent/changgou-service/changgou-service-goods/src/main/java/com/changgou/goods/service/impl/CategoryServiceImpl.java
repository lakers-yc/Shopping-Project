package com.changgou.goods.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/****
 * @Author:admin
 * @Description:Category业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class CategoryServiceImpl extends CoreServiceImpl<Category> implements CategoryService {

    private CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        super(categoryMapper, Category.class);
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> findByParentId(Integer pid) {
        //根据条件查询select * from tb_categary where parent_id = ?

        /*Category condition = new Category();
        condition.setParentId(pid);//where parent_id = ?
        categoryMapper.select(condition);
        return categaryList;*/

        return categoryMapper.findByParentId(pid);
    }
}
