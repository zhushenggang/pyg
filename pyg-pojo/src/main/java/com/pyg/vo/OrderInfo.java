package com.pyg.vo;

import com.pyg.pojo.TbAddress;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by on 2018/8/28.
 */
public class OrderInfo implements Serializable{

    private TbAddress address;

    private TbOrder orders;

    //定义明细
    private List<TbOrderItem> orderItemList;

    public TbAddress getAddress() {
        return address;
    }

    public void setAddress(TbAddress address) {
        this.address = address;
    }

    public TbOrder getOrders() {
        return orders;
    }

    public void setOrders(TbOrder orders) {
        this.orders = orders;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
