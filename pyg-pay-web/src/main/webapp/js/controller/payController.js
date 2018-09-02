//控制层
app.controller('payController', function ($scope,$location,payService) {

    //生成支付二维码
    $scope.createQrCode = function () {
        //完成注册
        payService.createQrCode().success(function (data) {
            //获取二维码支付链接
            var code_url = data.code_url;
            //获取支付金额
            $scope.total_fee = (data.total_fee/100).toFixed(2);
            $scope.out_trade_no = data.out_trade_no;

            //使用插件qrious生成二维码
            var qr = new QRious({
                element:document.getElementById('qrCode'),
                size:300,
                background:'white',
                foreground:'black',
                level:'H',
                value:code_url
            });

            //查询二维码状态
            //及时检测二维码是否支付成功，或者是否失败
           $scope.queryPayStatus();


        })


    };

    $scope.queryPayStatus = function () {
        payService.queryPayStatus($scope.out_trade_no).success(function (data) {
            //判断支付状态
            if(data.success){
                location.href="paysuccess.html#?money="+$scope.total_fee;
            }else if(data.message=='二维码超时'){
                //重新生成二维码支付
                $scope.createQrCode();
            }
            else {
                location.href="payfail.html";
            }
        })
    }

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
    };

    //支付成功页面显示金额
    $scope.loadPayment = function () {
        $scope.payMomey = $location.search()["money"];
    }

});	
