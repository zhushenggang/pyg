package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.manager.service.GoodsService;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbItem;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import com.pyg.vo.Goods;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage/{page}/{rows}")
    public PageResult findPage(@PathVariable int page, @PathVariable int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public PygResult add(@RequestBody Goods goods) {
        try {
            goodsService.add(goods);
            return new PygResult(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public PygResult update(@RequestBody TbGoods goods) {
        try {
            goodsService.update(goods);
            return new PygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbGoods findOne(@PathVariable Long id) {
        return goodsService.findOne(id);
    }

    //注入消息发送模板对象
    @Autowired
    private JmsTemplate jmsTemplate;

    //注入发送消息目的地
    @Autowired
    private ActiveMQTopic activeMQTopic;
    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete/{ids}")
    public PygResult delete(@PathVariable Long[] ids) {
        try {
            goodsService.delete(ids);
            //发送消息
            //jmsTemplate.convertAndSend("",ids);
            jmsTemplate.send("solr_index_delete", new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage message = session.createObjectMessage(ids);
                    return message;
                }
            });
            //返回
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }



    /**
     * 运营商系统审核商品
     * 同步索引库，同步静态页面
     * 1，审核商品通过
     * 2，使用activeMQ发送消息
     * 发送消息内容是什么？
     * 把改变的商品内容发送给搜索服务。
     */
    @RequestMapping("updateStatus/{status}/{ids}")
    public PygResult updateStatus(@PathVariable String status, @PathVariable Long[] ids) {
        try {
            //调用远程服务方法
            goodsService.updateStatus(status, ids);

            //判断状态是否是审核通过
            if ("1".equals(status)) {
                //查询需要发送商品信息
                List<TbItem> itemList = goodsService.findItemList(ids);

                //把集合转换json字符串
                String itemJson = JSON.toJSONString(itemList);

                //发送消息
                jmsTemplate.send(activeMQTopic, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(itemJson);
                    }
                });

            }

            //成功
            return new PygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "修改失败");
        }
    }

}
