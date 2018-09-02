package com.pyg.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.manager.service.SpecificationService;
import com.pyg.mapper.TbSpecificationMapper;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.pojo.TbSpecification;
import com.pyg.pojo.TbSpecificationOption;
import com.pyg.pojo.TbSpecificationOptionExample;
import com.pyg.utils.PageResult;
import com.pyg.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/7.
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    //注入规格mapper接口代理对象
    @Autowired
    private TbSpecificationMapper specificationMapper;

    //注入规格选项mapper接口代理对象
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 需求：查询规格分页列表数据
     */
    public PageResult findPage(Integer page, Integer rows) {
        //设置分页参数
        PageHelper.startPage(page, rows);
        //分页查询
        Page<TbSpecification> pageInfo = (Page<TbSpecification>) specificationMapper.selectByExample(null);

        return new PageResult(pageInfo.getTotal(), pageInfo.getResult());
    }

    /**
     * 需求：添加规格表数据，同时添加规格选项表数据
     * 表关系：一个规格数据对应多个规格选项
     * 思考：同时保存2个表数据，参数应该如何传递？
     * 定义一个包装类，此类包装2个表所对应pojo
     */
    public void add(Specification specification) {

        //获取规格对象
        TbSpecification tbSpecification = specification.getSpecification();
        //保存规格数据结束后，必须返回规格主键id,用于维护规格选项关系
        specificationMapper.insertSelective(tbSpecification);

        //获取规格选项集合对象
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        //循环遍历，保存每一个规格选项
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
            //设置外键,维护规格及规格选项关系
            tbSpecificationOption.setSpecId(tbSpecification.getId());

            //实现保存
            specificationOptionMapper.insertSelective(tbSpecificationOption);

        }


    }

    /**
     * 需求：根据id查询规格及规格选项数据
     */
    public Specification findOne(Long id) {

        //根据id查询规格对象
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //根据外键查询规格选项
        //创建example对象
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        //创建criteria对象
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        //设置外键查询参数
        criteria.andSpecIdEqualTo(id);
        //执行查询
        List<TbSpecificationOption> list = specificationOptionMapper.selectByExample(example);

        //创建包装类对象封装查询结果
        Specification specification = new Specification();
        specification.setSpecification(tbSpecification);
        specification.setSpecificationOptionList(list);

        return specification;
    }

    /**
     * 需求：修改规格及规格选项数据
     */
    public void update(Specification specification) {

        TbSpecification tbSpecification = specification.getSpecification();


        //规格数据修改
        specificationMapper.updateByPrimaryKeySelective(tbSpecification);

        //创建example对象
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        //创建criteria对象
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        //设置删除参数
        criteria.andSpecIdEqualTo(tbSpecification.getId());

        //删除规格选项数据
        specificationOptionMapper.deleteByExample(example);

        //修改规格选项
        //先获取规格选项
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        //循环遍历保存
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {

            //设置外键
            tbSpecificationOption.setSpecId(tbSpecification.getId());
            //保存
            specificationOptionMapper.insertSelective(tbSpecificationOption);

        }

    }

    @Override
    public void delete(Long[] ids) {

        //遍历ids集合，删除
        for (Long id : ids) {

            specificationMapper.deleteByPrimaryKey(id);
            //删除规格选项
            //创建example对象
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            //创建criteria对象
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            //设置删除参数
            criteria.andSpecIdEqualTo(id);
            //删除
            specificationOptionMapper.deleteByExample(example);

        }
    }

    /**
     * 需求：查询规格值，进行下拉列表展示
     */
    public List<Map> findSpecList() {
        return specificationMapper.findSpecList();
    }
}
