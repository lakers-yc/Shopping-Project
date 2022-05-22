package com.changgou.goods.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.AlbumMapper;
import com.changgou.goods.pojo.Album;
import com.changgou.goods.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:Album业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class AlbumServiceImpl extends CoreServiceImpl<Album> implements AlbumService {

    private AlbumMapper albumMapper;

    @Autowired
    public AlbumServiceImpl(AlbumMapper albumMapper) {
        super(albumMapper, Album.class);
        this.albumMapper = albumMapper;
    }
}
