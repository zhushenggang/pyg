package com.pyg.solrj;

import com.pyg.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by on 2018/8/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-solr.xml")
public class MySOLR {

    //注入solr模板对象
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 需求：索引库添加操作
     * 1,如果id存在，修改
     * 2，如果id不存在，添加
     */
    @Test
    public void addIndex(){
        //创建商品对象
        TbItem item = new TbItem();
        item.setId(1000000000L);
        item.setTitle("黑妹药膏");
        item.setSellPoint("非常黑,黑的吓人");
        item.setBrand("黑妹");

        solrTemplate.saveBean(item);
        solrTemplate.commit();

    }

    /**
     * 需求：根据id删除
     */
    @Test
    public  void deleteById(){

        Query query = new SimpleQuery("*:*");

        //solrTemplate.deleteById("1000000000");
        //根据查询删除
        solrTemplate.delete(query);
        //批量删除
        //提交
        solrTemplate.commit();
    }

    /**
     * 需求：分页查询
     */
    @Test
    public  void queryIndexForPage(){

        //创建查询对象，封装查询对象
        Query query = new SimpleQuery(new Criteria().expression("*:*"));

        //设置分页查询条件
        //设置分页查询起始位置
        query.setOffset(0);
        //设置每页显示条数
        query.setRows(10);

        //查询
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        //获取查询总记录数
        long totalElements = scoredPage.getTotalElements();
        System.out.println("总记录数："+totalElements);

        //获取总记录
        List<TbItem> list = scoredPage.getContent();
        for (TbItem tbItem : list) {

            System.out.println(tbItem.getTitle());
        }


    }

    /**
     * 需求：条件查询
     */
    @Test
    public  void queryIndexForCondition(){

        //创建查询对象，封装查询对象
        Query query = new SimpleQuery("*:*");

        //创建criteria对象，设置查询条件
        //item_title:黑妹
        Criteria criteria = new Criteria("item_title").is("黑妹");

        //把条件对象添加到query对象中
        query.addCriteria(criteria);

        //查询
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        //获取查询总记录数
        long totalElements = scoredPage.getTotalElements();
        System.out.println("总记录数："+totalElements);

        //获取总记录
        List<TbItem> list = scoredPage.getContent();
        for (TbItem tbItem : list) {

            System.out.println(tbItem.getTitle());
        }


    }

    /**
     * 需求：高亮查询
     */
    @Test
    public  void queryIndexForHighLight(){

        //创建高亮查询对象，封装查询对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        //创建criteria对象，设置查询条件
        //item_title:黑妹
        Criteria criteria = new Criteria("item_title").is("黑妹");

        //把条件对象添加到query对象中
        query.addCriteria(criteria);


        //创建高亮对象，设置高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<font color='red'>");
        highlightOptions.setSimplePostfix("</font>");

        //把高亮对象添加到查询对象中
        query.setHighlightOptions(highlightOptions);


        //查询
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取查询总记录数
        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数："+totalElements);

        //获取总记录
        List<TbItem> list = tbItems.getContent();
        for (TbItem tbItem : list) {

            //获取高亮
            List<HighlightEntry.Highlight> highlights = tbItems.getHighlights(tbItem);
            String name = highlights.get(0).getSnipplets().get(0);

            System.out.println(name);
        }


    }
}
