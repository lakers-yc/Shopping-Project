package com.changgou.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.*;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

@CanalEventListener
public class MyEventListene {
    /***
     * 自定义数据修改监听
     * @param eventType
     * @param rowData
     */
    // destination 指定的是目的地和 canal-server中的服务器的example目录一致
    // schema 指定要监听的数据库名
    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //监听数据库中的表
    @ListenPoint
            (destination = "example",
            schema = "changgou_content",
            table = {"tb_content_category", "tb_content"},
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.DELETE, CanalEntry.EventType.INSERT}
            )

    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.获取category_id的值
        String categoryId = getColumnValue(eventType, rowData);

        //2.通过feign调用广告微服务（根据分类的id 获取分类下所有的广告列表数据）
        Result<List<Content>> result = contentFeign.findByCategory(Long.valueOf(categoryId));

        //获取所有的广告数据
        List<Content> contentList = result.getData();

        //3.将数据存储到redis中
        stringRedisTemplate.boundValueOps("content_"+categoryId).set(JSON.toJSONString(contentList));

    }

    //获取category_id的值
    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        String categoryId = "";
        //1.判断如果是delete就获取before的数据
        if (eventType== CanalEntry.EventType.DELETE){
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();
                    break;
                }
            }
        }else{
        //2.判断如果是update和insert就获取after的数据
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : afterColumnsList) {
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();
                    break;
                }
            }

        }
        //3.获取行中category_id的值
        return categoryId;
    }
}
