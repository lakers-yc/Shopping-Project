package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user",path = "/user")
public interface UserFeign {
    //根据用户名查询
    @GetMapping("/load/{id}")
    public Result<User> findById(@PathVariable(name="id") String id);

    //根据username在user微服务中增加积分
    @GetMapping("/points/add")
    public Result addPoints(@RequestParam(name = "username") String username,
                           @RequestParam(name = "points") Integer points);
}
