package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public Map<String, String> login(String username, String password, String grantType, String clientId, String clientSecret) {
        //模拟POST发送请求
        String url = "http://localhost:9001/oauth/token";
        //请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username",username);
        body.add("password",password);
        body.add("grant_type",grantType);

        //请求头
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization","Basic "+Base64.getEncoder().encodeToString(new String(clientId+":"+clientSecret).getBytes()));

        //请求实体对象（请求体和请求头对象）
        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<MultiValueMap<String,String>>(body,headers);

        //模拟浏览器发送请求
        //参数1 发送到的请求的路径
        //参数2 指定发送的请求的方法 POST
        //参数3 设置请求实体对象（里面封装了请求体 和请求头）
        //参数4 返回的数据类型（响应体的数据类型）
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        return exchange.getBody();//响应体
    }

    public static void main(String[] args) throws Exception {
        byte[] decode = Base64.getDecoder().decode("Y2hhbmdnb3U6Y2hhbmdnb3U=");
        String s = new String(decode, "utf-8");
        System.out.println(s);
        String s1 = Base64.getEncoder().encodeToString(new String("changgou:changgou").getBytes());
        System.out.println(s1);
    }
}
