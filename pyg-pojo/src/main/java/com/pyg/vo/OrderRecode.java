package com.pyg.vo;

import java.io.Serializable;

/**
 * Created by on 2018/9/1.
 */
public class OrderRecode implements Serializable {

    public OrderRecode(String userId, Long seckillId) {
        this.userId = userId;
        this.seckillId = seckillId;
    }

    //用户id
    private String userId;
    //秒杀商品id
    private Long seckillId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }
}
