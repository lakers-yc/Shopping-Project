package com.changgou.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.config
 * @version 1.0
 * @date 2020/3/24
 */
@Component
@ConfigurationProperties(prefix = "jks")
public class JKSProperties {

    /**
     * jks文件的名称
     */
    private String name;
    /**
     * 存储密码
     */
    private String storepassword;

    /**
     * 秘钥密码
     */
    private String keypassword;
    /**
     * 别名
     */
    private String alias;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorepassword() {
        return storepassword;
    }

    public void setStorepassword(String storepassword) {
        this.storepassword = storepassword;
    }

    public String getKeypassword() {
        return keypassword;
    }

    public void setKeypassword(String keypassword) {
        this.keypassword = keypassword;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
