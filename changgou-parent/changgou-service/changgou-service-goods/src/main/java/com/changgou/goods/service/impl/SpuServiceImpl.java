package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:admin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl extends CoreServiceImpl<Spu> implements SpuService {

    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    public SpuServiceImpl(SpuMapper spuMapper) {
        super(spuMapper, Spu.class);
        this.spuMapper = spuMapper;
    }

    //判断 如果页面传递过来了spu的ID 说明是要修改
    // 如果页面没有传递来spu的ID说明是要添加
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Goods goods) {
        /*1.导入spu数据*/
        Spu spu = goods.getSpu();

        //判断是否spu的ID有值,如果有值就是更新, 否则就是添加。
        if (spu.getId()!= null){
            //更新spu
            spuMapper.updateByPrimaryKeySelective(spu);

            //先删除原来的sku列表，在添加sku
            //delete from tb_sku where spu_id=?
            Sku condition = new Sku();
            condition.setSpuId(spu.getId());
            skuMapper.delete(condition);
            /*List<Sku> skuList = goods.getSkuList();
            for (Sku sku : skuList) {
                skuMapper.updateByPrimaryKeySelective(sku);
            }*/

        }else {
            //添加
            //1.1生成主键
            long id = idWorker.nextId();
            spu.setId(id);
            spuMapper.insertSelective(spu);
        }
        saveSpu(goods, spu);
    }

    private void saveSpu(Goods goods, Spu spu) {
        /*2.获取页面传递过来的sku的列表数据，添加到sku表中*/
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {    //批量添加
            //2.1生成sku的主键
            long skuId = idWorker.nextId();
            sku.setId(skuId);

            //2.2 设置skuname 要求：将spu的name + 空格 + 规格选项值 例如：商品规格：颜色 内存容量---》华为mate40 黑色 256G
            //{"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}
        /*思路  1.获取页面传递过来的规格的数据是一个JSON
               2.将JSON转成MAP
               3.循环遍历 获取map中的 value
               4.通过空格拼接 得到sku的名称*/
            String spec = sku.getSpec();
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            String name = sku.getName();
            for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                //黑色
                String value = stringStringEntry.getValue();
                //商品名 黑色 256G
                name += " " + value;
            }
            sku.setName(name);
            //2.3设置创建时间和更新时间
            sku.setCreateTime(new Date());
            sku.setUpdateTime(sku.getCreateTime());
            //2.4设置spu的ID
            sku.setSpuId(spu.getId());
            //2.5 设置分类的ID 和名称（根据spu中的categoy3ID 获取分类表中的分类数据（id和name） -->再设置给sku的categoy_id 和category_name字段）
            Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
            if (category != null) {
                sku.setCategoryId(category.getId());
                sku.setCategoryName(category.getName());
            }
            //2.6设置品牌名字 根据spu的brand_id 从brand表中获取品牌的名称 ---》再设置sku的brand_name
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            sku.setBrandName(brand.getName());

            skuMapper.insertSelective(sku);
        }
    }

    @Override
    public Goods findGoodsById(Long id) {
        //1.获取spu的数据
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //2.获取sku的列表数据 select * from tb_sku where spu_id = ？
        Sku condition = new Sku();
        condition.setSpuId(id);// where spu_id = ？
        List<Sku> skuList = skuMapper.select(condition);//select * from tb_sku
        //3.组合对象，返回
        return new Goods(spu,skuList);
    }
}
