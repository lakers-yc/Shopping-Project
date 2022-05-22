package com.changgou.pay.service;

import java.util.Map;

public interface WeixinPayService {
    Map<String, String> createNative(Map<String,String> parameter);

    Map<String, String> queryStatus(String out_trade_no);

}
