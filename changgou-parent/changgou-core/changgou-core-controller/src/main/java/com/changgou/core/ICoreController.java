package com.changgou.core;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.core
 * @version 1.0
 * @date 2020/8/10
 */
public interface ICoreController<T> extends
        ISelectController<T>,
        IInsertController<T>,
        IPagingController<T>,
        IDeleteController<T>,
        IUpdateController<T> {
}
