<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>NULL值处理问题</title>
</head>
<body>
<h1>第一种处理方法：内建函数处理NULL值</h1>
<h2>
    默认值：${name?default("默认值")} <br>
    不显示：${name?default("")}
</h2>
<hr color="red" size="2">
<h1>第一种处理方法：! 处理NULL值</h1>
<h2>
    默认值：${name!"默认值"} <br>
    不显示：${name!}
</h2>
<hr color="red" size="2">
<h1>第一种处理方法：判断指令处理NULL值</h1>
<h2>
    <#if name??>
        ${name}
    </#if>
</h2>

</body>
</html>