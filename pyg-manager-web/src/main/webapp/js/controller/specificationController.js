//在模块下定义控制器
app.controller("specificationController", function ($scope, $controller, specificationService) {

    //控制器继承
    //继承baseController控制器，且把父控制器$scope传递子控制器的$scope
    $controller("baseController", {$scope: $scope});

    //定义函数
    //所有的函数都绑定$scope域中
    $scope.findAll = function () {
        //使用$http内置服务调用后台restfull请求
        specificationService.findAll().success(function (data) {
            $scope.list = data;
        })
    };


    //定义分页查询方法
    $scope.findPage = function (page, rows) {
        //使用内置服务，发送分页查询请求
        specificationService.findPage(page, rows).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //定义添加品牌数据方法
    $scope.add = function () {

        //接受添加，或者修改返回对象
        var objService = null;

        if ($scope.entity.specification.id != null) {
            //修改
            objService = specificationService.update($scope.entity);
        } else {
            //否则就是添加
            objService = specificationService.add($scope.entity);
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
        specificationService.findOne(id).success(function (data) {
            $scope.entity = data;
        })
    };


    //定义删除方法
    $scope.del = function () {
        //发送删除请求
        specificationService.del($scope.selectIds).success(function (data) {
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

    //定义添加行方法
    //specificationOptionList = [{},{},{}];
    //js中凡是使用.调用对象，方法，必须考虑此对象，方法是否存在。
    $scope.addTableRow = function () {
        //添加行
        $scope.entity.specificationOptionList.push({});

    }

    //定义删除集合中行
    $scope.delTableRow = function (index) {
        //删除
        $scope.entity.specificationOptionList.splice(index, 1);
    }


})
