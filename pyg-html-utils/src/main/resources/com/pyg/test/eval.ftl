<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>eval内建函数</title>
<#--assign定义json对象 -->
    <#assign userinfo = '{"username":"张三","age":12}'/>
    <#assign user = userinfo?eval/>
</head>
<body>
<h1>${user.username} || ${user.age}</h1>
</body>
</html>