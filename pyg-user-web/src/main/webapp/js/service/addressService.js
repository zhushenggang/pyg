//服务层
app.service('addressService', function ($http) {

    //读取列表数据绑定到表单中
    this.findAll = function () {
        return $http.get('../address/findAll');
    }
    //分页
    this.findPage = function (page, rows) {
        return $http.get('../address/findPage/' + page + '/' + rows);
    }
    //查询实体
    this.findOne = function (id) {
        return $http.get('../address/findOne/' + id);
    }
    //增加
    this.add = function (entity) {
        return $http.post('../address/add', entity);
    }
    //修改
    this.update = function (entity) {
        return $http.post('../address/update', entity);
    }
    //删除
    this.dele = function (id) {
        return $http.get('../address/delete/' + id);
    }
    //搜索
    this.search = function (page, rows, searchEntity) {
        return $http.post('../address/search/' + page + "/" + rows, searchEntity);
    };

    this.def = function (id,isDefault) {
        return $http.get('../address/def/' + id + "/" + isDefault)
    }
});
