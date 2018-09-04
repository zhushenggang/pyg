//服务层
app.service('payService',function($http){

	//用户注册
	this.createQrCode = function () {
		return $http.get("../pay/createQrCode");
    }
    
    //查询支付状态
	this.queryPayStatus = function (out_trade_no,orderId) {
        return $http.get("../pay/queryPayStatus/"+out_trade_no+"/"+orderId);
    }

    //生成日志文件
    this.payLog=function(out_trade_no,total_fee,orderId){
	    return $http.get("../pay/payLog/"+out_trade_no+"/"+total_fee+"/"+orderId);
    }

});
