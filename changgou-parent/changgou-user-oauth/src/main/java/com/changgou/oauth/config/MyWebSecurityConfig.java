package com.changgou.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/***
 * 描述
 * @author ljh
 * @packagename com.itheima.config
 * @version 1.0
 * @date 2020/1/10
 */
@Configuration
@EnableWebSecurity
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsServiceImpl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    //设置管理器 提供者 比如设置 使用默认的用户名和密码进行管理
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder);
    }

    @Override
    @Bean //设置认证管理器 便于我们使用 ，使用默认的认证管理器即可
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //全局静态资源的放行
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**",
                "/js/**",
                "/data/**",
                "/fonts/**",
                "/img/**",
                "/oauth/login",
                "/user/login",//全局放行 ，不经过spring security contxt的上下文 跳过默认的/user/login的路径
                "/login.html");
    }

    //设置拦截器 设置为任意的请求都需要登录认证
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                //设置用户登录的路径放行
                //.antMatchers("/user/login").permitAll()
                //剩下所有的路径都必须进行认证的配置
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/user/login")//配置处理的表单登录的路径
                .loginPage("/oauth/login");//登录的页面
        //	.and()
        //.httpBasic();//注意 在使用/user/login?username=zhangsan&password=itheima的时候不要带basic否则就会进入basic登录了。不再使用username的方式登录
    }
}
