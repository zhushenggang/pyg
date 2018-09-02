package com.pyg.mq;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by on 2018/8/20.
 */
public class SpringMQ {

    @Test
    public void receiveMessage() {
        ApplicationContext app =
                new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-mq-comsumer.xml");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
