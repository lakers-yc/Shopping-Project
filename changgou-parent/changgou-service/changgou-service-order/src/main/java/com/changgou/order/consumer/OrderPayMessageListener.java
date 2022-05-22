package com.changgou.order.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
@RabbitListener(queues = "queue.order")
public class OrderPayMessageListener {

    @Resource
    private OrderMapper orderMapper;

    //用于监听消息，并根据支付状态处理订单
    @RabbitHandler
    public void handler(String msg) throws ParseException {
        if (!StringUtils.isEmpty(msg)){
            //1.接收消息转成MAP对象
            Map<String,String> map = JSON.parseObject(msg, Map.class);
            if(map.get("return_code").equals("SUCCESS")) {

            //2.判断是否支付成功--->如果支付成功,更新订单的状态、支付时间和交易流水
                //2.1 获取订单号（从数据库找）
                String out_trade_no = map.get("out_trade_no");
                Order order = orderMapper.selectByPrimaryKey(out_trade_no); //根据订单号获取订单的数据
                if(map.get("result_code").equals("SUCCESS")){
                    //2.2 如果订单不为空---> 修改支付状态、更新时间、支付时间、交易流水...
                    if(order!=null && order.getIsDelete().equals("0")) {
                        order.setPayStatus("1");        //修改支付状态
                        order.setUpdateTime(new Date());//更新时间
                        //支付时间
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = simpleDateFormat.parse(map.get("time_end"));
                        order.setPayTime(date);
                        //交易流水
                        order.setTransactionId(map.get("transaction_id"));
                    //2.3 最后执行SQL 保存到数据库中
                        orderMapper.updateByPrimaryKeySelective(order);
                    }
                }else {
            //3.如果支付失败:关闭交易 判断 成功 删除订单(todo 模拟浏览器发送请求给微信支付 关闭订单)
                    order.setIsDelete("1");  //删除
                    orderMapper.updateByPrimaryKeySelective(order);
                }
            }else {
                System.out.println("通讯失败");
            }
        }else {
            System.out.println("数据为空");
        }
    }
}
