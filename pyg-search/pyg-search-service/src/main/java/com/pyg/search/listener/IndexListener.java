package com.pyg.search.listener;

import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * Created by on 2018/8/20.
 * 接受消息监听器
 * 实现同步索引库操作
 * 1，接受消息
 * 2，把消息转换集合对象
 * 3，使用solr模板把集合保存到索引库即可实现索引库同步
 */
public class IndexListener implements MessageListener{

    //注入solr模板对象
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        //接受消息
        try {
            if(message instanceof TextMessage){
                TextMessage m = (TextMessage) message;
                //获取消息
                String itemJson = m.getText();
                //把消息转换成json对象
                List<TbItem> itemList = JSON.parseArray(itemJson, TbItem.class);
                //同步索引库
                solrTemplate.saveBeans(itemList);

                //提交
                solrTemplate.commit();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
