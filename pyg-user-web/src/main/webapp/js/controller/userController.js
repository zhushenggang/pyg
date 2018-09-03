//控制层
app.controller('userController', function ($scope,loginService
                                           ,addressService,
                                           uploadService,
                                           userService) {

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



    /*$scope.$watch('entity.address.provinceId',function (newValue,oldValue) {
        addressService.findcityList(newValue).success(function (data) {
            $scope.cityList = data;
        })
    });

    $scope.$watch('entity.address.cityId',function (newValue,oldValue) {
        addressService.findtownList(newValue).success(function (data) {
            $scope.townList = data;
        })
    });

    $scope.findProvinceList = function () {
        addressService.findProvinceList().success(function (data) {
            $scope.provinceList = data;
        })
    }*/

    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (data) {
            if(data.success){
                $scope.entity.headPic = data.message;
            }else{
                alert(data.message);
            }
        })
    }

    $scope.xiugai = function () {
        var province = document.getElementById("province1").value;
        var city = document.getElementById("city1").value;
        var area = document.getElementById("district1").value;
        $scope.entity.dizhi = province + city + area;
        var birthday = "";
        var  year = document.getElementById("select_year2").value;
        birthday += year + "-";
        var  month = document.getElementById("select_month2").value ;
        birthday += month + "-";
        var  day = document.getElementById("select_day2").value;
        birthday += day;
        birthday = new Date(birthday);
        $scope.entity.birthday = birthday;
        userService.saveUserInfo($scope.entity).success(function (data) {
            if(data.success){
                alert(data.message);
            }else{
                alert(data.message);
            }
        })
    }

    $scope.changepwd = function () {
        if($scope.newpwd == null || $scope.newpwd == ""){
            alert("新密码不能为空");
            return;
        }
        if($scope.relpwd != $scope.newpwd){
            alert("两次输入密码不一致");
            return;
        }
        userService.changepwd($scope.oldpwd,$scope.newpwd).success(function (data) {
            if(data.success){
                location.href="/logout/cas";
            }else{
                alert(data.message);
            }
        })
    }

});
