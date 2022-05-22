package com.changgou.core.service;

import com.github.pagehelper.PageInfo;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.core *
 * @since 1.0
 */
public interface PagingService<T> {

    //查询所有并分页
    PageInfo<T> findByPage(Integer pageNo, Integer pageSize);

    //根据查询条件 record 分页查询
    PageInfo<T> findByPage(Integer pageNo, Integer pageSize, T record);

    //根据查询条件exmaple来分页查询
    PageInfo<T> findByPageExample(Integer pageNo, Integer pageSize, Object example);
}
