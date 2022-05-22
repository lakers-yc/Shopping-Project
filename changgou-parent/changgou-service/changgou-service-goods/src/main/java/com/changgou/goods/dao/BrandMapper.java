package com.changgou.goods.dao;
import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import sun.awt.SunHints;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:BrandDao
 * @Date 2019/6/14 0:12
 *****/
public interface BrandMapper extends Mapper<Brand> {

    @Select(value = "select tbb.* from tb_category_brand tcb,tb_brand tbb where tcb.category_id = #{id} and tcb.brand_id=tbb.id;")
    List<Brand> findByCategory(Integer id);
}
