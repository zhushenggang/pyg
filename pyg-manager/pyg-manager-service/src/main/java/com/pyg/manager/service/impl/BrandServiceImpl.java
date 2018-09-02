package com.pyg.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.manager.service.BrandService;
import com.pyg.mapper.BrandMapper;
import com.pyg.pojo.TbBrand;
import com.pyg.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/4.
 */
@Service
public class BrandServiceImpl implements BrandService{

    //注入mapper接口代理对象
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.findAll();
    }

    /**
     * 需求：查询所有品牌数据，进行分页展示
     */
    public PageResult findPage(Integer page, Integer rows) {

        //设置分页查询参数
        PageHelper.startPage(page,rows);

        //分页查询
        Page<TbBrand> pageInfo = (Page<TbBrand>) brandMapper.findAll();

        return new PageResult(pageInfo.getTotal(),pageInfo.getResult());
    }

    /**
     * 需求：插入品牌数据
     * 返回值：void
     */
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }


    /**
     * 需求：修改品牌数据
     * 返回值：void
     */
    public void update(TbBrand brand) {
        brandMapper.update(brand);
    }

    @Override
    public TbBrand findOne(Long id) {
        TbBrand brand = brandMapper.findOne(id);
        return brand;
    }

    /**
     * 需求：删除品牌数据
     * 返回值：void
     */
    public void delete(Long[] ids) {
        for (Long id : ids) {

            brandMapper.delete(id);
        }
    }

    /**
     * 需求:查询品牌下拉列表
     * 查询数据格式：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     * 返回值：List<Map>
     *
     */
    public List<Map> findBrandList() {
        return brandMapper.findBrandList();
    }


}
