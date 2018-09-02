package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.manager.service.SpecificationService;
import com.pyg.utils.PageResult;
import com.pyg.utils.PygResult;
import com.pyg.vo.Specification;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/8/7.
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

    //注入远程service服务对象
    @Reference(timeout = 10000000)
    private SpecificationService specificationService;

    /**
     * 需求：查询规格分页列表数据
     */
    @RequestMapping("findPage/{page}/{rows}")
    public PageResult findPage(@PathVariable Integer page, @PathVariable Integer rows) {
        //调用远程服务方法
        PageResult result = specificationService.findPage(page, rows);

        return result;

    }

    /**
     * 需求：实现规格，规格选项保存
     * 参数：Specification 包装类对象
     * 返回：PygResult
     * 注意：angularJS前台传递的参数都是json格式。
     * 逆向分析：specification对象json格式数据
     * specification =
     * {specification:{},specificationOptionList:[{},{}]}
     * 前端代码：
     * angularJS就是需要封装specification的json数据，把数据组装好了以后
     * 直接发送给后端代码。
     */
    @RequestMapping("add")
    public PygResult add(@RequestBody Specification specification) {
        try {
            //调用远程对象服务方法
            specificationService.add(specification);
            //成功
            return new PygResult(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            //保存失败
            return new PygResult(false, "保存失败");
        }
    }

    /**
     * 需求：根据id查询规格及规格选项
     */
    @RequestMapping("findOne/{id}")
    public Specification findOne(@PathVariable Long id) {
        Specification specification = specificationService.findOne(id);

        return specification;
    }

    /**
     * 需求：修改规格及规格选项数据
     */
    @RequestMapping("update")
    public PygResult update(@RequestBody Specification specification) {
        try {
            //调用远程对象服务方法
            specificationService.update(specification);
            //成功
            return new PygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            //保存失败
            return new PygResult(false, "修改失败");
        }
    }



    /**
     * 需求：删除规格及规格选项
     */
    @RequestMapping("del/{ids}")
    public PygResult delete(@PathVariable Long[] ids) {
        try {
            //调用远程对象服务方法
            specificationService.delete(ids);
            //成功
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            //保存失败
            return new PygResult(false, "删除失败");
        }
    }

    /**
     * 需求：查询规格值，进行下拉列表展示
     */
    @RequestMapping("findSpecList")
    public List<Map> findSpecList() {
        return specificationService.findSpecList();
    }

}
