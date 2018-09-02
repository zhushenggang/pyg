package com.pyg.fm.utils;

import com.pyg.mapper.TbGoodsDescMapper;
import com.pyg.mapper.TbGoodsMapper;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.*;
import com.pyg.utils.FMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import sun.font.CreatedFontTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/22.
 */
@Component
public class PygHtmlUtils {

    //注入spu 商品mapper接口代理对象
    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    //注入sku 商品mapper接口代理对象
    @Autowired
    private TbItemMapper itemMapper;

    //注入分类mapper接口代理对象
    @Autowired
    private TbItemCatMapper itemCatMapper;


    public void genHtml(){

        try {
            //查询所有商品
            TbGoodsExample example = new TbGoodsExample();
            TbGoodsExample.Criteria criteria = example.createCriteria();
            //设置查询参数
            criteria.andAuditStatusEqualTo("1");
            criteria.andIsMarketableEqualTo("1");
            criteria.andIsDeleteEqualTo("1");
            criteria.andCategory1IdIsNotNull();
            criteria.andCategory2IdIsNotNull();
            criteria.andCategory3IdIsNotNull();

            //查询
            List<TbGoods> goodsList = goodsMapper.selectByExample(example);

            //根据商品id查询商品描述信息
            for (TbGoods tbGoods : goodsList) {

                //查询商品描述信息
                TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(tbGoods.getId());


                //创建tbItemExample对象
                TbItemExample example1 = new TbItemExample();
                TbItemExample.Criteria criteria1 = example1.createCriteria();
                //设置查询参数
                criteria1.andGoodsIdEqualTo(tbGoods.getId());

                //查询和商品相关联的sku
                List<TbItem> itemList = itemMapper.selectByExample(example1);

                //创建map封装数据
                Map maps = new HashMap();
                maps.put("goods",tbGoods);
                maps.put("goodsDesc",goodsDesc);
                maps.put("itemList",itemList);

                //查询一级分类
                TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
                maps.put("cat1",tbItemCat.getName());

                //查询二级分类
                TbItemCat tbItemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
                maps.put("cat2",tbItemCat2.getName());

                //查询一级分类
                TbItemCat tbItemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
                maps.put("cat3",tbItemCat3.getName());


                //创建生成静态页面工具类对象
                FMUtils fmUtils = new FMUtils();
                fmUtils.ouputFile("item.ftl",tbGoods.getId()+".html",maps);

            }



        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        //加载spring配置文件
        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        //获取spring容器中PygHtmlUtils对象
        PygHtmlUtils htmlUtils = app.getBean(PygHtmlUtils.class);
        //生成html静态页面
        htmlUtils.genHtml();
    }
}
