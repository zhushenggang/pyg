package com.pyg.seckill.service;

import java.util.List;

import com.pyg.pojo.TbSeckillOrder;

import com.pyg.utils.PageResult;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface SeckillOrderService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbSeckillOrder> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbSeckillOrder seckillOrder);


    /**
     * 修改
     */
    public void update(TbSeckillOrder seckillOrder);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbSeckillOrder findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);

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
    public void saveOrder(Long seckillId,String userId);
    public void createOrder(Long seckillId,String userId);

    /**
     * 查询用户秒杀订单
     * @param userId
     * @return
     */
    TbSeckillOrder checkSeckillOrderStatus(String userId);

    /**
     * 获取秒杀排队人数
     * @return
     */
    Long findSeckillUsers();
}
