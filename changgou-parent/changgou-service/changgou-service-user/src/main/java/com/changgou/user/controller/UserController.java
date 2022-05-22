package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.core.AbstractCoreController;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import entity.BCrypt;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController extends AbstractCoreController<User>{

    private UserService  userService;

    @Autowired
    public UserController(UserService  userService) {
        super(userService, User.class);
        this.userService = userService;
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response){
        //1.判断是否为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return new Result(false, StatusCode.LOGINERROR, "用户名和密码不能为空");
        }
        //2.判断是否数据库中有该用户名对应的数据
        User user = userService.selectByPrimaryKey(username);
        if (user == null) {
            return new Result(false, StatusCode.LOGINERROR, "用户名或者密码错误");
        }
        //3.判断是否数据的密码和传递的密码一致
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return new Result(false, StatusCode.LOGINERROR, "用户名或者密码错误");
        }

        //4.先生成一个令牌，再返回给用户
        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("role","ROLE_ADMIN");

        String token = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(map), null); //生成令牌

        //放入Cookie中
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setPath("/");
        response.addCookie(cookie);

        //4.3 将令牌信息返回给前端
        return new Result(true, StatusCode.OK,"登录成功",token);
    }

    //根据用户名查询(用于feign调用)
    @GetMapping("/load/{id}")
    public Result<User> findById(@PathVariable(name="id") String id){
        User user = userService.selectByPrimaryKey(id);
        return new Result(true, StatusCode.OK,"查询成功",user);
    }

    //根据username在user微服务中增加积分
    @GetMapping("/points/add")
    public Result addPoints(@RequestParam(name = "username") String username,
                           @RequestParam(name = "points") Integer points){
        Integer count = userService.addPoints(username,points);

        if (count > 0 ){
            return new Result<>(true,StatusCode.OK,"添加积分成功");
        }else {
            return new Result<>(false,StatusCode.ERROR,"添加积分失败");
        }
    }

    public static void main(String[] args) throws Exception {
        //bcrypt
        String encode = new BCryptPasswordEncoder().encode("123456");
        System.out.println(encode);

        //Base64解码
        byte[] bytes = Base64.getDecoder().decode("eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9");
        System.out.println(new String(bytes,"utf-8"));
    }
}
