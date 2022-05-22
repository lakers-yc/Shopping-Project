package com.changgou.oauth.service;

import java.util.Map;

public interface LoginService {
    Map<String, String> login(String username, String password, String grantType, String clientId, String clientSecret);

}
