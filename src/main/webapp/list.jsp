<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>

<%@ page import ="java.util.ArrayList"%>
<%@ page import ="java.util.List"%>
<%@ page import ="stringutils.StringUtils"%>

<html>
<%
    ArrayList<String> postidList = (ArrayList<String>) request.getAttribute("postidList");
    ArrayList<String> titleList = (ArrayList<String>) request.getAttribute("titleList");
    ArrayList<String> createdTimeList = (ArrayList<String>) request.getAttribute("createdTimeList");
    ArrayList<String> modifiedTimeList = (ArrayList<String>) request.getAttribute("modifiedTimeList");
    int size = (Integer) request.getAttribute("size");
%>
<head>
    <meta charset="UTF-8">
    <title>Post List</title>
</head>
<body>
    <div>
        <form action="post" method="POST">
            <input type="hidden" name="username" value="<%= StringUtils.encodeHtml(request.getParameter("username")) %>" >
            <input type="hidden" name="postid" value="0" >

            <button type="submit" name="action" value="open">New Post</button>
        </form>
    </div>
    <table>
        <tbody>
            <tr><th>Title</th><th>Created</th><th>Modified</th><th>&nbsp;</th></tr>
            <% for (int i=0; i<size; i++) { %>
                <tr>
                <form action="post" method="POST">

                <input type="hidden" name="username" value="<%= StringUtils.encodeHtml(request.getParameter("username")) %>" >
                <input type="hidden" name="postid" value="<%= StringUtils.encodeHtml(postidList.get(i)) %>" >

                <td><%= StringUtils.encodeHtml(titleList.get(i)) %></td>

                <td><%= StringUtils.encodeHtml(createdTimeList.get(i)) %></td>

                <td><%= StringUtils.encodeHtml(modifiedTimeList.get(i)) %></td>

                <td>
                    <button type="submit" name="action" value="open">Open</button>
                    <button type="submit" name="action" value="delete">Delete</button>
                </td>
                </form>
                </tr>
            <% } %>
        </tbody>
    </table>
</body>
</html>
