<%-- 
    Document   : FileUploadPage
    Created on : Jul 3, 2016, 12:13:59 PM
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
        <h1>Multi-File Upload Page</h1>
        <s:form action="UploadIDCerts" namespace="/FileTransfer" method="POST" enctype="multipart/form-data">
            <s:file name="uploadedFiles" multiple="multiple" label="myFile"/>
            <s:submit></s:submit>
        </s:form>
        
    </body>
</html>
