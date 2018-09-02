//抽取一个父控制器
app.controller("baseController", function ($scope) {

    //定义reloadList
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            //重新加载,此方法将会被自动加载
            //1,页面刷新
            //2,分页控件中数据发生变化，reloadList也会自动调用
            $scope.reloadList();
        }
    };

    //定义数组，封装id参数
    $scope.selectIds = [];

    //组装选中id参数
    $scope.updateSelection = function ($event, id) {
        //判断是否checkbox选中事件
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            //取消事件
            $scope.selectIds.splice($scope.selectIds.indexOf(id), 1);
        }
    };


})
