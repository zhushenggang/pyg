//控制层
app.controller('cartController', function ($scope,$controller,cartService) {
    //控制器继承
    //把父控制器$scope传递给子控制器$scope
    $controller("baseController",{$scope:$scope});

    //查询购物车 List<Cart>
    $scope.findCartList = function () {
        cartService.findCartList().success(function (data) {
            $scope.cartList = data;
            $scope.totalValue = cartService.sum( $scope.cartList);
        })
    };
    
    //定义添加购物车方法
    //添加
    //减少
    $scope.addGoodsToCartList = function (itemId,num) {
        //调用服务层方法
        cartService.addGoodsToCartList(itemId,num).success(function (data) {
            if(data.success){
                $scope.findCartList();
            }else{
                alert(data.message);
            }
        })
    }

    //
    $scope.closeFee = function () {
        if ($scope.selectIds != null && $scope.selectIds.length >0){
            location.href="http://localhost:8087/getOrderInfo.html#?ids="+$scope.selectIds;
            //window.location.reload(true);
        }else {
            alert("请选择需要结算的商品");
        }
    }

  /*  $scope.All = function ($event) {
        //判断是否checkbox选中事件
        if ($event.target.checked){
            $scope.sel2 = "choosed";
        }else {
            $scope.sel2 ="unchoosed";
        }
    }

    $scope.All2 =function ($event) {
        //判断是否checkbox选中事件
        if ($event.target.checked){

        }else {
            $scope.sel = "unchoosed";
        }
    }*/


});	
