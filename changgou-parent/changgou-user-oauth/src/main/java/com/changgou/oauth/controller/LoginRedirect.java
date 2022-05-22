package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/oauth")
public class LoginRedirect {

    //展示登录的页面
    @GetMapping(value = "/login")
    public String login(String url, Model model){
        //返回逻辑视图页面
        model.addAttribute("url",url);
        return "login";
    }
}
