/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataAccessModulePackage;

import ConfigurationPackage.ConfigAction;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

/**
 *
 * @author jjbillings
 */
public class DataAccessAction extends ActionSupport implements ServletRequestAware{

    private HttpServletRequest servletReq;
    
    public String execute()
    {
        return SUCCESS;
    }
    
    public String generateEMR()
    {
        String path = servletReq.getSession().getServletContext().getRealPath("/");
        String emrDir = path + "/EMRDownload";
        File dir = new File(emrDir);
        if(!dir.exists())
        {
            dir.mkdir();
        }
        File emrFile = new File(emrDir + "/emr.txt");
        if(emrFile.exists())
        {
            emrFile.delete();
        }
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(emrFile));
            writer.write("EMR: THIS IS A TEST EMR.");
            writer.write(" Generated on: " + (new Date()).toString());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfigAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return SUCCESS;
    }
    
    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        this.servletReq = hsr;
    }
    
}
