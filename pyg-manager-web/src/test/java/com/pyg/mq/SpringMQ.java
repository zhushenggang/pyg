package com.pyg.mq;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Created by on 2018/8/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/mq-producer.xml")
public class SpringMQ {

    //注入模板对象
    @Autowired
    private JmsTemplate jmsTemplate;

    //注入消息发送目的地
    @Autowired
    private ActiveMQQueue activeMQQueue;

    //发布订阅
    @Autowired
    private ActiveMQTopic activeMQTopic;

    /**
     * 需求：点对点模式发送
     */
    @Test
    public void sendMessageWithP2P(){
        jmsTemplate.send(activeMQQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("spring整合mQ,点对点发送消息，接受消息");
            }
        });
    }

    /**
     * 需求：发布订阅模式发送
     */
    @Test
    public void sendMessageWithPS(){
        jmsTemplate.send(activeMQTopic, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("spring整合mQ........");
            }
        });
    }

}
