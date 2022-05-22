package com.changgou.search.service;

import java.util.Map;

public interface SkuService {
    void importSku();


    Map<String, Object> search(Map<String, String> searchMap);
}
