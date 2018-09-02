package com.pyg.html.listener;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbGoodsDescMapper;
import com.pyg.mapper.TbGoodsMapper;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.*;
import com.pyg.utils.FMUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.soap.Text;
import java.util.*;

/**
 * Created by on 2018/8/23.
 * 需求：接受消息，同步静态页面
 * 业务分析：
 * 商品数据：添加，修改，删除，静态页面数据不能及时发生变化，静态页面数据和数据库的数据不一致。
 * 因此需要把数据库的数据及时同步到静态页面。
 * 如何同步？
 * 使用activeMQ消息中间件同步静态页面。
 * 业务流程：
 * 1，商品审核通过，发生消息到activeMQ消息服务器
 * 2，静态页面同步服务监听消息空间
 * 3，发现消息空间有消息后，接受消息
 * 4，根据消息准备模板页面需要的数据
 * 5，生成静态页面，实现静态页面同步
 */
public class HtmlListener implements MessageListener {

    //注入商品spumapper接口代理对象
    @Autowired
    private TbGoodsMapper goodsMapper;

    //注入商品描述对象
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    //注入商品mapper接口代理对象
    @Autowired
    private TbItemMapper itemMapper;

    //注入商品分类接口代理对象
    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Override
    public void onMessage(Message message) {

        try {
            if (message instanceof TextMessage) {
                TextMessage m = (TextMessage) message;
                //接受消息
                String itemJson =  m.getText();
                //把json格式数据转换为集合对象
                //手机，服装
                List<TbItem> itemList = JSON.parseArray(itemJson, TbItem.class);

                //定义set集合封装goodsId
                Set<Long> nodes = new HashSet<Long>();

                //循环sku列表，准备spu数据
                for (TbItem tbItem : itemList) {

                    nodes.add(tbItem.getGoodsId());
                }
                //循环spu的商品id
                for (Long goodsId : nodes) {

                    //查询商品spu数据 手机
                    TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
                    //查询商品描述对象 手机
                    TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
                    //查询商品sku
                    TbItemExample example = new TbItemExample();
                    //创建criteria对象
                    TbItemExample.Criteria criteria = example.createCriteria();
                    //设置查询参数
                    criteria.andGoodsIdEqualTo(goodsId);

                    //执行查询
                    List<TbItem> skuList = itemMapper.selectByExample(example);

                    //商品分类
                    TbItemCat cat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
                    TbItemCat cat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
                    TbItemCat cat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());

                    //创建map对象，封装模板需要的数据
                    Map maps = new HashMap();
                    maps.put("goods",goods);
                    maps.put("goodsDesc",goodsDesc);
                    maps.put("itemList",skuList);
                    maps.put("cat1",cat1.getName());
                    maps.put("cat2",cat2.getName());
                    maps.put("cat3",cat3.getName());


                    //创生成静态页面工具类对象
                    FMUtils fm = new FMUtils();
                    fm.ouputFile("item.ftl",goodsId+".html",maps);
                }
            }




        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
