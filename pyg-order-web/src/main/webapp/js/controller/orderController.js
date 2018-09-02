//控制层
app.controller('orderController', function ($scope, addressService, orderService) {

    //定义参数接受结构
    $scope.entity = {address:{},orders:{}};

    //读取列表数据绑定到表单中  
    $scope.findAddressList = function () {
        addressService.findAddressList().success(
            function (response) {
                $scope.addressList = response;
                //设置默认选中地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == '1') {
                        $scope.address = $scope.addressList[i];
                        $scope.entity.address = $scope.address;
                    }
                }

            }
        );
    };

    //判断是否选中是当前地址
    $scope.isSelected = function (address) {

        if ($scope.address == address) {
            return true;
        }
        return false;

    };

    //改变选中地址
    $scope.changeAddress = function (address) {
        //循环整个地址列表
        for (var i = 0; i < $scope.addressList.length; i++) {
            $scope.addressList[i].isDefault = '0';
            if ($scope.addressList[i] == address) {
                $scope.addressList[i].isDefault = '1';
                $scope.address = $scope.addressList[i];
                $scope.entity.address = $scope.address;
            }
        }
    };
    
    //支付方式参数组装
    $scope.selectPayType = function (type) {
        //支付方式参数
        $scope.entity.orders.paymentType=type;
    }

    //定义方法查询购物车送货清单
    $scope.findOrderCartList = function () {
        orderService.findOrderCartList().success(function (data) {
            $scope.cartList = data;
            //计算总价格，商品数量
            $scope.totalValue = orderService.sum( $scope.cartList);

            //订单支付金额
            $scope.entity.orders.payment = $scope.totalValue.totalPrice;

        })
    };
    
    //提交订单
    $scope.submitOrder = function () {

        //封装参数
        //订单参数
        $scope.entity.orders.receiverAreaName = $scope.address.address;
        //手机
        $scope.entity.orders.receiverMobile = $scope.address.mobile;
        //收货人
        $scope.entity.orders.receiver = $scope.address.contact;

        //调用订单服务事项订单提交
        orderService.submitOrder($scope.entity).success(function (data) {
            //判断
            if(data.success){
                location.href = "http://localhost:8088/pay.html";
            }else{
                alert(data.message);
            }
        })
    }

});	