package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.SearchService;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/17.
 */
@Service
public class SearchServiceImpl implements SearchService {

    //注入solr模板对象
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 需求：根据关键词实现索引库搜索
     */
    public Map searchList(Map searchMap) {

        //创建query查询对象，直接高亮查询对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        //1,获取关键词
        //主关键词查询
        String keywords = (String) searchMap.get("keywords");

        //创建criteria对象
        Criteria criteria = null;

        //判断关键词是否为空
        if (keywords != null && !"".equals(keywords)) {
            criteria = new Criteria("item_keywords").is(keywords);
        } else {
            criteria = new Criteria().expression("*:*");
        }

        //把条件设置到query查询对象
        query.addCriteria(criteria);


        //2,根据分类来进行过滤查询
        //从searchMap获取分类参数值
        String category = (String) searchMap.get("category");
        //判断分类是否为空
        if (category != null && !"".equals(category)) {
            //创建criteria对象
            Criteria criteria1 = new Criteria("item_category").is(category);
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery(criteria1);

            //把过滤条件添加到查询对象中
            query.addFilterQuery(filterQuery);

        }


        //3,根据品牌进行过滤查询
        //从searchMap获取品牌参数值
        String brand = (String) searchMap.get("brand");
        //判断分类是否为空
        if (brand != null && !"".equals(brand)) {
            //创建criteria对象
            Criteria criteria1 = new Criteria("item_brand").is(brand);
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery(criteria1);

            //把过滤条件添加到查询对象中
            query.addFilterQuery(filterQuery);
        }


        //4，根据规格参数进行过滤查询
        //从searchMap中获取规格属性值
        Map<String, String> specMap = (Map) searchMap.get("spec");
        //判断规格属性是否存在
        if (specMap != null) {
            //循环遍历specMap
            for (String key : specMap.keySet()) {
                //创建criteria对象
                Criteria criteria1 = new Criteria("item_spec_" + key).is(specMap.get(key));
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                //把过滤条件添加到查询对象中
                query.addFilterQuery(filterQuery);
            }
        }

        //5,价格过滤查询
        //从searchMap中获取价格数据
        //0-500元500-1000元1000-1500元1500-2000元2000-3000元 3000-*元以上
        String price = (String) searchMap.get("price");
        //判断价格是否存在值
        if (price != null && !"".equals(price)) {
            //截取价格
            String[] prices = price.split("-");

            //判断价格区间
            if (!prices[0].equals("0")) {
                //创建criteria对象，设置查询条件
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(prices[0]);
                //创建过滤对象
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }

            if (!prices[1].equals("*")) {
                //创建criteria对象，设置查询条件
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(prices[1]);
                //创建过滤对象
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }


        }

        //6,排序查询
        //获取排序字段
        String solrField = (String) searchMap.get("sortField");
        //获取排序方法 ASC,DESC
        String sort = (String) searchMap.get("sort");

        //判断排序字段是否有值
        if (solrField != null && !"".equals(sort) && sort != null && !"".equals(sort)) {
            //判断
            if (sort.equals("ASC")) {
                //创建排序对象
                Sort sort2 = new Sort(Sort.Direction.ASC, "item_" + solrField);
                query.addSort(sort2);

            } else if (sort.equals("DESC")) {
                //创建排序对象
                Sort sort2 = new Sort(Sort.Direction.DESC, "item_" + solrField);
                query.addSort(sort2);
            }

        }


        //7,分页查询
        //获取当前页码值
        Integer page = (Integer) searchMap.get("page");
        //获取每页显示的条数
        Integer pageSize = (Integer) searchMap.get("pageSize");

        //判断当前页为空
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 40;
        }

        //计算查询起始页
        int startNo = (page-1)*pageSize;
        //设置分页
        query.setOffset(startNo);
        query.setRows(pageSize);


        //8,创建高亮对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置高亮字段
        highlightOptions.addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<font color='red'>");
        //设置高亮后缀
        highlightOptions.setSimplePostfix("</font>");

        //把高亮对象添加到查询对象
        query.setHighlightOptions(highlightOptions);


        //高亮查询
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取高亮结果
        List<TbItem> list = highlightPage.getContent();
        //循环搜索结果集合
        for (TbItem tbItem : list) {
            //获取高亮
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(tbItem);
            //判断高亮是否存在
            if (highlights != null && highlights.size() > 0) {
                HighlightEntry.Highlight highlight = highlights.get(0);
                //获取高亮
                List<String> highlightSnipplets = highlight.getSnipplets();
                //把高亮字段设置会商品对象
                tbItem.setTitle(highlightSnipplets.get(0));

            }

        }

        //创建map对象
        Map maps = new HashMap();
        //总页码
        maps.put("page",page);
        maps.put("totalPages", highlightPage.getTotalPages());
        maps.put("rows", list);


        return maps;
    }
}
