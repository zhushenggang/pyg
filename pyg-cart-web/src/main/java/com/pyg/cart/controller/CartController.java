package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.cart.service.CartService;
import com.pyg.utils.CookieUtil;
import com.pyg.utils.PygResult;
import com.pyg.vo.Cart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by on 2018/8/26.
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    //注入远程购物车服务对象
    @Reference(timeout = 1000000)
    private CartService cartService;

    /**
     * 需求:添加购物车
     * 业务场景：
     * 1，未登录 --- 添加购物车(cookie)
     * 2，登录 ---添加购物车(redis)
     * 业务分析：
     * 1,查询购物车列表（redis/cookie）
     * 2,把新的商品添加购物车原来的购物车里面
     * 3,如果没有登录，使用cookie存储购物车
     * 4，一旦登录，必须把cookie购物车合并到redis购物车
     * 5，如果登录状态，添加redis购物车
     * 6,购物车删除，数量修改，清空
     */
    @RequestMapping("addGoodsToCartList/{itemId}/{num}")
    @CrossOrigin(origins = "http://www.pyg.com")

    public PygResult addGoodsToCartList(@PathVariable Long itemId,
                                        @PathVariable Integer num,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {

        try {
            //获取用户登录信息
            String username = "ZHJ";
            //1,查询购物车数据
            List<Cart> cartList = this.findCartList(request,response);
            //2,添加购物车数据，把新的商品添加购物车cartList购物车列表中
            //组装购物车数据结构
            //List<Cart<List<Orderitem>>>
            cartList = cartService.addCartList(cartList, itemId, num);

            //判断用户是否处于登录状态
            if (username==null) {
                //未登录状态
                //把购物车数据添加到cookie购物车
                CookieUtil.setCookie(request,
                        response,
                        "cookie_cart",
                        JSON.toJSONString(cartList), 260000, true);
            } else {
                //把购物车数据存储在redis购物车
                cartService.addRedisCart(cartList, username);


            }

            return new PygResult(true, "添加成功");

        } catch (Exception e) {
            e.printStackTrace();

            return new PygResult(false, "添加失败");
        }


    }


    /**
     * 需求：查询购物车列表，在购物车列表的基础上实现购物车操作（添加）
     * 业务分析：
     * 1，未登录
     * 查询cookie购物车
     * 2，redis状态查询redis购物车
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {

        //获取用户登录信息
        //String userId = request.getRemoteUser();
        String userId = "ZHJ";
        //此时处于未登录
        //获取cookie购物车中购物车数据
        String cookie_cart = CookieUtil.getCookieValue(request, "cookie_cart", true);

        //判断cook购物车数据是否为空
        if (cookie_cart == null || "".equals(cookie_cart)) {
            cookie_cart = "[]";
        }

        //把cookie购物车数据转换购物车集合对象
        List<Cart> cookieCartList = JSON.parseArray(cookie_cart, Cart.class);


        //判断用户是否处于登录状态
        if (userId==null) {

            return cookieCartList;


        } else {
            //否则处于登录状态
            //查询redis购物车
            List<Cart> redisCartList = cartService.findRedisCartList(userId);

            //判断cookie购物车是否为空
            if(cookieCartList!=null && cookieCartList.size()>0){
                //合并购物车
              redisCartList =   cartService.mergeCart(redisCartList,cookieCartList);
              //清空cookie购物车
                CookieUtil.setCookie(request,response,
                        "cookie_cart",
                        "",
                        0,
                        true);


            }
            return redisCartList;
        }

    }

    /**
     * 需求：根据购物车SKU的ID返回选定下单商品
     */
    @RequestMapping("/findSelectItem/{ids}")
    public List<Cart> findSelectItem(@PathVariable Long[] ids){
        //从redis中查询购物车数据
        List<Cart> selectItem = cartService.findSelectItem(ids);
        return selectItem;
    }

}
