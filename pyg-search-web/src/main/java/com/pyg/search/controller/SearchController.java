package com.pyg.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.search.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by on 2018/8/17.
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Reference(timeout = 1000000)
    private SearchService searchService;

    /**
     * 接受查询参数，实现搜索
     */
    @RequestMapping("searchList")
    public Map searchList(@RequestBody Map searchMap){
        Map map = searchService.searchList(searchMap);
        return  map;
    }


}
