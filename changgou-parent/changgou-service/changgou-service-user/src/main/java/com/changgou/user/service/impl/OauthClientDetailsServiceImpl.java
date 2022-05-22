package com.changgou.user.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.user.dao.OauthClientDetailsMapper;
import com.changgou.user.pojo.OauthClientDetails;
import com.changgou.user.service.OauthClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:OauthClientDetails业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OauthClientDetailsServiceImpl extends CoreServiceImpl<OauthClientDetails> implements OauthClientDetailsService {

    private OauthClientDetailsMapper oauthClientDetailsMapper;

    @Autowired
    public OauthClientDetailsServiceImpl(OauthClientDetailsMapper oauthClientDetailsMapper) {
        super(oauthClientDetailsMapper, OauthClientDetails.class);
        this.oauthClientDetailsMapper = oauthClientDetailsMapper;
    }
}
