package com.changgou.goods.dao;
import com.changgou.goods.pojo.Category;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:CategoryDao
 * @Date 2019/6/14 0:12
 *****/
public interface CategoryMapper extends Mapper<Category> {

    /**
     * 根据父id查询分类列表
     * @param pid
     * @return
     */
    @Select(value = "select * from tb_category where parent_id=#{pid}")
    List<Category> findByParentId(Integer pid);
}
