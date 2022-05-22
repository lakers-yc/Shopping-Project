package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchResultMapperImpl implements SearchResultMapper {

    /**
     * //1.获取非高亮的数据
     * //2.获取高亮的数据
     * //3.将高亮的数据中name的值 替换到非高亮POJO中的NAME属性中
     * //4.再返回
     * @param searchResponse
     * @param aClass
     * @param pageable
     * @param <T>
     * @return
     */
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
        //1.获取content
        List<T> content = new ArrayList<T>();

        //2.获取分页对象 （不用获取了参数中有）
        SearchHits hits = response.getHits();
        //如果数据为空，增返回一个空数组
        if (hits==null || hits.getTotalHits()<=0){
            return new AggregatedPageImpl<T>(content);
        }

        //遍历所有数据--->hits，高亮和非高亮（见kibana中）
        for (SearchHit hit : hits) {
            //(1)先获取非高亮的JSON数据,并转skuinfo  hit.getSourceAsString()是JSON----->转换为skuInfo（POJO）
            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);

            //(2)获取高亮数据，将高亮的数据设置到SKUinfo中的name属性中
            //kibana中-----> "highlight": {"name":["<span style='color:red'>华为</span>P30手机 红 64G"]},
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();  //highlight是一个Map
            HighlightField highlightField = highlightFields.get("name");     //获取name里面的东西

            if (highlightField != null && highlightField.getFragments() != null) {
                Text[] fragments = highlightField.getFragments();   //获取文本数组[]中的内容:["<span style='color:red'>华为</span>P30手机 红 64G"]

                //单个数据： skuInfo.setName(fragments[0].string());
                //多个数据：创建一个字符串对象，遍历里面的字符串，最后拼接到一起
                StringBuffer sb = new StringBuffer();
                for (Text fragment : fragments) {       //遍历fragments中的字符串，虽然里面只有一条数据
                    String string = fragment.string();  //高亮的数据name字段对应的高亮数据
                    sb.append(string);                  //拼接每一条字符串数据
                }
                //(3)将高亮的数据设置到SKUinfo中的name属性中
                skuInfo.setName(sb.toString());
            }
            //添加到content中
            content.add((T) skuInfo);
        }

        //3.获取总记录数
        long totalHits = response.getHits().getTotalHits();//总记录数
        //4.获取聚合结果
        Aggregations aggregations = response.getAggregations();
        //5.获取游标的ID
        String scrollId = response.getScrollId();

        //最后返回content，分页对象，总记录数，聚合结果，游标的ID。
        return new AggregatedPageImpl<T>(content,pageable,totalHits,aggregations,scrollId);
    }

        /*
        String sourceAsString = hit.getSourceAsString();
        SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);*/

}
