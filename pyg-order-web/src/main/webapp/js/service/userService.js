//服务层newPhoneNum
app.service('userService',function($http){

	//用户注册
	this.add = function (entity,smsCode) {
		return $http.post("../user/add/"+smsCode,entity);
    }
    //获取验证码
	this.getSmsCode = function (phone) {
        return $http.get("../user/getSmsCode/"+phone);
    }




});
