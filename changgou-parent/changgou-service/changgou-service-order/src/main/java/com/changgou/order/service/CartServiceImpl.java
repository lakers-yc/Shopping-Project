package com.changgou.order.service;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    //添加购物车
    @Override
    public void add(Integer num, Long id, String username) {
        //删除购物车逻辑
        if (num <= 0) {
            redisTemplate.boundHashOps("Cart_" + username).delete(id);
            return;
        }
        //1.根据商品的SKU_ID，获取商品数据(通过feign调用)
        Sku sku = skuFeign.findById(id).getData();

        //2.将商品的数据，转换到POJO中（orderitem）
        OrderItem orderItem = new OrderItem();

        //设置一级 二级 三级分类的ID  //在根据sku中的spu_id, 查询spu中的一级二级三级分类
        Spu spu = spuFeign.findById(sku.getSpuId()).getData();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());

        orderItem.setImage(sku.getImage());
        orderItem.setPrice(sku.getPrice());
        orderItem.setSkuId(id);
        orderItem.setSpuId(sku.getSpuId());
        orderItem.setName(sku.getName());
        orderItem.setNum(num);
        orderItem.setMoney(num * sku.getPrice());//应付金额
        orderItem.setPayMoney(num * sku.getPrice());//实付金额
        //...优惠券什么

        //3.将数据存储到redis中（购物车）  key：用户名； value：数据列表
        //hash key field value
        redisTemplate.boundHashOps("Cart_" + username).put(id, orderItem);
    }

    //购物车列表展示
    @Override
    public List<OrderItem> list(String username) {
        return redisTemplate.boundHashOps("Cart_" + username).values();
    }
}
