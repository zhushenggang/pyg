//控制层
app.controller('userController', function ($scope, userService,loginService) {

    $scope.orderStatus = ['未付款','待发货','确认收货'];
    /**
     * 查询 用户 订单
     */
    $scope.findOrderSe = function () {
        userService.findOrderSe().success(function (data) {
            $scope.orderList = data;
        })
    };

    //完成用户注册
    $scope.save = function () {
        //判断密码是否输入匹配
        if ($scope.entity.password != $scope.password) {
            alert("两次密码不一致");
            return;
        }
        //判断手机不能为空
        if ($scope.entity.phone == null) {
            alert("手机号不能为空");
            return;
        }
        //完成注册
        userService.add($scope.entity,$scope.smsCode).success(function (data) {
            //判断
            if (data.success) {
               location.href="login.html"
            } else {
                alert(data.message);
            }
        })


    };
    
    //获取短信验证码
    $scope.getSmsCode = function () {
        //调用服务层方法
        //判断手机不能为空
        if ($scope.entity.phone == null) {
            alert("手机号不能为空");
            return;
        }
        //调用服务层方法
        userService.getSmsCode($scope.entity.phone).success(function (data) {
            //判断
            if (data.success) {
                alert(data.message);
            } else {
                alert(data.message);
            }
        })

    };

    //获取用户名
    $scope.loadUserInfo = function () {
        loginService.loadUserInfo().success(function (data) {
            $scope.username = data.loginName;
        })
    }

});	
