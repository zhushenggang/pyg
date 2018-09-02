package com.pyg.mapper;

import com.pyg.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/4.
 */
public interface BrandMapper {

    /*
    * 需求：查询所有品牌数据
    * */
    public List<TbBrand> findAll();
    /**
     * 需求：添加品牌数据
     */
    public void insert(TbBrand brand);

    /**
     * 需求：修改品牌数据
     */
    public void update(TbBrand brand);

    /**
     * 需求：根据id查询品牌数据
     */
    public TbBrand findOne(Long id);

    /*
     * 需求：根据id删除
     */
    public void delete(Long id);

    /**
     * 需求:查询品牌下拉列表
     * 查询数据格式：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     * 返回值：List<Map>
     *
     */
    public List<Map> findBrandList();

}
