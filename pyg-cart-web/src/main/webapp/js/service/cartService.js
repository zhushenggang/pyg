//服务层
app.service('cartService', function ($http) {

    //查询购物车数据
    this.findCartList = function () {
        return $http.get("../cart/findCartList");
    }

    //加1，减一购物车数量
    this.addGoodsToCartList = function (itemId, num) {
        return $http.get("../cart/addGoodsToCartList/" + itemId + "/" + num);
    };

    //计算购物车列表中总价格，总数量
    this.sum = function (cartList) {

        //定义对象，封装数量和价格
        var totalValue = {totalNum: 0, totalPrice: 0};

        for (var i = 0; i < cartList.length; i++) {
            //获取购物车清单数据
            var orderItemList = cartList[i].orderItemList;
            //循环购物车列表集合
            for (var j = 0; j < orderItemList.length; j++) {
                totalValue.totalNum += orderItemList[j].num;
                totalValue.totalPrice += orderItemList[j].totalFee;
            }
        }

        return totalValue;


    }


});
