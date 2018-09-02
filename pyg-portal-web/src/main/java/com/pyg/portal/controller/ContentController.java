package com.pyg.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.content.service.ContentService;
import com.pyg.pojo.TbContent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by on 2018/8/16.
 */
@RestController
@RequestMapping("/ad")
public class ContentController {


    //注入远程服务对象
    @Reference(timeout = 1000000)
    private ContentService contentService;


    /**
     * 需求：根据内容分类id查询广告内容
     * 参数：分类id
     * 返回值：广告内容集合
     */
    @RequestMapping("findContentListByCategoryId/{categoryId}")
    public List<TbContent> findContentListByCategoryId(@PathVariable Long categoryId){
        //调用远程服务方法
        List<TbContent> catList = contentService.findContentByCategoryId(categoryId);
        return catList;
    }

}
