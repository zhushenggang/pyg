<%--
  Created by IntelliJ IDEA.
  User: hubin
  Date: 2018/8/25
  Time: 16:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>单点登录系统测试-1</title>
</head>
<body>
<h1 style="background-color: yellow">
欢迎您!<%=request.getRemoteUser()%> 访问本系统，热烈欢迎。
</h1>
<hr size="2" color="blue">
<a href="http://192.168.66.66:9100/cas/logout?service=http://www.baidu.com">单点退出</a>
</body>
</html>
