//控制层
app.controller('seckillGoodsController', function ($scope,
                                                   $location,
                                                   $interval,
                                                   seckillGoodsService,
                                                   seckillOrderService) {


    //查询秒杀商品数据
    $scope.findSecKillList = function () {
        //调用服务查询秒杀商品列表
        seckillGoodsService.findSecKillList().success(function (data) {
            $scope.seckillList = data;
        })
    };

    //跳转到秒杀详情页面
    $scope.seckillItem = function (id) {
        location.href = "seckill-item.html#?id=" + id;
    }

    //根据id查询秒杀商品
    $scope.findOne = function () {
        //获取参数
        var id = $location.search()["id"];

        if (id > 0) {
            //调用服务查询秒杀商品
            seckillGoodsService.findOne(id).success(function (data) {
                $scope.seckill = data;


                //计算出剩余时间
                //获取毫秒时间
                var endTime = new Date($scope.seckill.endTime).getTime();
                //获取现在时间的毫秒
                var nowTime = new Date().getTime();

                //剩余时间
                //Math.floor: 向下取整
                //-5.1 -6  0.9 0
                $scope.secondes = Math.floor((endTime - nowTime) / 1000);

                var time = $interval(function () {
                    if ($scope.secondes > 0) {
                        //时间递减
                        $scope.secondes = $scope.secondes - 1;
                        //时间格式化
                        $scope.timeString = $scope.convertTimeString($scope.secondes);
                    } else {
                        //结束时间递减
                        $interval.cancel(time);
                    }
                }, 1000);


            })
        }


    }

    //时间计算转换
    $scope.convertTimeString = function (allseconds) {
        //计算天数
        var days = Math.floor(allseconds / (60 * 60 * 24));

        //小时
        var hours = Math.floor((allseconds - (days * 60 * 60 * 24)) / (60 * 60));

        //分钟
        var minutes = Math.floor((allseconds - (days * 60 * 60 * 24) - (hours * 60 * 60)) / 60);

        //秒
        var seconds = allseconds - (days * 60 * 60 * 24) - (hours * 60 * 60) - (minutes * 60);

        //拼接时间
        var timString = "";
        if (days > 0) {
            timString = days + "天:";
        }
        return timString += hours + ":" + minutes + ":" + seconds;
    };

    //秒杀下单
    $scope.saveOrder = function (seckillId) {
        //调用服务层方法
        seckillOrderService.saveOrder(seckillId).success(function (data) {

            alert("正在排队中.....");
            //定义时间30s
            $scope.seconds = 30;

            //倒计时查询订单状态
            time = $interval(function () {
                $scope.seconds = $scope.seconds - 1;
                if ($scope.seconds < 1) {
                    $interval.cancel(time);
                    alert("请求超时");
                } else {

                    //查询秒杀下单状态
                    seckillOrderService.checkSeckillOrderStatus().success(function (data) {
                        if (data.success) {
                            location.href = "pay.html";
                        } else if (data.message == "404") {
                            alert("秒杀失败");
                        } else {
                            alert(data.message);
                        }
                    })

                }

            }, 2000)

            //判断
            if (data.success) {
                //1，跳转设置地址
                //2,跳转支付页面
                location.href = "pay.html";
            } else {
                alert(data.message);
            }
        })
    }


});	
