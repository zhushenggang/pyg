//服务层
app.service('seckillGoodsService',function($http){
	    	

	//查询秒杀商品
    this.findSecKillList=function(){
        return $http.get('../seckillGoods/findSecKillList');
    }

    //根据id查询商品
	this.findOne = function (id) {
        return $http.get('../seckillGoods/findOne/'+id);
    }

});
