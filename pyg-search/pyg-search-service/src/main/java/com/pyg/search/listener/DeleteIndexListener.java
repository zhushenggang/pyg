package com.pyg.search.listener;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by on 2018/8/20.
 * 接受消息监听器
 * 实现同步索引库操作
 * 1，接受消息
 * 2，把消息转换集合对象
 * 3，使用solr模板把集合保存到索引库即可实现索引库同步
 */
public class DeleteIndexListener implements MessageListener{

    //注入itemMapper接口代理对象
    @Autowired
    private TbItemMapper itemMapper;

    //注入solr模板对象
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        //接受消息
        try {
            if(message instanceof ObjectMessage){
                ObjectMessage m = (ObjectMessage) message;
                Long[] ids = (Long[]) m.getObject();

                //定义集合
                List<String> itemIds = new ArrayList<>();

                for (Long id : ids) {
                    //创建example查询
                    TbItemExample example = new TbItemExample();
                    TbItemExample.Criteria criteria = example.createCriteria();
                    //设置外键
                    criteria.andGoodsIdEqualTo(id);
                    //查询
                    List<TbItem> itemList = itemMapper.selectByExample(example);

                    for (TbItem tbItem : itemList) {
                        itemIds.add(tbItem.getId()+"");
                    }

                }

                //删除
                solrTemplate.deleteById(itemIds);

                //提交
                solrTemplate.commit();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
