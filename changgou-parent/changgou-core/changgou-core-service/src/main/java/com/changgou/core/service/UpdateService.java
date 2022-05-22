package com.changgou.core.service;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.core.service
 * @version 1.0
 * @date 2020/8/9
 */
public interface UpdateService<T> {

    //根据对象进行更新
    int updateByPrimaryKey(T record);
}
