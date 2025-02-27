<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>

<%@ page import ="stringutils.StringUtils"%>

<head>
    <meta charset="UTF-8">
    <title>Preview Post</title>
</head>
<body>
    <div>
        <form action="post" method="POST">
            <input type="hidden" name="username" value="<%= StringUtils.encodeHtml(request.getParameter("username")) %>" >
            <input type="hidden" name="postid" value="<%= StringUtils.encodeHtml(request.getParameter("postid")) %>" >
            <input type="hidden" name="title" value="<%= StringUtils.encodeHtml(request.getParameter("title")) %>" >
            <input type="hidden" name="body" value="<%= StringUtils.encodeHtml(request.getParameter("body")) %>" >
            <button type="submit" name="action" value="open">Close Preview</button>
        </form>
    </div>
    <div>
        <h1 id="title"><%= request.getAttribute("title") %></h1>
        <div id="body"><%= request.getAttribute("body") %></div>
    </div>
</body>
</html>
