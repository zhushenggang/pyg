package com.pyg.listener;

import com.aliyuncs.exceptions.ClientException;
import com.pyg.utils.SmsUtils;
import org.apache.activemq.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by on 2018/8/23.
 */
@Component
public class SmsListener {

    //注入工具类对象
    @Autowired
    private SmsUtils smsUtils;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String, String> smsMap) {

        try {
            //消息map中包含：手机号，签名，模板code,验证码
            String phone = smsMap.get("phone");
            String sign_name = smsMap.get("sign_name");
            String template_code = smsMap.get("template_code");
            String code = smsMap.get("code");
            //调用发送短信方法
            smsUtils.sendSms(phone, sign_name, template_code, code);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
