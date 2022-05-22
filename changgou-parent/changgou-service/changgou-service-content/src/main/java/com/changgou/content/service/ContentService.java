package com.changgou.content.service;
import com.changgou.content.pojo.Content;
import com.changgou.core.service.CoreService;

import java.util.List;

/****
 * @Author:admin
 * @Description:Content业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface ContentService extends CoreService<Content> {


    List<Content> findByCategory(Long id);
}
