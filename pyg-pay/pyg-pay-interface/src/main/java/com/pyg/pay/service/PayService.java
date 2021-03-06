package com.pyg.pay.service;

import java.util.Map;

/**
 * Created by on 2018/8/29.
 */
public interface PayService {
    /**
     * 需求：生成二维码
     * @param username
     * @return
     */
    Map createQrCode(String username);
    /**
     * 需求：查询支付订单状态
     * 请求：../pay/queryPayStatus/"+out_trade_no
     * 参数：String out_trade_no
     * 返回值：PygResult
     */
    Map queryPayStatus(String out_trade_no);


    /*
    * 根据订单号查询该订单的商品
    * */
    void findByOutTradeNo(Long outTradeNo,Double totalFee,String orderId);

    /*
    * 交易成功，更改订单状态
    * */
    void updateStatus(String orderId);
}
