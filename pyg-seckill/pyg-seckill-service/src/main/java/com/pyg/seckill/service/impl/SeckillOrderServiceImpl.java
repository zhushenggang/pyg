package com.pyg.seckill.service.impl;

import java.util.List;

import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.utils.IdWorker;
import com.pyg.utils.SysConstant;
import com.pyg.vo.OrderRecode;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbSeckillOrderMapper;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.pojo.TbSeckillOrderExample;
import com.pyg.pojo.TbSeckillOrderExample.Criteria;
import com.pyg.seckill.service.SeckillOrderService;

import com.pyg.utils.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    //注入redis模板对象
    @Autowired
    private RedisTemplate redisTemplate;

    //注入秒杀商品mapper对象
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillOrder> findAll() {
        return seckillOrderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillOrderExample example = new TbSeckillOrderExample();
        Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
            }
            if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
            }
            if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
            }
            if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
                criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
            }
            if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
            }
            if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
            }
            if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
                criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
            }

        }

        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //注入线程调度对象
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    //注入多线程对象
    @Autowired
    private CreateOrder createOrder;

    /**
     * 需求：开启多线程下单
     * 1）判断商品入库商品是否存在，如果不存在，表示已售罄
     * 2）判断用户是否在排队，如果在排序，获取用户订单，如果有订单，表示有支付订单，不能下单
     * 3）用户在排队，那就是排队中
     * 4）否则，没有排队，排序秒杀人数是否超限
     * 5）如果秒杀人数没有超限，那么就可以参与排队，参与秒杀。
     * 6）参与排队： 存储排队记录，存储排队用户，存储排序人数+1
     */
    public void createOrder(Long seckillId, String userId) {
        //1）判断商品入库商品是否存在，如果不存在，表示已售罄
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(TbSeckillGoods.class.getName()).get(seckillId);
        if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("已售罄");
        }
        //2）判断用户是否在排队，如果在排对，获取用户订单，如果有订单，表示有支付订单，不能下单
        boolean member = redisTemplate.boundSetOps(SysConstant.SECKILL_USER_ID).isMember(userId);
        //表示用户正在排队
        if (member) {
            //获取当前用户订单
            Object order = redisTemplate.boundHashOps(TbSeckillOrder.class.getSimpleName()).get(userId);
            if (order != null) {
                throw new RuntimeException("你已经下单，还未支付");
            }
            throw new RuntimeException("正在排队中....");
        }

        //4）否则，没有排队，排序秒杀人数是否超限
        //获取秒杀人数数量
        Long seckill_persons = redisTemplate.boundValueOps(SysConstant.SECKILL_COUNT_USER).increment(0);
        if(seckill_persons+200>seckillGoods.getStockCount()){
            throw  new RuntimeException("秒杀过多..");
        }

        //5）如果秒杀人数没有超限，那么就可以参与排队，参与秒杀。
        OrderRecode orderRecode = new OrderRecode(userId,seckillId);
        //入队
        redisTemplate.boundListOps(SysConstant.SECKILL_USER).leftPush(orderRecode);
        //记录用户入队状态
        redisTemplate.boundSetOps(SysConstant.SECKILL_USER_ID).add(userId);

       // 6）参与排队： 存储排队记录，存储排队用户，存储排序人数+1
        //抢购人数加1
        redisTemplate.boundValueOps(SysConstant.SECKILL_COUNT_USER).increment(1);

        //调用多线程执行下单任务
        threadPoolTaskExecutor.execute(createOrder);

    }

    /**
     * 查询用户秒杀订单
     * @param userId
     * @return
     */
    public TbSeckillOrder checkSeckillOrderStatus(String userId) {
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(TbSeckillOrder.class.getSimpleName()).get(userId);
        return seckillOrder;
    }

    /**
     * 获取秒杀排队人数
     * @return
     */
    public Long findSeckillUsers() {
        Long increment = redisTemplate.boundValueOps(SysConstant.SECKILL_COUNT_USER).increment(0);
        return increment;
    }

    /**
     * 需求：秒杀下单
     *
     * @param seckillId 业务：
     *                  1），从redis服务器中获取入库的秒杀商品
     *                  2），判断商品是否存在，或是是商品库存是否小于等于0
     *                  3），如果秒杀商品存在，创建秒杀订单,此订单此时处于未支付状态
     *                  4），把新增订单存储在redis服务器中
     *                  5），下订单后，把秒杀商品库存减一
     *                  6），判断库存是否小于0,卖完需要同步数据库
     *                  7），否则把库存减少（但是此时没有减为0）的秒杀商品同步redis
     */
    public void saveOrder(Long seckillId, String userId) {
        //1），从redis服务器中获取入库的秒杀商品
       /* */

        //从redis队列中出队一个商品id
        Long goodsId = (Long) redisTemplate.boundListOps(SysConstant.SECKILL_GOODS_ID + seckillId).rightPop();

        // 2），判断商品是否存在，或是是商品库存是否小于等于0
        if (goodsId == null) {
            throw new RuntimeException("已售罄");
        }
       /* if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("已售罄");
        }*/

        //从redis服务器中获取入库的秒杀商品
        TbSeckillGoods seckillGoods =
                (TbSeckillGoods) redisTemplate.boundHashOps(TbSeckillGoods.class.getName()).get(seckillId);

        //3），如果秒杀商品存在，创建秒杀订单,此订单此时处于未支付状态
        //创建秒杀商品对象
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setUserId(userId);
        //订单此时未支付
        seckillOrder.setStatus("0");
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setSeckillId(seckillId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());

        //创建idworker对象，生成秒杀订单id
        IdWorker idWorker = new IdWorker();
        long nextId = idWorker.nextId();
        //秒杀订单id
        seckillOrder.setId(nextId);

        // 4），把新增订单存储在redis服务器中
        redisTemplate.boundHashOps(TbSeckillOrder.class.getSimpleName()).put(userId, seckillOrder);

        //5），下订单后，把秒杀商品库存减一
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

        //6），判断库存是否小于0,卖完需要同步数据库
        if (seckillGoods.getStockCount() <= 0) {
            //更新数据库数据
            seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
        } else {
            //7），否则把库存减少（但是此时没有减为0）的秒杀商品同步redis
            redisTemplate.boundHashOps(TbSeckillGoods.class.getName()).put(seckillId, seckillGoods);

        }

    }

}
