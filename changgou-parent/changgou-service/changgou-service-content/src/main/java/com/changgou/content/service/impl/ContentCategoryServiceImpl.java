package com.changgou.content.service.impl;

import com.changgou.content.dao.ContentCategoryMapper;
import com.changgou.content.pojo.ContentCategory;
import com.changgou.content.service.ContentCategoryService;
import com.changgou.core.service.impl.CoreServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:ContentCategory业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class ContentCategoryServiceImpl extends CoreServiceImpl<ContentCategory> implements ContentCategoryService {

    private ContentCategoryMapper contentCategoryMapper;

    @Autowired
    public ContentCategoryServiceImpl(ContentCategoryMapper contentCategoryMapper) {
        super(contentCategoryMapper, ContentCategory.class);
        this.contentCategoryMapper = contentCategoryMapper;
    }
}
