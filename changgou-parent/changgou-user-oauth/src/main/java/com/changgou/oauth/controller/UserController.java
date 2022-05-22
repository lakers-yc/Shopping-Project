package com.changgou.oauth.controller;

import com.changgou.oauth.service.LoginService;
import com.changgou.oauth.util.CookieUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private LoginService loginService;

    private static final String GRANT_TYPE="password";
    private static final String CLIENT_ID="changgou";
    private static final String CLIENT_SECRET="changgou";

    //Cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    @RequestMapping("/login")
    public Result login(String username,String password){
        //1.模拟POSTman发送请求（申请令牌）
        Map<String,String> map = loginService.login(username,password,GRANT_TYPE,CLIENT_ID,CLIENT_SECRET);
        //2.获取到令牌数据进行解析
        String access_token = map.get("access_token");
        //3.将数据存储到cookie中，并返回给页面
        saveCookie(access_token);
        return new Result(true, StatusCode.OK,"登录成功",access_token);
    }

    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }
}
