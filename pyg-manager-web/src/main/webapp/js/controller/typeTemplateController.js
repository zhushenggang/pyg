//控制层
app.controller('typeTemplateController', function ($scope,
                                                   $controller,
                                                   typeTemplateService,
                                                   brandService,
                                                   specificationService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        typeTemplateService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        typeTemplateService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        typeTemplateService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //把品牌json字符串转换成json对象
                $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
                //把规格json字符串转换成json对象
                $scope.entity.specIds = JSON.parse($scope.entity.specIds);
                //扩展属性
                $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
    }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = typeTemplateService.update($scope.entity); //修改
        } else {
            serviceObject = typeTemplateService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        typeTemplateService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        typeTemplateService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //组装json文本数据
    //循环参数数组：[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
    //参数1：json数组
    //参数2：json的key固定式:text
    //json数据操作：
    //typeJson[i].key  这个方法是错误的方法，是获取不到值。必须使用:typeJson[i][key]
    //案例：
    //有如下格式的数据： list = [{id:1,name:"张三"},{id:2,name:"王五"}]
    //获取name属性值：
    //1,list[0].name √ 对象.属性 可以获取到值
    //2,key="name" list[0].key × 不能获取到值，此时使用如下方法：list[0][key] √
    //获取json对象中属性值，如果属性是通过变量存储的，必须使用[]方式获取
    $scope.jsonToStr = function (jsonStr, key) {
        //把json字符串转换json对象
        var typeJson = JSON.parse(jsonStr);

        //定义字符串对象，拼接json数组数据
        var value = "";

        //循环json数组对象
        for (var i = 0; i < typeJson.length; i++) {

            if (i > 0) {
                value += ",";
            }

            value += typeJson[i][key];
        }

        return value;

    };

    //定义方法，查询品牌下拉列表
    $scope.findBrandList = function () {
        //调用service服务方法，实现品牌列表查询
        brandService.findBrandList().success(function (data) {
            $scope.brandList = {data: data};
        })

    }

    //定义方法，查询品牌下拉列表
    $scope.findSpecList = function () {
        //调用service服务方法，实现品牌列表查询
        specificationService.findSpecList().success(function (data) {
            $scope.specList = {data: data};
        })

    }


    //模拟select2插件需要的数据
    //$scope.brandList ={data:[{id:'1',text:'联想'},{id:'2',text:'华为'}]};

    //定义动态添加扩展属性行的操作
    //entity.customAttributeItems = [{},{}]
    $scope.addTableRow = function () {
        //添加空对象
        $scope.entity.customAttributeItems.push({});
    };

    //删除扩展属性行
    $scope.delTableRow = function (index) {
        $scope.entity.customAttributeItems.splice(index,1);
    }

});	
