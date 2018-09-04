//服务层
app.service('orderService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findOrderCartList=function(ids){
		return $http.get('../order/findOrderCartList/'+ids);
	}

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

    //提交订单
    this.submitOrder = function (entity) {
       return $http.post("../order/submitOrder",entity);
    }
});
