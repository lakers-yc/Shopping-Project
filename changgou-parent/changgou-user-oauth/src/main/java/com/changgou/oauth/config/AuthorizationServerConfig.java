package com.changgou.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.security.KeyPair;

/***
 * 描述
 * @author ljh
 * @packagename com.itheima.config
 * @version 1.0
 * @date 2020/1/10
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired  //该配置用于配置开放端点使用，比如哪些安全端点需要放行
    private PasswordEncoder passwordEncoder;

    @Resource(name = "dataSource")   //将数据库注入进来
    private DataSource dataSource;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")   //默认为 denyAll 表示拒绝所有  这里开放端点为申请令牌的端点 应当为所有人都可以访问
                .checkTokenAccess("isAuthenticated()")//默认也是为denyAll 表示拒绝所有  校验 令牌端点 应当是 只有登录之后才能校验
                .passwordEncoder(passwordEncoder);//设置密码需要使用加密器 针对客户端
        //.allowFormAuthenticationForClients();
    }

    //设置客户端配置 一定要配置，不配置就会报错 标识客户端配置项 ，
    // 支持 哪些授权模式 这里指定为changgou客户端可以有哪些授权模式
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //内存配置
        //数据库方式配置客户端
        clients.jdbc(dataSource).clients(jdbcClientDetailsService);
        /*clients.inMemory()
                .withClient("changgou")//客户端ID
                .secret(passwordEncoder.encode("changgou"))//客户端秘钥 注意需要加密存储
                .authorizedGrantTypes(
                        "authorization_code", //授权码模式
                        "refresh_token",      //刷新令牌
                        "password",           //密码认证
                        "client_credentials"  //客户端认证
                )
                .redirectUris("http://localhost")
                .refreshTokenValiditySeconds(3600)
                .accessTokenValiditySeconds(3600)
                .scopes("app");*/
    }

    @Autowired
    private ClientDetailsService jdbcClientDetailsService;

    //设置数据库的方式的客户端
    @Bean
    public ClientDetailsService jdbcClientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Autowired
    private JKSProperties jksProperties;

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        //设置jwt的转换器 必须要有
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        //设置jwt的秘钥库
        //秘钥库的名称
        String keystorepath =jksProperties.getName();
        //秘钥库的密码
        String storepassword = jksProperties.getStorepassword();
        //读取秘钥对的密码
        String keypassword = jksProperties.getKeypassword();

        //秘钥库的别名
        String alias = jksProperties.getAlias();


        KeyPair keyPair = new KeyStoreKeyFactory(
                new ClassPathResource(keystorepath), //设置加密的加载文件
                storepassword.toCharArray())//设置读取秘钥库文件的密码
                .getKeyPair(alias, keypassword.toCharArray());//设置获取秘钥的密码
        //设置秘钥对象
        converter.setKeyPair(keyPair);

        //使用JWT的令牌转换器
        DefaultAccessTokenConverter accessTokenConverter = (DefaultAccessTokenConverter) converter.getAccessTokenConverter();
        return converter;
    }

    //如果要采用jwt的方式进行认证 需要进行配置


    @Autowired
    private UserDetailsService userDetailsServiceImpl;

    //--------------------------可以不写---start-----------------
    @Autowired
    private TokenStore tokenStore;

    //设置存储方式使用jwttoken存储方式
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }
    //--------------------------可以不写----end----------------

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsServiceImpl)//一定要设置
                .tokenStore(tokenStore)//可以不设置，不设置会默认使用jwttoken 前提是使用了jwtAccessTokenConverter
                //设置converter 设置为jwt的令牌生成方式
                .accessTokenConverter(jwtAccessTokenConverter());
    }


}
