package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    //导入数据到ES
    public void importSku() {
        //1.调用feign查询商品微服务符合条件的sku的数据集合
            //1.1在changgou-service-goods-api中创建一个接口  业务接口
            //1.2定义方法 和添加注解  方法：根据状态进行查询符合条件的sku的数据列表
            //1.3在changgou-service-goods微服务实现业务接口（编写controller service dao ）
            //1.4添加changgou-service-goods-pai的依赖，启动类中启用feignclients
            //1.5注入接口 指定调用
        Result<List<Sku>> result = skuFeign.findByStatus("1");
        List<Sku> skuList = result.getData();

        //先将POJO转成JSON，再将JSON转回POJO  类型不同
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuList), SkuInfo.class);

        //获取SkuMap
        for (SkuInfo skuInfo : skuInfoList) {
            //获取规格的数据 {"电视音响效果":"小影院","电视屏幕尺寸":"60英寸","尺码":"165"}
            String spec = skuInfo.getSpec();
            //转成MAP
            Map<String, Object> specMap = JSON.parseObject(spec, Map.class);
            //设置到skuinfo中的specMap属性中
            skuInfo.setSpecMap(specMap);
        }

        //2.将数据存储到ES服务器中
        skuEsMapper.saveAll(skuInfoList);  //JSON 用到了JSON序列化 jackson的序列化方式
    }

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
    //1.获取关键字的值
        String keywords = searchMap.get("keywords");
        if (StringUtils.isEmpty(keywords)){
            //轮播效果方法，随机产生一个数据-----防止空指针
            keywords="华为";
        }

    //2.创建查询对象的 构建对象----->设置分组查询条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //2.1 设置分组查询条件：商品分类
        //AggregationBuilders.terms("skuCategorygroup")表示设置分组条件:分组别名skuCategorygroup、分组字段为categoryName、大小为50
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(100));
        //2.2 设置分组查询条件：商品品牌
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandgroup").field("brandName").size(100));
        //2.3 设置分组查询条件：规格列表 ***字段设置为spec.keyword--->带keyword,不分词
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecgroup").field("spec.keyword").size(100));

        //2.4 设置高亮 1. 设置高亮的字段  2. 设置前缀和后缀
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name"));
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<span style=\"color:red\">").postTags("</span>"));

//3.设置查询条件： 匹配查询 match。 从多个字段中搜索 比如：从brandName 上或者从categoryName 上或者从name上
        //参数1:指定要搜索的内容、 参数2:指定要从哪一些字段上进行搜索  关系 OR
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords,"name","categoryName","brandName"));

    //3.1 设置过滤查询(多个条件组合)   bool查询 添加过滤查询
        // 1.创建bool查询对象    2.创建xxx过滤查询对象（商品分类....）   3.将条件设置bool查询对象（Must should,must_not,filter）
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //(1) 商品分类过滤
        String category = searchMap.get("category");
        if (!StringUtils.isEmpty(category)) {
            boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", category));  //must根据分数排序
        }
        //(2) 品牌分类过滤
        String brand = searchMap.get("brand");
        if (!StringUtils.isEmpty(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", brand));      //filter性能更好
        }
        //(3) 规格过滤 1.获取参数 2.循环遍历  3.判断以spec_开头的就将这些数据进行拼接，过滤查询
        //searchMap:{"keywords":"华为","category":"你点击的分类的值","brand":"你点击到的品牌的名称","spec_网络制式":"电信2G".....}
        for (Map.Entry<String, String> stringStringEntry : searchMap.entrySet()) {
            String key = stringStringEntry.getKey();       //spec_网络制式
            String value = stringStringEntry.getValue();   //电信2G
            if (key.startsWith("spec_")){
                //boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.网络制式.keyword", "电信2G"));
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." +key.substring(5) + ".keyword",value));
            }
        }
        //(4) 价格区间的过滤查询（范围）
        String price = searchMap.get("price");        //0-500,...,10000-*
        if (!StringUtils.isEmpty(price)){
            String[] split = price.split("-");  //split[0]  split[1]
            if (split[1].equals("*")){
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));   //如果>=10000，就过滤查询这个10000-*
            }else{
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0],true).to(split[1],true));
                //boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]).lte(split[1]));
            }
        }
        //(5) 最后----->拼接以上多个过滤条件:商品分类过滤、品牌分类过滤、规格过滤、价格区间的过滤查询（范围）
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);

    //3.2 分页查询:接收页面传递过来指定当前的页码值(pageNum)和每页显示的行(pageSize)
        String pageNumString = searchMap.get("pageNum");   //设置分页对象
        Integer pageNum = 1;   //1.设置默认页码值为第1页
        if (!StringUtils.isEmpty(pageNumString)){          //当传过来的值不为空，进行转换
            pageNum = Integer.parseInt(pageNumString);     //作用是将()内的String类型字符串转化为int类型
        }
        Integer pageSize =30;  //2.设置默认每页显示的行为30个
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum-1,pageSize));
        /*Pageable pageable = PageRequest.of(pageNum-1,pageSize); //参数1:指定当前的页码值 0 、 参数2:指定每页显示的行
        nativeSearchQueryBuilder.withPageable(pageable);*/

    //3.3 排序:排序的类型和字段 类似于order by column desc/asc（后台接收2个参数，分别是排序域名字和排序方式）
        String sortField = searchMap.get("sortField");
        String sortRule = searchMap.get("sortRule");    //默认：DESC(降序) /ASC（升序）
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
            //设置排序的字段   SortBuilders.fieldSort("price")
            //设置排序的类型   order(SortOrder.DESC)
            //设置升序还是降序  sortRule.equals("DESC")?SortOrder.DESC:SortOrder.ASC
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equals("DESC")?SortOrder.DESC:SortOrder.ASC));
        }

    //4.构建查询对象
        SearchQuery query = nativeSearchQueryBuilder.build();

    //5.执行查询
        //参数1:指定查询对象（所有的查询的条件封装在这）、参数2:指定返回的数据类型（字节码对象）、参数3:指定searchresultmap (自定义的)
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(query, SkuInfo.class,new SearchResultMapperImpl());

    //6.获取结果 封装返回
        //获取当前页的记录、根据条件命中的总记录数、总页数
        List<SkuInfo> content = skuInfos.getContent();     //当前页的记录 content
        long totalElements = skuInfos.getTotalElements();  //总记录数 totalElements
        int totalPages = skuInfos.getTotalPages();         //总页数 totalPages

        //获取 1.商品分类的分组结果---stringTerms
        StringTerms stringTerms = (StringTerms) skuInfos.getAggregation("skuCategorygroup");
        List<String> categoryList = getStringsList(stringTerms);
        //获取 2.品牌分类的分组结果---brandstringTerms
        StringTerms brandstringTerms = (StringTerms) skuInfos.getAggregation("skuBrandgroup");
        List<String> brandList = getStringsList(brandstringTerms);
        //获取 3.商品规格列表---stringTermsSpec（Json的字符串列表）
        StringTerms stringTermsSpec = (StringTerms) skuInfos.getAggregation("skuSpecgroup");
        Map<String, Set<String>> specMap = getStringSetMap(stringTermsSpec); //解析规格列表(返回Map集合) 例：网络：4G,5G...;颜色：白,黑,红...;尺寸：...;

        //解析规格数据 返回map对象
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("rows",content);               //当前页的记录
        resultMap.put("total",totalElements);        //总记录数
        resultMap.put("totalPages",totalPages);      //总页数
        resultMap.put("categoryList",categoryList);  //商品分类的列表
        resultMap.put("brandList",brandList);        //商品品牌的列表
        resultMap.put("specMap",specMap);            //规格列表
        return resultMap;
    }

    //获取分类列表 或者 品牌列表的统一方法
    private List<String> getStringsList(StringTerms Terms) {
        List<String> list = new ArrayList<>();
        if (Terms != null) {
            for (StringTerms.Bucket bucket : Terms.getBuckets()) {
                list.add(bucket.getKeyAsString());
            }
        }
        return list;
    }

    //获取规格列表数据,将 JSON字符串列表 转换解析成 Map<String, Set<String>>集合
    private Map<String, Set<String>> getStringSetMap(StringTerms stringTermsSpec) {
        Map<String, Set<String>> specMap = new HashMap<>();
        if (stringTermsSpec!=null){

            Set<String> set = new HashSet<String>();
            //遍历每一条JSON数据：
            for (StringTerms.Bucket bucket : stringTermsSpec.getBuckets()) {
                //JSON字符串keyAsString：{"手机屏幕尺寸":"5.5寸","网络":"电信4G","颜色":"白","测试":"s11","机身内存":"128G","存储":"16G","像素":"300万像素"}
                String keyAsString = bucket.getKeyAsString();
                Map<String,String> map = JSON.parseObject(keyAsString, Map.class);     //JSON转换成map
                for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {   //循环遍历map，拿到key和value
                    String key = stringStringEntry.getKey();      //key    规格名字：手机屏幕尺寸
                    String value = stringStringEntry.getValue();  //value  规格种类：5寸  ，5.5寸

                    //如果key为空就创建新的set，不为空就往后添加
                    set= specMap.get(key);
                    if (set==null){
                        set = new HashSet<String>();
                    }
                    set.add(value);        //将value加入到set集合中
                    specMap.put(key,set);  //将当前规格加入specMap到集合中  specMap：{"手机"：["5寸" ,"5.5寸"],"网络":["4G","5G"]......}
                }
            }
        }
        return specMap;
    }

    /*//获取分类列表数据
    private List<String> getStringsCategoryList(StringTerms stringTerms) {
        List<String> categoryList = new ArrayList<>();
        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                categoryList.add(bucket.getKeyAsString());
            }
        }
        return categoryList;
    }

    //获取品牌列表
    private List<String> getStringsBrandList(StringTerms stringTermsBrand) {
        List<String> brandList = new ArrayList<>();
        if (stringTermsBrand != null) {
            for (StringTerms.Bucket bucket : stringTermsBrand.getBuckets()) {
                brandList.add(bucket.getKeyAsString());
            }
        }
        return brandList;
    }*/

}
