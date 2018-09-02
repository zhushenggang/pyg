package com.pyg.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by on 2018/8/10.
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 需求：在登录成功页面展示登录用户名
     */
    @RequestMapping("showName")
    public Map showLoginName(){
        //获取登录用户信息
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //创建map
        Map maps = new HashMap();
        maps.put("loginName",name);

        return  maps;
    }

}
