package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//操作ES
//@Repository 可以不用加
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo,Long> {
        }
