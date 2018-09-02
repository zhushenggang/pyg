//服务层
app.service('loginService',function($http){

	//用户注册
	this.loadUserInfo = function () {
		return $http.get("../login/loadUserInfo");
    }


});
