package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/weixin/pay")
public class WeixinPayController {
    //成功
    private static final String SUCCESS_RETURN = "<xml>" +
            "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
            "  <return_msg><![CDATA[OK]]></return_msg>\n" +
            "</xml>";

    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //普通订单
    @Value("${mq.pay.exchange.order}")
    private String exchange;

    @Value("${mq.pay.routing.key}")
    private String routing;

    //秒杀订单
    @Value("${mq.pay.exchange.seckillorder}")
    private String seckillexchange;

    @Value("${mq.pay.routing.seckillkey}")
    private String seckillrouting;

    /**
     * 生成支付二维码的链接 返回给页面 页面通过jS插件生成二维码
     *
     * @param //out_trade_no 订单号
     * @param //total_fee    金额 （单位:分）
     * @return
     */
    @GetMapping("/create/native")
    public Result<Map<String, String>> createNative(@RequestParam Map<String, String> parameter) {
        //1.模拟浏览器 发送HTTPS请求 给微信支付系统
        //获取当前登录的用户名
        String username = "yc";// TODO: 2022/5/18  //tokendcode获取
        parameter.put("username",username);
        Map<String, String> resultMap = weixinPayService.createNative(parameter);
        return new Result<Map<String, String>>(true, StatusCode.OK, "生成成功", resultMap);
    }

    /**
     * 根据订单号 获取订单对应的支付的状态等信息
     * @param out_trade_no
     * @return
     */
    @GetMapping("/status/query")
    public Result<Map<String, String>> ueryStatus(String out_trade_no) {
        Map<String, String> resultMap = weixinPayService.queryStatus(out_trade_no);
        return new Result<Map<String, String>>(true, StatusCode.OK, "查询订单的状态成功，具体状态请查看data", resultMap);
    }


    /**
     * 写一个方法 接收微信发送过来的请求（微信的通知）
     * @param request
     * @return
     */
    @RequestMapping("/notify/url")
    public String notifyurl(HttpServletRequest request) {
        InputStream inStream;
        try {
            //1.接收微信通知过来的数据 通过数据流的形式获取(hutools io工具类)
            inStream = request.getInputStream();
            //读取支付回调数据
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            // 将支付回调数据转换成xml字符串
            String result = new String(outSteam.toByteArray(), "utf-8");
            System.out.println(result);
            Map<String, String> map = WXPayUtil.xmlToMap(result); //XML--->MAP

            //2.发送消息给MQ 消息本身需要订单号,交易流水号,支付时间。JSON:{key1:value1,key2:value2,key3:value3}
            //参数1:指定交换机的名称 参数2:指定routingkey 参数3:指定消息本身

            //2.1 获取type数据做判断{type:2,username:ycc}，来绑定不同的交换机
            String attachjson = map.get("attach");
            Map<String,String> attachMap = JSON.parseObject(attachjson, Map.class);
            String type = attachMap.get("type");

            //判断如果是普通支付--->发送普通支付相关的消息;如果是秒杀支付--->发送秒杀支付相关的消息。
            if (type.equals(1)){
                rabbitTemplate.convertAndSend(exchange, routing, JSON.toJSONString(map));
            }else if (type.equals(2)){
                //秒杀发送消息  1.添加起步依赖 2配置连接到rabbitmq服务端的Ip:port 3 创建队列 交换机 绑定 4发送消息
                rabbitTemplate.convertAndSend(seckillexchange,seckillrouting,JSON.toJSONString(map));
            }else {
                System.out.println("错误类型");
                //log.error("")
            }
            //3.返回给微信
            return SUCCESS_RETURN;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
