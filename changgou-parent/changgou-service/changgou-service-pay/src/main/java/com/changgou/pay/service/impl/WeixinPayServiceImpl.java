package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Value("${weixin.notifyurl}")
    private String notifyurl;

    //模拟浏览器发送请求到微信支付系统
    //获取到code_url 返回
    @Override
    public Map<String, String> createNative(Map<String,String> parameter) {
        try {
            //1.创建一个map 组装参数
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("appid",appid);                 //微信公众账号或开放平台APP的唯一标识
            paramMap.put("mch_id",partner);              //财付通平台的商户账号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());  //随机id
            paramMap.put("body", "畅购");
            paramMap.put("out_trade_no", parameter.get("out_trade_no"));   //畅购的订单号
            paramMap.put("total_fee", parameter.get("total_fee"));         //金额 单位(分）
            paramMap.put("spbill_create_ip", "127.0.0.1");//终端ip
            paramMap.put("notify_url", notifyurl);        //回调地址
            paramMap.put("trade_type", "NATIVE");         //扫码支付
            //paramMap.put("nonce_str", WXPayUtil.);  //签名不用设置，在进行将map转成XML的过程中自动添加签名
            //添加附加字段attach 给微信  里面需要有type的值  JSON{type:1,username:yc,sex:male}
            HashMap<String, String> attachMap = new HashMap<>();
            attachMap.put("type",parameter.get("type"));
            attachMap.put("username",parameter.get("username"));
            paramMap.put("attach", JSON.toJSONString(attachMap));    //当前只有一个需求--->支付类型 ：普通？秒杀？

            //2.将map 转成 XML
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            //3.使用Httpclient 模拟浏览器发送一个HTTPS的POST的请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //4.使用httpclient 模拟浏览器接收响应（微信支付系统返回的一个XML）
            String result = httpClient.getContent();
            System.out.println(result);

            //5.将XML转成MAP
            Map<String, String> stringMap = WXPayUtil.xmlToMap(result);

            //6.按需求解析返回 需要金额 需要 订单号 需要code_url
            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("code_url",stringMap.get("code_url"));
            resultMap.put("total_fee",parameter.get("total_fee"));
            resultMap.put("out_trade_no",parameter.get("out_trade_no"));
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> queryStatus(String out_trade_no) {
        try {
            //1.创建一个map 组装参数
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid",appid);
            paramMap.put("mch_id",partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("out_trade_no", out_trade_no);//畅购的订单号

            //2.将map 转成XML 会自动的添加签名
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            //3.使用Httpclient 模拟浏览器发送一个HTTPS的POST的请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //4.使用httpclient 模拟浏览器接收响应（微信支付系统返回的一个XML）
            String result = httpClient.getContent();
            System.out.println(result);
            //5.将XML转成MAP
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
