package com.pyg.seckill.service.impl;

import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.utils.IdWorker;
import com.pyg.utils.SysConstant;
import com.pyg.vo.OrderRecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by on 2018/9/1.
 */
@Component
public class CreateOrder implements Runnable {

    //注入redis模板对象
    @Autowired
    private RedisTemplate redisTemplate;

    //注入秒杀商品mapper接口代理对象
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Override
    public void run() {

        //获取用户秒杀排队信息 redis服务器进行排队优化： redis服务器放入数据： 用户id,秒杀商品id
        OrderRecode orderRecode = (OrderRecode) redisTemplate.boundListOps(SysConstant.SECKILL_USER).rightPop();
        //判断此时入队对象是否存在
        if (orderRecode != null) {
            //表示此时秒杀用户正在排队
            //查询秒杀商品是否存在
            Long goodsId =
                    (Long) redisTemplate.boundListOps(SysConstant.SECKILL_GOODS_ID + orderRecode.getSeckillId()).rightPop();

            //判断秒杀商品是否存在
            if (goodsId == null) {
                //秒杀商品不存在，此时不能购买
                redisTemplate.boundSetOps(SysConstant.SECKILL_USER_ID).remove(orderRecode.getUserId());

                return;
            }

        }


        //从redis服务器中获取入库的秒杀商品
        TbSeckillGoods seckillGoods =
                (TbSeckillGoods) redisTemplate.boundHashOps(TbSeckillGoods.class.getName()).get(orderRecode.getSeckillId());

        //3），如果秒杀商品存在，创建秒杀订单,此订单此时处于未支付状态
        //创建秒杀商品对象
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setUserId(orderRecode.getUserId());
        //订单此时未支付
        seckillOrder.setStatus("0");
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setSeckillId(orderRecode.getSeckillId());
        seckillOrder.setSellerId(seckillGoods.getSellerId());

        //创建idworker对象，生成秒杀订单id
        IdWorker idWorker = new IdWorker();
        long nextId = idWorker.nextId();
        //秒杀订单id
        seckillOrder.setId(nextId);

        // 4），把新增订单存储在redis服务器中
        redisTemplate.boundHashOps(TbSeckillOrder.class.getSimpleName()).put(orderRecode.getUserId(), seckillOrder);

        //5），下订单后，把秒杀商品库存减一
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

        //6），判断库存是否小于0,卖完需要同步数据库
        if (seckillGoods.getStockCount() <= 0) {
            //更新数据库数据
            seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            //清空redis入库商品
            redisTemplate.boundHashOps(TbSeckillGoods.class.getName()).delete(seckillGoods.getId());
        } else {
            //7），否则把库存减少（但是此时没有减为0）的秒杀商品同步redis
            redisTemplate.boundHashOps(TbSeckillGoods.class.getName()).put(orderRecode.getSeckillId(), seckillGoods);

        }

        //抢购人数减一
        redisTemplate.boundValueOps(SysConstant.SECKILL_COUNT_USER).increment(-1);


    }
}
