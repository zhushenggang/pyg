package com.pyg.pay.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pay.service.PayService;
import com.pyg.pojo.TbPayLog;
import com.pyg.utils.PygResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * Created by on 2018/8/29.
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    //注入支付服务对象
    @Reference(timeout = 10000000)
    private PayService payService;

    /**
     * 需求：生成二维码
     * 请求：./pay/createQrCode
     * 参数：
     * 返回值：Map
     */
    @RequestMapping("createQrCode")
    public Map createQrCode(HttpServletRequest request) {
        //获取当前用户登录名
        //String username = request.getRemoteUser();
        String username = "ZHJ";
        //传递当前用户userid,查询支付金额
        Map map = payService.createQrCode(username);

        return map;

    }

    /**
     * 需求：查询支付订单状态
     * 请求：../pay/queryPayStatus/"+out_trade_no
     * 参数：String out_trade_no
     * 返回值：PygResult
     */
    @RequestMapping("queryPayStatus/{out_trade_no}/{orderId}")
    public PygResult queryPayStatus(@PathVariable String out_trade_no,@PathVariable String orderId) {

        try {
            //设置5分钟超时，重新生成二维码
            int x=0;
            while (true) {
                //调用服务层
                Map maps = payService.queryPayStatus(out_trade_no);

                if (maps == null) {
                    //支付出错
                    return new PygResult(false, "支付出错");
                }

                //获取订单状态
                String trade_state = (String) maps.get("trade_state");

                //判断
                if (trade_state.equals("SUCCESS")) {
                    payService.updateStatus(orderId);
                    return new PygResult(true, "支付成功");

                }
                //线程睡眠3s,每隔3秒查询一次
                Thread.sleep(3000);

                x++;

                if(x==100){
                    return  new PygResult(false,"二维码超时");
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "支付失败");
        }


    }



    /*
    * 生成日志文件
    * */
    @RequestMapping("payLog/{outTradeNo}/{totalFee}/{orderId}")
    public void payLog(@PathVariable Long outTradeNo,@PathVariable Long totalFee ,@PathVariable String orderId){

        //设置订单编号列表
        payService.findByOutTradeNo(outTradeNo,totalFee,orderId);

    }

}
