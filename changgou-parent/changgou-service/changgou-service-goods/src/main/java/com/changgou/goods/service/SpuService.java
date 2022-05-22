package com.changgou.goods.service;

import com.changgou.core.service.CoreService;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;

/****
 * @Author:admin
 * @Description:Spu业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SpuService extends CoreService<Spu> {

    void save(Goods goods);

    Goods findGoodsById(Long id);
}
