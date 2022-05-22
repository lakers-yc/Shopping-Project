package com.changgou.goods.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SpecMapper;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/****
 * @Author:admin
 * @Description:Spec业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpecServiceImpl extends CoreServiceImpl<Spec> implements SpecService {

    private SpecMapper specMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    public SpecServiceImpl(SpecMapper specMapper) {
        super(specMapper, Spec.class);
        this.specMapper = specMapper;
    }

    @Override
    public List<Spec> findByCategoryId(Integer id) {

        //1.根据商品分类获取分类对象
        Category category = categoryMapper.selectByPrimaryKey(id);
        //2.获取分类对象中的模板id
        Integer templateId = category.getTemplateId();
        //3.根据模板id获取规格列表数据 select * from tb_spec where template_id = ?
        Spec condition = new Spec();
        condition.setTemplateId(templateId);//where template_id = ?
        return specMapper.select(condition);//select * from tb_spec
    }
}
