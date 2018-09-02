package com.pyg.controller;

import com.pyg.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by on 2018/8/23.
 */
@RestController
public class UserController {

    //注入环境变量值
    @Autowired
    private Environment env;

    @RequestMapping("/hello")
    public String showHello(){
        return "hello,spring boot! url = "+env.getProperty("url");
    }

    @RequestMapping("/hello2")
    public TbItem sohwItem(){
        //创建对象
        TbItem item = new TbItem();
        item.setId(10000L);
        item.setTitle("牙刷");
        item.setSellPoint("非常好使");
        return item;
    }

}
