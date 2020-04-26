<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<%@ page import ="stringutils.StringUtils"%>

<html>
<head>
    <meta charset="UTF-8">
    <title>Invalid Request</title>
</head>
<body>
    <h1>Invalid Request</h1>
    
    <h2>Request</h2>
    <b>action:</b> <%= StringUtils.encodeHtml(request.getParameter("action")) %><br>
    <b>username:</b> <%= StringUtils.encodeHtml(request.getParameter("username")) %><br>
    <b>postid:</b> <%= StringUtils.encodeHtml(request.getParameter("postid")) %><br>
    <b>title:</b> <%= StringUtils.encodeHtml(request.getParameter("title")) %><br>
    <b>body:</b> <%= StringUtils.encodeHtml(request.getParameter("body")) %><br>

    <h2>Reason of Error</h2>
    <!-- Check for Why Its Bad. Bad GET. Bad Parameters (Missing Username) -->
    Action <%= request.getParameter("action") %> cannot be issued via GET method
</body>
</html>
