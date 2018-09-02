//定义模块
var app = angular.module("pyg", []);
//定义过滤器，处理html代码，让浏览器解析html
app.filter("trustHtml",["$sce",function ($sce) {
    return function (data) {
        //把字符串html代码进行解析，让浏览器能识别
        return $sce.trustAsHtml(data);

    }
}])