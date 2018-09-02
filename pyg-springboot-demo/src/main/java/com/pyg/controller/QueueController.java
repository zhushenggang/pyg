package com.pyg.controller;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by on 2018/8/23.
 */
@RestController
public class QueueController {

    //注入发送消息模板对象
   @Autowired
   private JmsTemplate jmsTemplate;

    /**
     * 需求：发送消息给mq消息服务器
     */
    @RequestMapping("sendSms/{code}")
    public String sendSms(@PathVariable String code){
        jmsTemplate.convertAndSend("one-queue",code);
        return "success";
    }


    /**
     * 需求：发送消息，测试发送短信
     */
    @RequestMapping("send")
    public String sendSms(){
        Map<String,String> smsMap = new HashMap<>();
        smsMap.put("phone","13412809628");
        smsMap.put("sign_name","黑马");
        smsMap.put("template_code","SMS_125028677");
        smsMap.put("code","888888");
        jmsTemplate.convertAndSend("sms",smsMap);
        return "success";
    }

    /**
     * 需求：接受消息
     *
     */
    @JmsListener(destination = "one-queue")
    public void receive(Message message){

        try {
            TextMessage m = (TextMessage) message;
            String text = m.getText();
            System.out.println("接受消息:"+text);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}
