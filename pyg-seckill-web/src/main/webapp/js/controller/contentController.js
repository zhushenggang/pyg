 //控制层 
app.controller('contentController' ,function($scope,contentService){

	//定义数组存储广告信息
	$scope.adList = [];
    //读取列表数据绑定到表单中  
	$scope.findContentListByCategoryId=function(categoryId){
		contentService.findContentListByCategoryId(categoryId).success(
			function(response){
                $scope.adList[categoryId]=response;
			}
		);
	};
	
	//定义搜索方法
	$scope.solrSearch = function () {
		//发送请求给搜索系统
		//angualrjs参数路由：静态页面如何传递参数？
		//angualrjs参数路由:#？在静态页面后面追加参数
		window.location.href="http://localhost:8083/search.html#?keywords="+$scope.keywords;
    }
	

});	
