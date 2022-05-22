package com.changgou.order.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.OrderService;
import com.changgou.user.feign.UserFeign;
import entity.IdWorker;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/****
 * @Author:admin
 * @Description:Order业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OrderServiceImpl extends CoreServiceImpl<Order> implements OrderService {

    private OrderMapper orderMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper) {
        super(orderMapper, Order.class);
        this.orderMapper = orderMapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)//本地的spring的声明式事务注解
    @GlobalTransactional
    public Order add(Order order) {
    //1.添加数据到订单表(Order) 和 订单选表(OrderItem)中
        //1.1生成主键
        order.setId(idWorker.nextId()+"");

        //1.2获取redis中购物车的数据 循环遍历 统计即可
        List<OrderItem> orderItemList = redisTemplate.boundHashOps("Cart_" + order.getUsername()).values();
        Integer TotalNum = 0;
        Integer TotalMoney = 0;
        if (orderItemList == null || orderItemList.size()==0) {
            throw new RuntimeException("购物车数据异常,下单失败");
        }

        //从购物车来的
        for (OrderItem orderItem : orderItemList){
            TotalNum += orderItem.getNum();     //数量合计
            TotalMoney += orderItem.getMoney();  //金额合计

            //订单的选项
            orderItem.setId(idWorker.nextId()+"");
            orderItem.setOrderId(order.getId());
            //添加到订单明细表
            orderItemMapper.insertSelective(orderItem);

            //2.减库存(feign)
                //1.创建feign接口; 2.goods微服务controller实现接口 3.order添加依赖 4.order启动类添加注解 5.调用
                //方法：根据购买数量(num)和sku的(SkuId) 减库存。update tb_sku set num=num-#{num} where id=#{id} and num>=#{num}
            skuFeign.deleteSku(orderItem.getNum(),orderItem.getSkuId());
        }
        order.setTotalNum(TotalNum);
        order.setTotalMoney(TotalMoney);
        order.setPayMoney(TotalMoney);

        //1.3设置邮费免邮
        order.setPostFee(0);
        //1.5设置创建和更新时间
        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());
        order.setBuyerRate("0");
        //1.6设置状态
        order.setPayStatus("0");
        order.setConsignStatus("0");
        order.setOrderStatus("0");
        order.setIsDelete("0");//未删除
        orderMapper.insertSelective(order);

    //3.加积分(user微服务中)
        //Feign调用：1.user创建feign接口; 2.user微服务controller实现接口 3.order添加依赖 4.order启动类添加注解 5.调用
        //方法：根据username在user微服务中增加积分 update tb_user set points=points+#{points} where username=#{username}
        //按理说应该调用一个积分系统，获取该商品的积分
        userFeign.addPoints(order.getUsername(),10);

        //删除购物车
        redisTemplate.delete("Cart_" + order.getUsername());
        return order;
    }
}
