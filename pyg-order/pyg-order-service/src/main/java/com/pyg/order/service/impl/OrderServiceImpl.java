package com.pyg.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbAddressMapper;
import com.pyg.mapper.TbOrderItemMapper;
import com.pyg.order.service.OrderService;
import com.pyg.mapper.TbOrderMapper;
import com.pyg.pojo.TbAddress;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbOrderExample;
import com.pyg.pojo.TbOrderExample.Criteria;
import com.pyg.pojo.TbOrderItem;
import com.pyg.utils.IdWorker;
import com.pyg.utils.PageResult;
import com.pyg.vo.Cart;
import com.pyg.vo.OrderInfo;
import com.sun.tools.javac.util.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    //注入地址mapper接口代理对象
    @Autowired
    private TbAddressMapper addressMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        orderMapper.insert(order);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //注入idworker生成分布式id对象
    @Autowired
    private IdWorker idWorker;

    //注入订单明细对象
    @Autowired
    private TbOrderItemMapper orderItemMapper;

    /**
     * 需求：提交单点
     *
     * @param orderInfo
     */
    public ArrayList<Long> submitOrder(OrderInfo orderInfo, List<Cart> redisCartList) {

        //更新地址对象
        //获取地址对象
        TbAddress address = orderInfo.getAddress();
        //更新
        addressMapper.updateByPrimaryKeySelective(address);

        ArrayList<Long> orderIdlist = new ArrayList<>();
        //循环购物清单
        //一个商家一个订单
        for (Cart cart : redisCartList) {
            //获取订单对象
            TbOrder orders = orderInfo.getOrders();
            orders.setSellerId(cart.getSellerId());
            //订单id,必须设置，不能重复
            Long orderId = idWorker.nextId();
            orders.setOrderId(orderId);
            //添加到订单ID列表
            orderIdlist.add(orderId);
            //免邮费
            orders.setPostFee("0");
            //状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
            orders.setStatus("1");
            //订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
            orders.setSourceType("2");

            //设置订单创建时间
            Date date = new Date();
            orders.setUpdateTime(date);
            orders.setCreateTime(date);

            //先保存订单
            orderMapper.insertSelective(orders);

            //保存订单明细
            //获取订单明细
            List<TbOrderItem> orderItemList = cart.getOrderItemList();

            //循环订单明细，保存订单明细
            for (TbOrderItem orderItem : orderItemList) {
                //使用ikworker生成订单明细id
                Long orderItemId = idWorker.nextId();
                orderItem.setId(orderItemId);
                //设置订单id
                orderItem.setOrderId(orderId);
                //商家id
                orderItem.setSellerId(cart.getSellerId());

                //保存
                orderItemMapper.insertSelective(orderItem);


            }


        }
        return orderIdlist;

    }

}
