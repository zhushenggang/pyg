package com.pyg.service;

import com.pyg.manager.service.SellerService;
import com.pyg.pojo.TbSeller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by on 2018/8/10.
 * 用户自定义认证
 * 1，根据用户名查询数据密码
 * 2，通过认证管理匹配用户名和密码
 * 3，匹配成功，用户登录成功
 */
public class UserDetailsServiceImpl implements UserDetailsService{

    //注入远程sellerService对象
    private SellerService sellerService;

    public SellerService getSellerService() {
        return sellerService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询数据库
        TbSeller seller = sellerService.findOne(username);
        //定义集合封装角色
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        //判断用户是否存在
        if(seller!=null){
            if(seller.getStatus().equals("1")){
                return new User(username,seller.getPassword(),authorities);
            }
        }
        return null;
    }
}
