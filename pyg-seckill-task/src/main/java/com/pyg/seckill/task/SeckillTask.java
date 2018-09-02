package com.pyg.seckill.task;

import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillGoodsExample;
import com.pyg.utils.SysConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by on 2018/8/31.
 */
@Component
public class SeckillTask {

    //注入redis模板对象
    @Autowired
    private RedisTemplate redisTemplate;

    //注入秒杀商品mapper接口代理对象
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 定时任务方法：
     * 需求：定时对秒杀商品进行入库
     * 春节秒杀入库
     * 6月1日入库
     * 十一入库
     * 双十一入库
     * 双12入库
     * 定时任务如何实现？
     * spring框架集成定时任务。使用Scheduled配置定时任务表达式
     * 定时任务表达式语法：
     * <p>
     * 业务：
     * 1，查询待秒杀的商品
     * * 审核通过商品
     * * 有库存
     * 2，把秒杀商品设置到redis服务器
     * 数据结构：
     * key: TbSeckillGoods.class.getName
     * field: seckill id
     * value: tbSecGoods
     */

    @Scheduled(cron = "*/10 * * * * ?")
    public void timeTask() {
        //1，查询待秒杀的商品
        //创建example对象
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        //创建criteria对象
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        //设置参数
        //审核通过商品
        criteria.andStatusEqualTo("1");
        //有库存
        criteria.andStockCountGreaterThan(0);

        //判断时间
        //秒杀活动开启时间必须小于当前时间
        //获取结束时间必须大于当前时间
        Date date = new Date();
        criteria.andStartTimeLessThan(date);
        criteria.andEndTimeGreaterThan(date);

        //执行查询
        List<TbSeckillGoods> secKillList = seckillGoodsMapper.selectByExample(example);

        //循环把集合中秒杀商品对象放入redis的hash中
        for (TbSeckillGoods tbSeckillGoods : secKillList) {

            //调用redis模板，设置数据
            redisTemplate.boundHashOps(TbSeckillGoods.class.getName()).put(tbSeckillGoods.getId(), tbSeckillGoods);

            //把秒杀商品id进行排队，把id放入list队列
            this.pushRedisList(tbSeckillGoods);
        }


    }

    /**
     * 需求：秒杀商品id放入redis队列
     *
     * @param tbSeckillGoods
     */
    private void pushRedisList(TbSeckillGoods tbSeckillGoods) {
        //获取秒杀商品剩余库存
        Integer stockCount = tbSeckillGoods.getStockCount();
        //根据库存数据，把秒杀商品id进行入队
        for (int i = 0; i < stockCount; i++) {
            redisTemplate.boundListOps(SysConstant.SECKILL_GOODS_ID + tbSeckillGoods.getId()).leftPush(tbSeckillGoods.getId());
        }
    }

}
