//服务层
app.service('orderItemService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../orderItem/findAll');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../orderItem/findPage/'+page+'/'+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../orderItem/findOne/'+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../orderItem/add',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../orderItem/update',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../orderItem/delete/'+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../orderItem/search/'+page+"/"+rows, searchEntity);
	}    	
});
