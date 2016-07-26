<%-- 
    Document   : ConfigurationPage
    Created on : Jul 19, 2016, 10:39:51 AM
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
        <h1>Server Configuration Page</h1>
        <table border="1">
            <tr>
                <td>
                    <s:form action="ResetCredentials" namespace="/Config" method="POST">
                        <label for="CACN">New CA's Common Name</label><br/>
                        <input type="text" name="CACN"/><br/>
                        <label for="AACN">New AA's Common Name</label><br/>
                        <input type="text" name="AACN"/>
                        <s:submit>Reset Credentials</s:submit>
                    </s:form>
                <td/>
                <td>
                    <s:form action="UploadRootCredentials" namespace="/Config" method="POST" enctype="multipart/form-data">
                        <s:file name="rootCertFile" label="Root Certificate"/>
                        <s:file name="rootPrivFile" label="Root Private Key"/>
                        <s:file name="rootPubFile" label="Root Public Key"/>
                        <s:submit>Upload Credentials</s:submit>
                    </s:form>
                </td>
                <td>
                    <s:form action="UploadRootAACredentials" namespace="/Config" method="POST" enctype="multipart/form-data">
                        <s:file name="rootCertFile" label="Root AA Certificate"/>
                        <s:file name="rootPrivFile" label="Root AA Private Key"/>
                        <s:file name="rootPubFile" label="Root AA Public Key"/>
                        <s:submit>Upload Credentials</s:submit>
                    </s:form>
                </td>
                <td>
                    <s:form action="UploadTrustedACIssuerCert" namespace="/Config" method="POST" enctype="multipart/form-data">
                        <s:file name="trustedIssuerFile" label="Trusted AC Issuer Certificate"/>
                        <s:submit>Upload Cert</s:submit>
                    </s:form>
                </td>
                <td>
                    <s:form action="SetupEverything" namespace="/Config">
                        <s:submit>Setup Everything</s:submit>
                    </s:form>
                </td>
                <td>
                    <s:form action="SetACFields" namespace="/Config" method="POST">
                        <label for="acRole">AC Role</label><br/>
                        <input type="text" name="acRole"/><br/>
                        <label for="acID">AC Record ID</label><br/>
                        <input type="text" name="acID"/>
                        <label for="acID">AC Record Subject</label><br/>
                        <input type="text" name="acSubj"/>
                        <label for="acTypes">Comma Separated Record Types</label><br/>
                        <input type="text" name="acTypes"/>
                        <label for="acTypes">Comma Separated Actions Taken</label><br/>
                        <input type="text" name="acActionsTaken"/>
                        <s:submit>Set Fields</s:submit>
                    </s:form>
                </td>
                <td>
                    <s:form action="ExamineCerts" namespace="/Config">
                        <s:submit>Examine Certificates</s:submit>
                    </s:form>
                </td>
            </tr>
        </table>
        
    </body>
</html>
