package com.pyg.gen;

import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by on 2018/8/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-dao.xml")
public class SqlGenTest {

    //注入mapper接口代理对象
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 需求：mybatis逆向工程代码使用方法
     */
    @Test
    public  void findAll(){
        List<TbItem> list = itemMapper.selectByExample(null);
        System.out.println(list);
    }

    /**
     * 需求：条件查询  and 关系
     */
    @Test
    public void findBrandCondition(){

        //创建item的example对象
        TbItemExample example = new TbItemExample();
        //创建example ccriteria对象
        TbItemExample.Criteria criteria = example.createCriteria();

        //criteria对每一个字段都进行所有条件封装
        //多个条件之间是and的关系
        criteria.andIdEqualTo(691300L);
        //设置条件参数
        criteria.andTitleLike("%三星%");

        //执行查询
        List<TbItem> list = itemMapper.selectByExample(example);

        System.out.println(list);


    }

    /**
     * 需求：条件查询 OR 关系
     */
    @Test
    public void findBrandConditionOR(){

        //创建item的example对象
        TbItemExample example = new TbItemExample();

        example.or().andIdEqualTo(691300L);
        example.or().andIdEqualTo(738388L);
        //执行查询
        List<TbItem> list = itemMapper.selectByExample(example);

        System.out.println(list);


    }

}
