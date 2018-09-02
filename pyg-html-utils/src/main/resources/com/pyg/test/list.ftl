<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>循环集合数据-list指令</title>
</head>
<body>
<table style="height: 300px;width: 600px;" border="1">
    <tr>
        <td>角标</td>
        <td>编号</td>
        <td>姓名</td>
        <td>性别</td>
        <td>年龄</td>
        <td>地址</td>
        <td>操作</td>
    </tr>

<#--
        c标签循环集合：<c:foreach items="${pList}" var="p"> ${p.name}..
        ftl指令List指令：<#list pList as p> ${p.name}
        list指令循环集合角标获取语法：别名_index
-->

<#list pList as p>
    <#if p_index%2==0>
    <tr style="background-color: blue;">
    <#else>
    <tr style="background-color: red;">
    </#if>
    <td>${p_index}</td>
    <td>${p.id!}</td>
    <td>${p.name!}</td>
    <td>${p.sex!}</td>
    <td>${p.age!}</td>
    <td>${p.address!}</td>
    <td>
        <a href="#">删除</a>
        <a href="#">修改</a>
    </td>
</tr>
</#list>

    <h1>共 ${pList?size} 条</h1>

</table>
</body>
</html>