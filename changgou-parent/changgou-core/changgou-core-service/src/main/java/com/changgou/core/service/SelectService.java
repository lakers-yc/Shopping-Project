package com.changgou.core.service;

import java.util.List;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.core *
 * @since 1.0
 */
public interface SelectService<T> {
    //查询所有
    public List<T> selectAll();

    //查询一个对象
    public T selectByPrimaryKey(Object id);

    //根据条件查询
    public List<T> select(T record);

}
