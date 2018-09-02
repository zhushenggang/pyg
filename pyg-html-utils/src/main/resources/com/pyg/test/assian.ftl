<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>assain常量指令</title>
<#assign linkman="周大海"/>
<#--assign定义json对象 -->
    <#assign userinfo = {"username":"张三","age":12}/>
</head>
<body>
<h1>${linkman}</h1>
<hr>
<h1>${userinfo.username} || ${userinfo.age}</h1>
</body>
</html>