package com.pyg.vo;

import com.pyg.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by on 2018/8/26.
 */
public class Cart implements Serializable{
    //商家id 表示此商品属于哪个商家
    private String sellerId;
    //商家名称
    private String sellerName;

    //购物车明细
    //一个商家可以购买多个商品
    private List<TbOrderItem> orderItemList;


    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
