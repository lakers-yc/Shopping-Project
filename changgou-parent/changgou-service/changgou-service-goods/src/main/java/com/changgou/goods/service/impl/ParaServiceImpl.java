package com.changgou.goods.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.ParaMapper;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Para;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.ParaService;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/****
 * @Author:admin
 * @Description:Para业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class ParaServiceImpl extends CoreServiceImpl<Para> implements ParaService {

    private ParaMapper paraMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    public ParaServiceImpl(ParaMapper paraMapper) {
        super(paraMapper, Para.class);
        this.paraMapper = paraMapper;
    }

    @Override
    public List<Para> findByCategoryId(Integer id) {
        //1.根据商品分类获取分类对象
        Category category = categoryMapper.selectByPrimaryKey(id);
        //2.获取分类对象中的模板id
        Integer templateId = category.getTemplateId();
        //3.根据模板id获取规格列表数据 select * from tb_para where template_id = ?
        Para condition = new Para();
        condition.setTemplateId(templateId);//where template_id = ?
        return paraMapper.select(condition);//select * from tb_para
    }
}
