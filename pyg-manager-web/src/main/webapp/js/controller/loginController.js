//在模块下定义控制器
app.controller("loginController", function ($scope,loginService) {
    //获取用户登录名
    $scope.showLoginName = function () {
        //使用$http内置服务调用后台restfull请求
        loginService.showLoginName().success(function (data) {
            $scope.loginName = data.loginName;
        })
    };



})
