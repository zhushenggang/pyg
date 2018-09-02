package com.pyg.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/17.
 */
@Component
public class SolrUtils {

    //注入商品mapper接口代理对象
    @Autowired
    private TbItemMapper itemMapper;

    //注入solr模板对象，实现索引库数据导入
    @Autowired
    private SolrTemplate solrTemplate;


    /**
     * 需求：导入数据库到到索引库
     *
     * @param
     */
    public void importDatabaseToSolrIndex() {
        //创建example对象
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        //设置查询参数，查询已启用的商品
        criteria.andStatusEqualTo("1");
        //查询数据库数据
        List<TbItem> itemList = itemMapper.selectByExample(example);

        //封装规格属性数据
        //规格数据是动态域数据，因此规格数据使用map来进行封装
        for (TbItem tbItem : itemList) {
            //获取规格数据
            String spec = tbItem.getSpec();
            //把规格数据字符串转换map对象
            Map<String,String> maps = (Map<String, String>) JSON.parse(spec);
            //设置规格属性值
            tbItem.setSpecMap(maps);

        }

        //保存数据到索引库
        solrTemplate.saveBeans(itemList);

        //提交
        solrTemplate.commit();

    }

    //微服务
    //java -jar xx.jar
    public static void main(String[] args) {

        //加载spirng配置文件
        ApplicationContext app =
                new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        //从spring容器中获取对象
        SolrUtils solrUtils = app.getBean(SolrUtils.class);
        //调用方法
        solrUtils.importDatabaseToSolrIndex();
    }


}
