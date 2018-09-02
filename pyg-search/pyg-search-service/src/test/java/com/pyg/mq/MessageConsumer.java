package com.pyg.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

import javax.jms.*;

/**
 * Created by on 2018/8/20.
 */
public class MessageConsumer {


    /**
     * 需求：接受消息
     * 模式：点对点模式
     */
    @Test
    public void receiveMessage() throws Exception {

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

        //指定接受消息目的地，此消息目的地必须和发送消息目的地一致
        Queue queue = session.createQueue("myQueue");

        //指定消息接受者
        javax.jms.MessageConsumer consumer = session.createConsumer(queue);

        //监听模式接受消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    if(message instanceof TextMessage){
                        TextMessage m = (TextMessage) message;

                        //获取消息
                        String text = m.getText();

                        System.out.println("接受消息是："+text);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //等待输入，让端口阻塞状态
        System.in.read();


        //关闭资源
        consumer.close();
        session.close();
        connection.close();



    }


    /**
     * 需求：接受消息
     * 模式：发布订阅模式
     */
    @Test
    public void receiveMessageWithPs() throws Exception {

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

        //指定接受消息目的地，此消息目的地必须和发送消息目的地一致
        //创建消息发送目的地： 开辟服务器消息空间，且给消息空间起一个名称
        //点对点模式发送消息 ， 发布订阅模式发现消息 唯一区别 就在于存储消息的目的地数据结构不一样。
        //点对点：queue
        //发布订阅：topic
        Topic topic = session.createTopic("myTopic");

        //指定消息接受者
        javax.jms.MessageConsumer consumer = session.createConsumer(topic);

        //监听模式接受消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    if(message instanceof TextMessage){
                        TextMessage m = (TextMessage) message;

                        //获取消息
                        String text = m.getText();

                        System.out.println("接受消息是："+text);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //等待输入，让端口阻塞状态
        System.in.read();


        //关闭资源
        consumer.close();
        session.close();
        connection.close();



    }









}
