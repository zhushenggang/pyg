package com.pyg.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.cart.service.CartService;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbOrderItem;
import com.pyg.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by on 2018/8/26.
 */
@Service
public class CartServiceImpl implements CartService {

    //注入redis模板对象
    @Autowired
    private RedisTemplate redisTemplate;

    //注入商品mapper接口代理对象
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 需求：查询redis购物车
     * 参数：String userId
     * 返回值:List<cart>
     * redis购物车数据结构：hash
     * key:redis_cart
     * field:userId
     * value:json(list<cart>)
     */
    public List<Cart> findRedisCartList(String userId) {
        //从reids中查询购物车数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("redis_cart").get(userId);
        //判断
        if (cartList == null || cartList.size() == 0) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 需求：组装购物车数据结构
     * 参数：
     * 参数1：List<Cart> cartList  原来的购物车列表
     * 参数2：商品id
     * 参数3：购买的商品数量 num
     * 返回值：List<Cart>
     * 组装购物车数据结构业务：
     * 1，根据商品id查询商品
     * 2，判断商品是否存在
     * 3，判断商品是否上架（上架：可以买，下架：不可卖）
     * 4，判断是否有相同商家
     * 5，如果有相同的商家，判断购买是否有相同商家中相同的商品
     * 6，如果有相同商家，相同的商品： 商品数量相加，总价格重新计算
     * 7，如果有相同商家，没有相同的商品，新增商品添加到此商家的购物车
     * 8，没有没有相同商家，新建商家，添加商品数据
     */
    public List<Cart> addCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1，根据商品id查询商品
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //2，判断商品是否存在
        if (item == null) {
            throw new RuntimeException("此商品不存在");
        }
        //3,判断此商品是否上架
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("此商品不可用");
        }

        //4，判断是否有相同商家
        //cart对象代表一个商家
        Cart cart = this.isSameSeller(cartList, item.getSellerId());

        //判断购物车对象是否为空
        //如果不为空，表示有相同商家
        if (cart != null) {
            //有相同商家
            //获取此商家购物车明细
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //判断是否有相同的商品
            TbOrderItem orderItem = this.isSameItem(orderItemList, itemId);

            //判断是否有相同的商品
            if (orderItem != null) {
                //有相同的商品
                //数量相加
                orderItem.setNum(orderItem.getNum() + num);
                //计算总价格
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));

                //判断是删除
                if (orderItem.getNum() < 1) {
                    orderItemList.remove(orderItem);
                }

                //判断购物车列表是否存在
                if (orderItemList.size() == 0) {
                    //把商家移除，此商家的商品已经全部删除
                    cartList.remove(cart);
                }


            } else {
                //没有相同的商品
                //创建购物商品对象
                TbOrderItem orderItem1 = this.createOrderItem(item, itemId, num);


                //把新的商品添加此商家的购物明细中
                orderItemList.add(orderItem1);

            }


        } else {
            //没有相同的商家
            cart = new Cart();
            //设置此商家的购物车数据
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());
            //设置购物车明细
            //创建购物商品对象
            TbOrderItem orderItem1 = this.createOrderItem(item, itemId, num);
            //新建一个购物明细集合
            List<TbOrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem1);

            cart.setOrderItemList(orderItems);


            //把此商家对象添加回去购物车列表
            cartList.add(cart);

        }


        return cartList;
    }

    /**
     * 需求：添加redis购物车
     *
     * @param cartList
     * @param username
     */
    public void addRedisCart(List<Cart> cartList, String username) {
        //redis模板对象添加购物车数据
        redisTemplate.boundHashOps("redis_cart").put(username, cartList);
    }

    /**
     * 需求：合并购物车数据
     *
     * @param redisCartList
     * @param cookieCartList
     * @return
     */
    public List<Cart> mergeCart(List<Cart> redisCartList, List<Cart> cookieCartList) {
        List<Cart> cartList = null;
        //循环cookie购物车列表
        for (Cart cart : cookieCartList) {

            //获取购物车清单
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //循环购物车清单对象
            for (TbOrderItem orderItem : orderItemList) {
                cartList = this.addCartList(redisCartList, orderItem.getItemId(), orderItem.getNum());
            }

        }


        return cartList;
    }
    /*
    * 查询选中的商品信息
    * */
    @Override
    public List<Cart> findSelectItem(Long[] ids) {
        BoundHashOperations redis_cart = redisTemplate.boundHashOps("redis_cart");

        List<Cart> list = new ArrayList<>();
        List<TbOrderItem> orderlist = new LinkedList<>();
        Cart c = new Cart();
        //获得redis中的所有键
        Set<String> keys = redis_cart.keys();
        //遍历出所有的Cart集合
        for (String key : keys) {
            if (key.equals("ZHJ")){
                List<Cart> cartList = (List<Cart>) redis_cart.get(key);
                //取出cartList集合中的所有Cart对象
                for (Cart cart : cartList) {
                    List<TbOrderItem> orderItemList = cart.getOrderItemList();
                    //获得商家订单数据
                    for (TbOrderItem orderItem : orderItemList) {
                        //获得SKU的ID
                        String itemId = orderItem.getItemId()+"";
                        //遍历用户传进来的itemId
                        for (Long id : ids) {
                            if ((id+"").equals(itemId)){
                                orderlist.add(orderItem);
                            }
                        }
                    }
                }
            }
        }

        //组装选中购物车数据
        c.setOrderItemList(orderlist);
        list.add(c);
        return list;


    }

    /**
     * 需求：创建新的购物商品对象
     *
     * @param item
     * @param itemId
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Long itemId, Integer num) {

        TbOrderItem orderItem1 = new TbOrderItem();
        orderItem1.setGoodsId(item.getGoodsId());
        orderItem1.setItemId(itemId);
        orderItem1.setSellerId(item.getSellerId());
        orderItem1.setTitle(item.getTitle());
        orderItem1.setPrice(item.getPrice());
        orderItem1.setNum(num);
        orderItem1.setTotalFee(new BigDecimal(num * item.getPrice().doubleValue()));
        orderItem1.setPicPath(item.getImage());

        return orderItem1;
    }

    /**
     * 需求：判断相同的商家中是否存在相同的商品
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem isSameItem(List<TbOrderItem> orderItemList, Long itemId) {
        //循环购物车明细集合，判断是否有相同的商品
        for (TbOrderItem orderItem : orderItemList) {
            //比较商品id是否相等，如果相等，表示有相同的商品
            if (orderItem.getItemId() == itemId.longValue()) {
                return orderItem;
            }

        }

        return null;
    }

    /**
     * 需求：判断是否有相同商家
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart isSameSeller(List<Cart> cartList, String sellerId) {
        //循环购物车列表，判断否有相同商家
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }

        return null;
    }
}
