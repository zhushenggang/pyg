package com.pyg.manager.service;

import com.pyg.utils.PageResult;
import com.pyg.vo.Specification;

import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/7.
 */
public interface SpecificationService {

    /**
     * 需求：查询规格分页列表数据
     */
    public PageResult findPage(Integer page,Integer rows);

    /**
     * 需求：添加规格表数据，同时添加规格选项表数据
     * 表关系：一个规格数据对应多个规格选项
     * 思考：同时保存2个表数据，参数应该如何传递？
     * 定义一个包装类，此类包装2个表所对应pojo
     */
    public void add(Specification specification);

    /**
     * 需求：根据id查询规格及规格选项数据
     */
    public Specification findOne(Long id);

    /**
     * 需求：修改规格及规格选项数据
     */
    public void update(Specification specification);

    /**
     * 需求：删除规格及规格选项数据
     */
    public  void delete(Long[] ids);

    /**
     * 需求：查询规格值，进行下拉列表展示
     */
    public List<Map> findSpecList();

}
