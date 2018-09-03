//服务层
app.service('addressService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAddressList=function(){
		return $http.get('../address/findAddressList');
	}
	
	this.add = function (entity) {
		return $http.post("../address/add",entity);
    }

    this.findProvinceList = function () {
        return $http.get("../address/findProvinceList");
    }

    this.findcityList = function (provinceid) {
        return $http.get("../address/findcityList/provinceid");
    }

    this.findtownList = function (cityid) {
        return $http.get("../address/findtownList/cityid");
    }

});
