package com.changgou.core.service;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.core.service
 * @version 1.0
 * @date 2020/8/9
 */
public interface InsertService<T> {
    /**
     * 添加记录
     * @param record
     * @return
     */
    int insert(T record);

}