package com.pyg.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

import javax.jms.*;

/**
 * Created by on 2018/8/20.
 */
public class MessageProducer {

    /**
     * 需求：发送消息
     * 模式：点对点模式
     */
    @Test
    public void sendMessage() throws Exception {

        //指定消息服务器地址：协议，ip,端口
        String brokerURL = "tcp://192.168.66.66:61616";
        //创建工厂对象，连接消息服务器
        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
        //从工厂对象中获取连接对象
        Connection connection = cf.createConnection();
        //开启连接
        connection.start();

        //从连接中获取回话对象
        //参数1：true:表示使用事务提交消息确认模式 Session.SESSION_TRANSACTED false:忽略事务提交确认模式
        //参数2：AUTO_ACKNOWLEDGE 自动确认模式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建消息发送目的地： 开辟服务器消息空间，且给消息空间起一个名称
        Queue queue = session.createQueue("myQueue");

        //创建消息发送者,且指定消息发送目的地
        javax.jms.MessageProducer producer = session.createProducer(queue);

        //创建消息对象
        TextMessage message = new ActiveMQTextMessage();
        message.setText("唐僧师徒四人一路打怪升级，最后终于高薪就业");
        //发送消息
        producer.send(message);

        //关闭资源
        producer.close();
        session.close();
        connection.close();



    }


    /**
     * 需求：发送消息
     * 模式：发布订阅模式
     */
    @Test
    public void sendMessageWithPs() throws Exception {

        //指定消息服务器地址：协议，ip,端口
        String brokerURL = "tcp://192.168.66.66:61616";
        //创建工厂对象，连接消息服务器
        ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
        //从工厂对象中获取连接对象
        Connection connection = cf.createConnection();
        //开启连接
        connection.start();

        //从连接中获取回话对象
        //参数1：true:表示使用事务提交消息确认模式 Session.SESSION_TRANSACTED false:忽略事务提交确认模式
        //参数2：AUTO_ACKNOWLEDGE 自动确认模式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建消息发送目的地： 开辟服务器消息空间，且给消息空间起一个名称
        //点对点模式发送消息 ， 发布订阅模式发现消息 唯一区别 就在于存储消息的目的地数据结构不一样。
        //点对点：queue
        //发布订阅：topic
        Topic topic = session.createTopic("myTopic");

        //创建消息发送者,且指定消息发送目的地
        javax.jms.MessageProducer producer = session.createProducer(topic);

        //创建消息对象
        TextMessage message = new ActiveMQTextMessage();
        message.setText("唐僧师徒四人一路打怪升级，最后终于高薪就业，年薪100w");
        //发送消息
        producer.send(message);

        //关闭资源
        producer.close();
        session.close();
        connection.close();



    }

}
