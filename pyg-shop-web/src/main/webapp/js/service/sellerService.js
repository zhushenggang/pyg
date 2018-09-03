//服务层
app.service('sellerService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seller/findAll');		
	}
	//查询实体
	this.findOne=function(){
		return $http.get('../seller/findOne');
	}
	//增加 
	this.add=function(entity){
		return $http.post('../seller/add',entity );
	}
	//修改 
	this.update=function(entity){
		return $http.post('../seller/update',entity );
	}
    this.updatePassword=function (oldPwd,newPwd,reNewPwd) {
		return $http.post('../seller/updatePassword/'+oldPwd+'/'+newPwd+'/'+reNewPwd);
    }
});
