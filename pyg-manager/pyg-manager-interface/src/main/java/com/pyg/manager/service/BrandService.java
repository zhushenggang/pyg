package com.pyg.manager.service;

import com.pyg.pojo.TbBrand;
import com.pyg.utils.PageResult;

import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/4.
 */
public interface BrandService {

    /**
     * 需求：查询所有品牌数据
     */
    public List<TbBrand> findAll();

    /**
     * 需求：查询所有品牌数据，进行分页展示
     */
    public PageResult findPage(Integer page,Integer rows);
    /**
     * 需求：插入品牌数据
     * 返回值：void
     */
    public  void  add(TbBrand brand);

    /**
     * 需求：修改品牌数据
     * 返回值：void
     */
    public  void  update(TbBrand brand);

    /**
     * 需求：根据id查询品牌数据
     */
    public TbBrand findOne(Long id);

    /**
     * 需求：删除品牌数据
     * 返回值：void
     */
    public  void  delete(Long[] ids);

    /**
     * 需求:查询品牌下拉列表
     * 查询数据格式：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     * 返回值：List<Map>
     *
     */
    public List<Map> findBrandList();

}
