package com.changgou.filter;

import com.changgou.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZE_TOKEN = "Authorization";
    private static final String LOGIN_URL="http://localhost:9001/oauth/login?url=";//设置重定向的地址(登录页面)

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.先获取请求对象，再获取响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.判断如果是登录的路径---放行
        String path = request.getURI().getPath();
        if(path.startsWith("/api/user/login")){
            return chain.filter(exchange);
        }
        //3.先从请求参数(QueryParams)中获取Token令牌;请求参数没有---再从请求头(Headers)中获取;请求头没有---再从cookie中获取令牌
        String token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        if(StringUtils.isEmpty(token)){
            token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        }
        if(StringUtils.isEmpty(token)){
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if(cookie!=null){
                token = cookie.getValue();//令牌的数据
            }
        }
        //还是没有Token就拦截，如果为空，则重定向到登录的页面（认证服务器）;
        if(StringUtils.isEmpty(token)){
            //重定向到登录的页面 1.设置重定向的地址(登录页面) 2. 设置http状态码 303
            response.getHeaders().set("Location",LOGIN_URL+request.getURI().toString());
            response.setStatusCode(HttpStatus.SEE_OTHER); //还是没有Token就拦截
            return response.setComplete();
        }

        //5.将网关中收到的cookie的token数据，传递给下一个的微服务
        request.mutate().header(AUTHORIZE_TOKEN,"bearer "+token);
        /*//5.如果能获取到 还要解析令牌 解析不成功  拦截 返回 401
        try {
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }*/

        //6.如果能解析出来 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;      //数据值越低，过滤器的执行的优先级越高
    }
}
