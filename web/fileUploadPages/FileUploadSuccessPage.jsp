<%-- 
    Document   : FileUploadSuccessPage
    Created on : Jul 3, 2016, 12:27:29 PM
    Author     : jbillz
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>SUCCESS</h1>
        You have uploaded the following file.
        <hr>
        File Name : ${uploadedFilesFileName} <br>
        Content Type : ${uploadedFilesContentType}
    </body>
</html>
