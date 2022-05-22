package com.changgou.core.service;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.core.service
 * @version 1.0
 * @date 2020/8/9
 */
public interface CoreService<T> extends
        DeleteService<T>,
        InsertService<T>,
        PagingService<T>,
        SelectService<T>,
        UpdateService<T> {
}
