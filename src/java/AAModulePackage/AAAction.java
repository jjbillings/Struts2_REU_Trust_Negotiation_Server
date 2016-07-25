/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AAModulePackage;

import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

/**
 * AAAction (Attribute Authority Action).
 * 
 * @author jjbillings
 */
public class AAAction extends ActionSupport implements ServletRequestAware {
    
    private AAService aa;
    private HttpServletRequest servletReq;
   
    
    public String execute()
    {
        String path = servletReq.getSession().getServletContext().getRealPath("/");
        aa = new AAService(path);
        return SUCCESS;
    }
    
    
    public String generateACs()
    {
        String path = servletReq.getSession().getServletContext().getRealPath("/");
        aa = new AAService(path);
        aa.generateAC(path);
        return SUCCESS;
    }
    
    public String verifyACs()
    {
        String serverPath = servletReq.getSession().getServletContext().getRealPath("/");
        aa = new AAService(serverPath);
        if(!aa.verifyACs())
        {
            return "failure";
        }
        
        return SUCCESS;
    }

    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        this.servletReq = hsr;
    }
}
