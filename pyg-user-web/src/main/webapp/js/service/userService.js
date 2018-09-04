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

    //更换手机号码
    this.changepwd = function (oldpwd, newpwd) {
        return $http.get("../user/changepwd/"+oldpwd+"/"+newpwd);
    };

    //发送旧手机的验证码
    this.changeMobile = function (mobileNum) {
        return $http.get("../user/changeMobile/"+mobileNum);
    };

    //检查旧手机的验证码
    this.checkCode = function (phoneCode,mobileNum) {
        return $http.get("../user/checkCode/"+phoneCode+"/"+mobileNum);
    };

    //新手机发送验证码
    this.sendCodeToNewPhone = function (newPhoneNum) {
        return $http.get("../user/sendCodeToNewPhone/"+newPhoneNum);
    }
});
