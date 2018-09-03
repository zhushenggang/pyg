//服务层
app.service('userService',function($http){


    this.findOrderSe = function () {
        return $http.get("../user/findOrderSe");
    }
	//用户注册
	this.add = function (entity,smsCode) {
		return $http.post("../user/add/"+smsCode,entity);
    }
    //获取验证码
	this.getSmsCode = function (phone) {
        return $http.get("../user/getSmsCode/"+phone);
    }

    this.saveUserInfo = function (UserInfo) {
        return $http.post("../user/saveUserInfo",UserInfo);
    }

    this.changepwd = function (oldpwd, newpwd) {
        return $http.get("../user/changepwd/"+oldpwd+"/"+newpwd);
    };
});
