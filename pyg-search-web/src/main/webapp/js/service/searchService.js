//服务层
app.service('searchService',function($http){

	this.searchList = function (searchMap) {
		return $http.post("../search/searchList",searchMap);
    }

});
