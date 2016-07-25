/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CAModulePackage;

import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

/**
 * The CAAction (Certificate Authority Action).
 * This class contains a method for each action that the server may need
 * for the Certificate Authority. Each method calls the appropriate method(s)
 * in the CAService to handle whatever needs to happen.
 * @author jjbillings
 */
public class CAAction extends ActionSupport implements ServletRequestAware{
    
    private CAService ca;
    private HttpServletRequest servletReq;
 
    public String execute()
    {
        //path is the filepath to the working directory
        String path = servletReq.getSession().getServletContext().getRealPath("/");
        ca = new CAService(path);
        ca.execute();
        return SUCCESS;
    }
    
    /**
     * Cleans out the uploadedCerts directory from a previous client's
     * connection.
     * @return "success".
     */
    public String removePreviousCerts()
    {
        execute();
        ca.clearPreviousCerts();
        return SUCCESS;
    }
    
    /**
     * Verifies the user's presented credentials.
     * @return - "success" if the user possesses AT LEAST 1 valid ID_Cert.
     * "failure" if they do not have any valid ID_Certs.
     */
    public String verifyIDCerts()
    {
        execute();
        
        System.out.println("Preparing to validate ID Certificates");
        if(ca.validateIDCerts())
        {
            return SUCCESS;
        }else //none of the ID Certs were valid...
        {
            return "failure";
        }
    }
    
    /**
     * This method checks to see if the user has credentials for this System.
     * @return - "success" if the user possesses an ID_Cert that was
     * signed by this system's CA. "failure" if the user does not.
     */
    public String checkForGoodID()
    {
        execute();
        
        System.out.println("Preparing to look for this system's certificate");
        if(ca.checkForGoodID())
        {
            return SUCCESS;
        }else //user doesn't have an id_cert for this system; request CSR
        {
            return "failure";
        }
    }
    
    /**
     * Sign the Certificate Signing Request that the client sends over.
     * @return SUCCESS
     */
    public String signCSR()
    {
        execute();
        ca.signCSR();
        ca.prepareChainForDownload();
        return SUCCESS;
    }

    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        this.servletReq = hsr;
    }
}
