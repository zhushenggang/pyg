//控制层
app.controller('goodsController', function ($scope, $controller,
                                            goodsService,
                                            itemCatService,
                                            typeTemplateService,
                                            uploadService) {

    $controller('baseController', {$scope: $scope});//继承


    //定义查询商品分类方法
    //默认传递参数0，初始化商品分类定义菜单
    $scope.findCat1List = function () {
        itemCatService.findItemCatByParentId(0).success(function (data) {
            $scope.cat1List = data;
        })
    };

    //加载商品分类二级节点，二级节点必须各级选中顶级节点来进行加载
    //使用angularJS 监听服务$watch 服务，监听上级节点变化，一旦上级节点，里面根据节点id查询下级子节点，实现联动效果
    //newValue:当前变化的值
    //oldValue:变化之前的值
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //调用商品分类service服务方法，查询分类节点
        itemCatService.findItemCatByParentId(newValue).success(function (data) {
            $scope.cat2List = data;
        })
    });

    //监听二级节点，查询三级节点，实现三级联动效果
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //调用商品分类service服务方法，查询分类节点
        itemCatService.findItemCatByParentId(newValue).success(function (data) {
            $scope.cat3List = data;
        })
    });


    //监控第三级分类节点id,三级节点有变化，查询获取分类模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //调用商品分类服务findONe方法，根据id查询分类对象
        itemCatService.findOne(newValue).success(function (data) {
            $scope.entity.goods.typeTemplateId = data.typeId;
        })
    });

    //参数结构
    $scope.entity = {goodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []}};

    //监控分类模板id变化，一旦模板id发生变化，根据模板id查询出模板对象
    //获取当前分类模板中品牌数据，此时模板中存储当前分类商品的所有品牌
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        //查询模板对象
        typeTemplateService.findOne(newValue).success(function (data) {
            //先给模板对象复制，进行初始化
            $scope.typeTemplate = data;
            //把字符串json转换成json对象
            $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds)
            //获取模板表扩展属性
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
        });
        //查询规格及规格选项
        typeTemplateService.findSpecList(newValue).success(function (data) {
            $scope.specList = data;
        })
    })


    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            //获取富文本编辑器中文本数据
            $scope.entity.goodsDesc.introduction = editor.html();
            //保存
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.entity = {};
                    //情况kindedtor
                    editor.html('');
                    alert("添加成功");
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    //带条件的分页查询
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //文件上传方法
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (data) {
            //判断
            if (data.success) {
                $scope.image_entity.url = data.message;
            } else {
                alert(data.message);
            }
        })
    };


    //第一阶段，组装参数
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };

    //定义方法判断选中哪个规格中属性值
    //参数1：entity.goodsDesc.specificationItems=[{"attributeName":"网络","attributeValue":["电信2G","联通2G"]}]
    //参数2：attributeName
    //参数3：网络，机身内存
    $scope.searchSpecOption = function (list, key, name) {
        ////$scope.entity.goodsDesc.specificationItems=[{"attributeName": 网络, "attributeValue": [联通4G]}]
        //循环list集合
        for (var i = 0; i < list.length; i++) {
            //判断选中是哪个规格中贵哦选项
            if (list[i][key] == name) {
                return list[i];
            }
        }
        return null;
    }

    //定义方法，组装规格选项参数
    //把规格选项的参数保存商品描述表specificationitems
    //参数1：$event事件，用于判断是选中事件，还是取消事件
    //参数2：规格名称  网络，内存
    //参数3：规格选项  16G,64G
    //entity = {goodsDesc:{specificationItems:[{"attributeName":"网络","attributeValue":["电信2G","联通2G"]},{"attributeName":"机身内存","attributeValue":["32G","16G","64G"]}]}}
    $scope.updateSecOption = function ($event, name, option) {

        //获取商品描述规格参数
        var specList = $scope.entity.goodsDesc.specificationItems;

        //判断添加规格选项是哪个规格中选项
        var obj = $scope.searchSpecOption(specList, 'attributeName', name);

        //判断是否为空
        if (obj != null) {
            //判断是否选中事件
            if ($event.target.checked) {
                obj.attributeValue.push(option);
            } else {
                //取消事件
                obj.attributeValue.splice(obj.attributeValue.indexOf(option), 1);
                //判断如果规格选项已经删除完了，需要把整个规格对象也删除
                if (obj.attributeValue.length == 0) {
                    specList.splice(specList.indexOf(obj), 1);
                }
            }
        } else {

            //第一次选择时，$scope.entity.goodsDesc.specificationItems是空数组，因此对其进行初始化值添加
            $scope.entity.goodsDesc.specificationItems.push(
                {
                    "attributeName": name,
                    "attributeValue": [option]
                });

            //第一次点击后：
            //$scope.entity.goodsDesc.specificationItems=[{"attributeName": 网络, "attributeValue": [联通4G]}]

        }

    };

    //定义方法，根据选中规格选项动态生成sku行
    $scope.createItemList = function () {
        //定义初始化sku行
        $scope.entity.itemList = [{spec: {}, price: 0, num: 999999, status: "1", isDefault: "0"}];

        //获取选中规格选项数据
        var specList = $scope.entity.goodsDesc.specificationItems;

        //判断规格选项是否为空
        if(specList.length==0){
            $scope.entity.itemList = [];
        }

        //循环选中规格循环，根据规格选项生成sku行
        for (var i = 0; i < specList.length; i++) {
            //添加行
            $scope.entity.itemList = addColumn($scope.entity.itemList, specList[i].attributeName, specList[i].attributeValue);
        }

    };

    //entity = {goodsDesc:{specificationItems:[{"attributeName":"网络","attributeValue":["电信2G","联通2G"]},{"attributeName":"机身内存","attributeValue":["32G","16G","64G"]}]}}
    //参数1：$scope.itemList = [{spec: {}, price: 0, num: 999999, status: "1", isDefault: "0"}]
    //参数2：网络
    //参数3：["电信2G","联通2G"]
    addColumn = function (itemList, name, values) {

        //定义新的数组，封装新组装结果
        var newList = [];
        //循环itemList,进行新的行添加
        for (var i = 0; i < itemList.length; i++) {
            //获取旧的行
            var oldRow = itemList[i];

            //根据选中规格选项组装行
            //循环规格选项
            for (var j = 0; j < values.length; j++) {
                //深克隆操作，新创建一行
                var newRow = JSON.parse(JSON.stringify(oldRow));

                //第一次循环结果：[{spec: {"网络":电信2G}}]
                //第二次循环结果：[{spec: {"网络":电信2G}}，{spec: {"网络":联通2G}}]

                newRow.spec[name] = values[j];

                //把新行添加到newList
                newList.push(newRow);

            }

        }

        return newList;

    };

    //status[auditStatus]
    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态

    //定义数组存储分类名称
    $scope.itemCatList = [];

    //查询分类，把所有的分类都封装数组中进行存储
    $scope.findCatAllList = function () {
        //调用分类服务，查询所有分类名称
        itemCatService.findAll().success(function (data) {
            //遍历分类数据集合
            for(var i=0;i<data.length;i++){
                //把分类id作为数组角标，在此角标存储分类名称
                $scope.itemCatList[data[i].id]=data[i].name;
            }
        })
    };

    //商品上下架方法
    $scope.isMarketable = function (status) {
        //调用服务方法
        goodsService.isMarketable(status,$scope.selectIds).success(function (data) {
            //判断
            if(data.success){
                $scope.reloadList();
            }else{
                alert(data.message);
            }
        })
    }

});	
