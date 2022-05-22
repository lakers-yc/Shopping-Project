package com.changgou.seckill.timer;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import entity.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    // 每5秒执行一次
    @Scheduled(cron = "0/5 * * * * ?")
    public void loadGoodsPushRedis(){
        //查询符合条件的商品的数据 以当前时间为基准的5个时间段进行时间的匹配
        // select * from tb_seckill_goods where  status='1' and stock_count>0 and 开始时间<= 当前时间 < 结束时间
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus){  //10 12 14 16 18
            //1.日期的字符串类型(时间转成yyyyMMddHH)
            String timespace = "SeckillGoods_"+DateUtil.data2str(dateMenu, "yyyyMMddHH");

            //2.条件查询数据
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();

            //2.1 审核状态->通过 status=1
            criteria.andEqualTo("status",1);
            //2.2 商品库存个数>0
            criteria.andGreaterThan("stockCount",0);
            //2.3 活动没有结束 startTime >= 开始的时间段, endTime < 开始时间段+2个小时
            criteria.andGreaterThanOrEqualTo("startTime",dateMenu);
            criteria.andLessThan("endTime",DateUtil.addDateHour(dateMenu,2));
            //2.4 排除已经存入Redis的商品
            Set keys = redisTemplate.boundHashOps(timespace).keys();
            if(keys!=null && keys.size()>0) {
                criteria.andNotIn("id", keys);
            }
            //2.5从数据库查询符合条件的商品数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);

            //3.遍历秒杀商品的集合，依次存入存入Redis
            for (SeckillGoods seckillGood : seckillGoods) {
                System.out.println(seckillGood.getId()+timespace);
                redisTemplate.boundHashOps(timespace).put(seckillGood.getId(),seckillGood);
            }

            //设置过期时间 2个小时过期
            redisTemplate.expire(timespace,2, TimeUnit.HOURS);
        }
    }
}
