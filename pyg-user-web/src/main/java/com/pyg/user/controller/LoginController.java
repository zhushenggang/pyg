package com.pyg.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by on 2018/8/25.
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 需求：获取用户登录名
     */
    @RequestMapping("loadUserInfo")
    public Map loadUserInfo(){
        //获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //创建map
        Map maps = new HashMap();
        maps.put("loginName",username);

        return maps;
    }

}
