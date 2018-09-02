package com.pyg.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.content.service.ContentService;
import com.pyg.mapper.TbContentMapper;
import com.pyg.pojo.TbContent;
import com.pyg.pojo.TbContentExample;
import com.pyg.pojo.TbContentExample.Criteria;
import com.pyg.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    //注入redis模板对象
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 查询全部
     */
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    public void add(TbContent content) {


        contentMapper.insert(content);
        //先删除缓存
        redisTemplate.boundHashOps("INDEX_CHAE").delete(content.getCategoryId());
    }


    /**
     * 修改
     */
    public void update(TbContent content) {
        contentMapper.updateByPrimaryKey(content);
        //先查询出广告内存对象
        TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
        //更新同步缓存
        redisTemplate.boundHashOps("INDEX_CHAE").delete(tbContent.getCategoryId());
        redisTemplate.boundHashOps("INDEX_CHAE").delete(content.getCategoryId());
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    public void delete(Long[] ids) {
        for (Long id : ids) {
            contentMapper.deleteByPrimaryKey(id);
            //先查询出广告内存对象
            TbContent tbContent = contentMapper.selectByPrimaryKey(id);
            //删除缓存
            redisTemplate.boundHashOps("INDEX_CHAE").delete(tbContent.getCategoryId());
        }
    }


    public PageResult findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();

        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getContent() != null && content.getContent().length() > 0) {
                criteria.andContentLike("%" + content.getContent() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }

        }

        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 需求：根据内容分类id查询广告内容
     * 此方法是前台门户系统调用，负责查询广告数据的方法，因此此方法并发压力非常大，为了减轻数据库压力，
     * 提高查询效率，提高并发能力，需要对此方法加入缓存。
     * 缓存业务：
     * 1，每次查询广告数据时，先查询redis服务器缓存
     * 2，如果缓存中没有数据，再查询数据数据，同时把数据放入缓存，下次查询缓存就有数据
     * 3，如果缓存中有数据，直接返回即可，不在查询数据库
     * 缓存服务器：使用redis服务器
     * 数据结构：hash
     * key: INDEX_CHAE  FOOD_CHACHE
     * field: categoryId 表示页面那个区域缓存
     * value: 缓存数据 （json字符串）
     */
    public List<TbContent> findContentByCategoryId(Long categoryId) {


        //先查询缓存
        String adStr = (String) redisTemplate.boundHashOps("INDEX_CHAE").get(categoryId);
        //判断缓存是否存在
        if(adStr!=null && !"".equals(adStr)){
            //把json字符串转换成json对象
            List<TbContent> list = JSON.parseArray(adStr, TbContent.class);
            return  list;
        }
        //创建example对象
        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();
        //设置查询参数，根据外键查询广告信息
        criteria.andCategoryIdEqualTo(categoryId);

        //执行查询
        List<TbContent> list = contentMapper.selectByExample(example);

        //放入缓存
        redisTemplate.boundHashOps("INDEX_CHAE").put(categoryId,JSON.toJSONString(list));

        return list;
    }

}
