package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.manager.service.BrandService;
import com.pyg.pojo.TbBrand;

import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/4.
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    //注入远程服务service对象
    @Reference(timeout = 1000000)
    private BrandService brandService;

    /**
     * 需求：查询所有品牌
     */
    @RequestMapping("findAll")
    public List<TbBrand> findAll() {

        //调用远程service服务方法
        List<TbBrand> brandList = brandService.findAll();

        return brandList;

    }

    /**
     * 需求：分页查询品牌数据
     */
    @RequestMapping("findPage/{page}/{rows}")
    public PageResult findPage(@PathVariable Integer page, @PathVariable Integer rows) {
        //调用远程服务service服务方法
        PageResult result = brandService.findPage(page, rows);
        return result;
    }

    /*
     * 需求：实现品牌数据添加
     */
    @RequestMapping("add")
    public PygResult add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);

            //添加成功
            return new PygResult(true, "添加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "添加失败");
        }
    }

    /**
     * 需求：根据id查询品牌数据
     *
     * @param
     * @return
     */
    @RequestMapping("findOne/{id}")
    public TbBrand findOne(@PathVariable Long id) {
        TbBrand brand = brandService.findOne(id);
        return brand;
    }

    /*
     * 需求：实现品牌数据修改
     */
    @RequestMapping("update")
    public PygResult update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            //添加成功
            return new PygResult(true, "添加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "添加失败");
        }
    }

    /*
     * 需求：实现品牌数据删除
     */
    @RequestMapping("del/{ids}")
    public PygResult delete(@PathVariable Long[] ids) {
        try {
            brandService.delete(ids);
            //删除成功
            return new PygResult(true, "删除成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
        }
    }


    /**
     * 需求:查询品牌下拉列表
     * 查询数据格式：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     * 返回值：List<Map>
     *
     */
    @RequestMapping("findBrandList")
    public List<Map> findBrandList() {
        //调用远程service服务方法
        List<Map> brandList = brandService.findBrandList();
        return  brandList;
    }


}
