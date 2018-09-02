//控制层
app.controller('searchController', function ($scope, $location, searchService) {

    //定义方法，处理本地搜索请求
    $scope.locationSearchList = function () {

        if ($scope.searchMap.keywords == '') {
            window.location.href = "http://localhost:8083/search.html";
        } else {
            location.href = "http://localhost:8083/search.html#?keywords=" + $scope.searchMap.keywords;
            //让页面刷新
            window.location.reload(true);
        }


    }


    //接受静态页面参数方法，搜索结果
    ////门户系统传递参数：search.html#?keywords=华为
    $scope.loadSearchList = function () {
        //接受静态页面，或者ng-model绑定参数，都可以使用$location服务接受参数
        //接受参数语法：
        //$location.name
        //$location.search()['keywords']
        var keywords = $location.search()['keywords'];
        $scope.searchMap.keywords = keywords;
        //调用搜索方法
        $scope.searchList();
    }


    //前台传递搜索参数条件很多，因此使用对象对参数做一个封装
    //分类
    //品牌
    //内存，尺寸，价格，排序，分页.....
    $scope.searchMap = {
        keywords: "",
        category: "",
        brand: "",
        spec: {},
        price: "",
        sortField: "price",
        sort: "ASC",
        page: 1,
        pageSize: 40
    };

    //定义搜索方法
    $scope.searchList = function () {
        //调用服务层方法，传递关键词进行搜索
        searchService.searchList($scope.searchMap).success(function (data) {
            $scope.resultMap = data;
            //调用方法，计算动态页码
            buildPageLable();
        })

    };

    //定义条件查询过滤参数传递
    $scope.addFilterCondition = function (key, value) {
        //判断是否是分类，品牌，价格 条件
        if (key == 'category' || key == 'brand' || key == 'price') {
            //把参数放入searchMap
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        //调用查询索引库的方法
        $scope.searchList();
    };

    //清除搜索条件，从新形成新的搜索条件
    $scope.removeSearchItem = function (key) {
        //判断删除的条件是分类，品牌，价格
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }

        //调用搜索方法
        $scope.searchList();

    };

    //定义排序方法
    $scope.addSort = function (sortField, sort) {
        //把搜索排序参数绑定到searchMap
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;

        //调用搜索方法
        $scope.searchList();
    };

    //定义方法，实现分页查询
    $scope.queryForPage = function (page) {

        //判断
        if (page <= 0 || page > $scope.resultMap.totalPages) {
            return;
        }

        //把参数绑定到searchMap
        $scope.searchMap.page = parseInt(page);
        //调用搜索方法
        $scope.searchList();
    };

    //组装动态分页页码
    buildPageLable = function () {

        //定义省略号判断标识符
        $scope.isPreDot = false;
        $scope.isPosDot = false;

        $scope.pageLable = [];

        //获取搜索商品数据总页码数
        var maxPage = $scope.resultMap.totalPages;

        //获取当前页
        var page = $scope.searchMap.page;

        //定义动态页起始页
        var firstPage = 1;
        //定义动态页结束页
        var lastPage = maxPage;

        //判断总页码数是否大于5页
        if ($scope.resultMap.totalPages > 5) {
            //如果当前页小于5，展示前5页
            if (page <= 3) {
                lastPage = 5;
                $scope.isPosDot = true;
            }
            else if (page > maxPage - 2) {
                //如果当前页码比最大页码数减去2还要大，显示最后5页
                firstPage = maxPage - 5;
                $scope.isPreDot = true;

            } else {
                //否则显示中间5页
                firstPage = page - 2;
                lastPage = page + 2;

                $scope.isPreDot = true;
                $scope.isPosDot = true;
            }


        }

        //把页面标签放入集合
        for (var i = firstPage; i < lastPage; i++) {
            $scope.pageLable.push(i);
        }


    };
    
    //定义方法判断如果是第一页,上一页显示为灰色，或者是最后一页，下一页显示为灰色
    $scope.isTopPage = function () {

        if($scope.searchMap.page == $scope.resultMap.totalPages){
            return true;
        }
        return false;
    }
    //第一页
    $scope.isFirstPage = function () {

        if($scope.searchMap.page == 1){
            return true;
        }
        return false;
    }

});	
