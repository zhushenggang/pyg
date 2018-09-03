package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.mapper.TbOrderMapper;
import com.pyg.mapper.TbPayLogMapper;
import com.pyg.pay.service.PayService;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbOrderExample;
import com.pyg.pojo.TbPayLog;
import com.pyg.pojo.TbPayLogExample;
import com.pyg.utils.HttpClient;
import com.pyg.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by on 2018/8/29.
 */
@Service
public class PayServiceImpl implements PayService {

    //注入订单mapper对象
    @Autowired
    private TbOrderMapper orderMapper;

    //注入公众号唯一标识appid
    @Value("${appid}")
    private String appid;
    //注入商户号
    @Value("${partner}")
    private String partner;

    //注入秘钥
    @Value("${partnerkey}")
    private String partnerkey;

    //注入统一下单地址
    @Value("${payUrl}")
    private String payUrl;


    //注入回调地址
    @Value("${notifyurl}")
    private String notifyurl;

    /**
     * 需求：生成二维码
     * @param username
     * @return
     */
    public Map createQrCode(String username) {

        try {
            //创建订单example对象
            TbOrderExample example = new TbOrderExample();
            TbOrderExample.Criteria criteria = example.createCriteria();
            //设置参数
            criteria.andUserIdEqualTo(username);
            criteria.andStatusEqualTo("1");
            //执行查询
            List<TbOrder> orderList = orderMapper.selectByExample(example);
            //准备支付金额
            Double payment = 0d;
            //计算支付总金额
            for (TbOrder tbOrder : orderList) {
                payment += tbOrder.getPayment().doubleValue();
            }

            //准备订单号 支付订单号
            IdWorker idWorker = new IdWorker();
            String payOrderId = idWorker.nextId()+"";


            //创建map，封装统一下单参数
            Map maps = new HashMap();
            maps.put("appid",appid);
            maps.put("mch_id",partner);
            maps.put("nonce_str", WXPayUtil.generateNonceStr());
            maps.put("body","品优购");
            maps.put("out_trade_no",payOrderId);
            maps.put("total_fee",payment+"");
            maps.put("notify_url",notifyurl);
            maps.put("trade_type","NATIVE");
            maps.put("spbill_create_ip","127.0.0.1");

            //把map参数转换为带有签名的xml
            String xmlParam = WXPayUtil.generateSignedXml(maps, partnerkey);


            //调用微信支付统一下单接口
            //https://api.mch.weixin.qq.com/pay/unifiedorder
            //使用httpclicent 远程调用工具
            HttpClient httpClient = new HttpClient(payUrl);
            //是否是https请求
            httpClient.setHttps(true);
            //设置参数
            httpClient.setXmlParam(xmlParam);
            //发送请求方式
            httpClient.post();

            //获取回调结果
            String content = httpClient.getContent();

            //把返回值xml格式转换为map对象
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);
            //返回订单号
            stringStringMap.put("out_trade_no",payOrderId);
            stringStringMap.put("total_fee",payment+"");

            return stringStringMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 需求：查询支付订单状态
     * 参数：String out_trade_no
     * 返回值：PygResult
     */
    public Map queryPayStatus(String out_trade_no) {

        try {
            //创建map,封装参数
            Map param = new HashMap();
            param.put("appid", appid);//公众账号ID
            param.put("mch_id", partner);//商户号
            param.put("out_trade_no", out_trade_no);//订单号
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //把参数转换为xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);

            //创建httpClient对象，向微信支付品台发送请求,检查支付订单状态
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            //设置请求是否是https
            httpClient.setHttps(true);
            //设置参数
            httpClient.setXmlParam(xmlParam);
            //请求方式
            httpClient.post();

            //获取请求结果
            String content = httpClient.getContent();

            //把xml格式返回值转换为map
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);


            return  stringStringMap;


        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Autowired
    private TbPayLogMapper logMapper;


    /*
    * 交易日志
    * */
    @Override
    public void findByOutTradeNo(Long outTradeNo,Long totalFee,String orderId) {
        TbPayLog payLog = new TbPayLog();
        //设置支付订单号
        payLog.setOutTradeNo(outTradeNo+"");
        //设置创建日期
        payLog.setCreateTime(new Date());
        //设置支付金额
        payLog.setTotalFee(totalFee);
        //设置用户ID
        //String username = request.getRemoteUser();
        payLog.setUserId("ZHJ");
        //设置交易号码
        payLog.setTransactionId(outTradeNo+"".substring(7)+new Random().nextInt(2000000)+"");
        //设置完成时间
        payLog.setPayTime(new Date());
        //设置交易状态
        payLog.setTradeState("1");
        //设置编号列表
        TbOrderExample example = new TbOrderExample();
        TbOrderExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("2");
        List<TbOrder> tbOrders = orderMapper.selectByExample(example);

        ArrayList<String> orderIdList = new ArrayList<>();
        for (TbOrder tbOrder : tbOrders) {
            orderIdList.add(tbOrder.getOrderId()+"");
        }
        payLog.setOrderList(tbOrders.toString());
        //设置支付类型

        logMapper.insert(payLog);
    }

    /*
    * 更改订单支付状态
    * */
    @Override
    public void updateStatus(String orderId) {
        TbOrder order = new TbOrder();
        order.setStatus("2");
        order.setOrderId(Long.parseLong(orderId));
        orderMapper.updateByPrimaryKeySelective(order);
    }
}
