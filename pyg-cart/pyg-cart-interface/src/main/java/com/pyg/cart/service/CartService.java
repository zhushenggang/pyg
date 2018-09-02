package com.pyg.cart.service;

import com.pyg.vo.Cart;

import java.util.List;

/**
 * Created by on 2018/8/26.
 */
public interface CartService {

    /**
     * 需求：查询redis购物车
     * 参数：String userId
     * 返回值:List<cart>
     */
    public List<Cart> findRedisCartList(String userId);
    /**
     * 需求：组装购物车数据结构
     * 参数：
     * 参数1：List<Cart> cartList  原来的购物车列表
     * 参数2：商品id
     * 参数3：购买的商品数量 num
     * 返回值：List<Cart>
     */
    public List<Cart> addCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 需求：添加redis购物车
     * @param cartList
     * @param username
     */
    public void addRedisCart(List<Cart> cartList, String username);

    /**
     * 需求：合并购物车数据
     * @param redisCartList
     * @param cookieCartList
     * @return
     */
    List<Cart> mergeCart(List<Cart> redisCartList, List<Cart> cookieCartList);
}
