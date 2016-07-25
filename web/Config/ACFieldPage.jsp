<%-- 
    Document   : ACFieldPage
    Created on : Jul 22, 2016, 1:44:09 PM
    Author     : jbillz
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <textarea>
            Role: ${acRole}
            Record ID: ${acID}
            Record Types: ${acTypes}
        </textarea>
    </body>
</html>
