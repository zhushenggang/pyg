package com.pyg.jedis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

/**
 * Created by on 2018/8/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class MyJedis {

    //注入reids模板对象
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 需求：操作String类型数据结构方法
     * 方法：添加
     */
    @Test
    public void valueOperationsSet() {
        //给string设置值
        redisTemplate.boundValueOps("username").set("张无忌");
    }

    /**
     * 需求：操作String类型数据结构方法
     * 方法：获取值
     */
    @Test
    public void valueOperationsGet() {
        //给string设置值
        String username = (String) redisTemplate.boundValueOps("username").get();
        System.out.println(username);
    }

    /**
     * 需求：操作String类型数据结构方法
     * 方法：删除方法
     */
    @Test
    public void valueOperationsDelete() {
        //给string设置值
        redisTemplate.delete("redis_cart");
    }


    /**
     * 需求：set集合类型数据操作
     * 方法：添加
     */
    @Test
    public void SetOperationsSet() {
        redisTemplate.boundSetOps("itcast").add("猪八戒");
        redisTemplate.boundSetOps("itcast").add("孙悟空");
        redisTemplate.boundSetOps("itcast").add("沙和尚");

    }

    /**
     * 需求：set集合类型数据操作
     * 方法：添加
     */
    @Test
    public void SetOperationsGet() {
        Set nodes = redisTemplate.boundSetOps("itcast").members();
        System.out.println(nodes);
    }

    /**
     * 需求：list集合类型数据操作
     * 方法：添加
     */
    @Test
    public void ListOperationsSet() {
        redisTemplate.boundListOps("userList").rightPush("赵敏");
        redisTemplate.boundListOps("userList").rightPush("周芷若");
        redisTemplate.boundListOps("userList").rightPush("小昭");
    }

    /**
     * 需求：list集合类型数据操作
     * 方法：查询
     */
    @Test
    public void ListOperationsGet() {
       // String name = (String) redisTemplate.boundListOps("userList").rightPop();
        List userList = redisTemplate.boundListOps("userList").range(0, -1);
        System.out.println(userList);
        //System.out.println(name);
    }



    /**
     * 需求：Hash集合类型数据操作
     * 方法：添加
     */
    @Test
    public void HashOperationsSet() {
      redisTemplate.boundHashOps("user").put("username","张君宝");
      redisTemplate.boundHashOps("user").put("age","12");
      redisTemplate.boundHashOps("user").put("address","少林寺");
    }

    /**
     * 需求：Hash集合类型数据操作
     * 方法：查询
     */
    @Test
    public void HashOperationsGet() {
        String username = (String) redisTemplate.boundHashOps("user").get("username");
        System.out.println(username);
    }


}
