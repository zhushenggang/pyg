//服务层
app.service('contentService',function($http){
	    	
	//根据内容分类id查询广告内容
	this.findContentListByCategoryId=function(categoryId){
		return $http.get('../ad/findContentListByCategoryId/'+categoryId);
	}

});
