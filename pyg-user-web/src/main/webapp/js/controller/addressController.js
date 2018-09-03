 //控制层 
app.controller('addressController' ,function($scope,addressService){

	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		addressService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	};
	

	
	//查询实体 
	$scope.findOne=function(id){				
		addressService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=addressService.update( $scope.entity); //修改
		}else{
			serviceObject=addressService.add( $scope.entity);//增加

			alert(entity.pro)
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findAll();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(id){
		//获取选中的复选框			
		addressService.dele(id).success(
			function(response){
				if(response.success){
					$scope.findAll();//刷新列表
				}						
			}		
		);				
	};


	//设为默认
	
	$scope.def = function (id, isDefault) {

		addressService.def(id,isDefault).success(function (data) {
			if (data.success){
                $scope.findAll();
			} else {
				alert(data.message)
			}
        })
    };

	$scope.ifDef = function (id,isDefault,oldDef) {
		if (oldDef=='1'){
			alert("此地址已是默认,无需设置")
			return
		}

        $scope.def(id,isDefault);
    }
    
});	
