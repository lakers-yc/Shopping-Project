package com.changgou.core;

import com.github.pagehelper.PageInfo;
import entity.Result;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.core *
 * @since 1.0
 */
public interface IPagingController<T> {

    /**
     * 查询所有并分页
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    Result<PageInfo<T>> findByPage(Integer pageNo, Integer pageSize);


    /**
     * 根据查询条件 record 分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param record
     * @return
     */
    Result<PageInfo<T>> findByPage(Integer pageNo, Integer pageSize, T record);


}
