 //控制层 
app.controller('sellerController' ,function($scope,sellerService){
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		sellerService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    

	//查询实体 
	$scope.findOne=function(){
		sellerService.findOne().success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
        sellerService.add( $scope.entity).success(
			function(response){
				if(response.success){
					//跳转到登录页面
					location.href="shoplogin.html"
				}else{
					alert(response.message);
				}
			}		
		);				
	};


	//修改资料
	$scope.update=function () {
		sellerService.update($scope.entity).success(function (data) {
			if (data.success){
				alert(data.message);
                location.href="home.html";
			}else {
				alert(data.message);
			}
        })
    };

	//修改密码
    $scope.updatePassword = function (oldPwd,newPwd,reNewPwd) {
        if (newPwd==null || newPwd==""){
            alert("新密码不能为空");
            return;
        }
        if(reNewPwd != newPwd){
            alert("两次密码不一样");
            return;
        }
        sellerService.updatePassword(oldPwd,newPwd,reNewPwd).success(function (data) {
			if (data.success){
				alert(data.message);
                location.href="/logout";
			} else {
                alert(data.message);
                location.href="password.html";
            }
        })
    }

});	
