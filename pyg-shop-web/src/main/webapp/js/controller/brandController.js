//在模块下定义控制器
app.controller("brandController", function ($scope,$controller, brandService) {

    //控制器继承
    //继承baseController控制器，且把父控制器$scope传递子控制器的$scope
    $controller("baseController",{$scope:$scope});

    //定义函数
    //所有的函数都绑定$scope域中
    $scope.findAll = function () {
        //使用$http内置服务调用后台restfull请求
        brandService.findAll().success(function (data) {
            $scope.list = data;
        })
    };


    //定义分页查询方法
    $scope.findPage = function (page, rows) {
        //使用内置服务，发送分页查询请求
        brandService.findPage(page, rows).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //定义添加品牌数据方法
    $scope.add = function () {

        //接受添加，或者修改返回对象
        var objService = null;

        //否则就是添加
        objService = brandService.add($scope.entity);

        if ($scope.entity.id != null) {
            //修改
            objService = brandService.update($scope.entity);
        }


        //使用$http内置服务发送请求
        objService.success(function (data) {
            //判断
            if (data.success) {
                //重新刷新列表页面
                $scope.reloadList();
            } else {
                alert(data.message);
            }
        })

    };

    //定义根据id查询品牌数据方法
    $scope.findOne = function (id) {
        //内置服务发送请求
        brandService.findOne(id).success(function (data) {
            $scope.entity = data;
        })
    };


    //定义删除方法
    $scope.del = function () {
        //发送删除请求
        brandService.del($scope.selectIds).success(function (data) {
            //判断
            if (data.success) {
                //清空删除ids
                $scope.selectIds = [];
                //重新刷新列表页面
                $scope.reloadList();
            } else {
                alert(data.message);
            }
        })
    };


})
