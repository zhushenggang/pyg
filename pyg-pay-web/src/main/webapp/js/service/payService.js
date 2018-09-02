//服务层
app.service('payService',function($http){

	//用户注册
	this.createQrCode = function () {
		return $http.get("../pay/createQrCode");
    }
    
    //查询支付状态
	this.queryPayStatus = function (out_trade_no) {
        return $http.get("../pay/queryPayStatus/"+out_trade_no);
    }


});
