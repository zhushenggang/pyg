//控制层
app.controller('cartController', function ($scope, cartService) {

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

});	
