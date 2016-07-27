/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConfigurationPackage;

import AAModulePackage.AAService;
import CAModulePackage.CAService;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

/**
 * Server Configuration Action.
 * This class provides functionality for configuring the server.
 * This allows for the resetting of the CA and AA credentials, as well as
 * setting attributes of the AC that will be generated when the client is
 * successfully authorized to the data.
 * @author jjbillings
 */
public class ConfigAction extends ActionSupport implements ServletRequestAware{
    
    private String CACN;
    private String AACN;
    private String certInfo;
    private String acRole, acID, acTypes, acSubj, acActionsTaken;
    private HttpServletRequest servletReq;
    
    public String execute()
    {
        return SUCCESS;
    }
    
    /**
     * Sets the Attributes of the X.509 Attribute Certificate that is to be
     * generated for the user.
     * @return "success".
     */
    public String setACFields()
    {
        String path = servletReq.getSession().getServletContext().getRealPath("/");
        File dir = new File(path + "/AA/ACFields");
        if(!dir.exists())
        {
            dir.mkdir();
        }
        File f = new File(path+"/AA/ACFields/ac.txt");
        if(f.exists())
        {
            f.delete();
        }
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(path+"/AA/ACFields/ac.txt"));
            writer.write(acRole + ", ");
            writer.write(acID + ", ");
            writer.write(getAcSubj() + ", ");
            writer.write(acTypes + ", ");
            writer.write("acActionsTaken, ");
            writer.write(getAcActionsTaken());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfigAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return SUCCESS;
    }
    
    /**
     * Extracts information about the CA and AA's certificates. It then makes
     * this information available so that we may display it on a JSP.
     * @return 
     */
    public String examineCerts()
    {
        String path = servletReq.getSession().getServletContext().getRealPath("/");
        
        StringBuilder strb = new StringBuilder();
        
        CAService ca = new CAService(path);
        ca.execute();
        strb.append(ca.checkCerts());
        
        AAService aa = new AAService(path);
        strb.append(aa.checkCerts());
        
        setCertInfo(strb.toString());
        
        return SUCCESS;
    }
    
    /**
     * Resets The CA and AA's credentials. New X.509 ID_Cert is generated for
     * each, using the Common Names that are entered on the JSP.
     * @return "success".
     */
    public String resetCredentials()
    {
        System.out.println(getCACN() + ", " + getAACN());
        
        String path = servletReq.getSession().getServletContext().getRealPath("/");
        
        CAService ca = new CAService(path);
        ca.execute();
        ca.resetCACert(getCACN());
        
        AAService aa = new AAService(path);
        aa.resetAACert(getAACN());
        
        return SUCCESS;
    }

    /**
     * Sets up the server.
     * @return 
     */
    public String setupEverything()
    {
        String path = servletReq.getSession().getServletContext().getRealPath("/");
        CAService ca = new CAService(path);
        ca.execute();
        
        AAService aa = new AAService(path);
        return SUCCESS;
    }
    /**
     * @return the CACN
     */
    public String getCACN() {
        return CACN;
    }

    /**
     * @param CACN the CACN to set
     */
    public void setCACN(String CACN) {
        this.CACN = CACN;
    }

    /**
     * @return the AACN
     */
    public String getAACN() {
        return AACN;
    }

    /**
     * @param AACN the AACN to set
     */
    public void setAACN(String AACN) {
        this.AACN = AACN;
    }

    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        this.servletReq = hsr;
    }

    /**
     * @return the certInfo
     */
    public String getCertInfo() {
        return certInfo;
    }

    /**
     * @param certInfo the certInfo to set
     */
    public void setCertInfo(String certInfo) {
        this.certInfo = certInfo;
    }

    /**
     * @return the acRole
     */
    public String getAcRole() {
        return acRole;
    }

    /**
     * @param acRole the acRole to set
     */
    public void setAcRole(String acRole) {
        this.acRole = acRole;
    }

    /**
     * @return the acID
     */
    public String getAcID() {
        return acID;
    }

    /**
     * @param acID the acID to set
     */
    public void setAcID(String acID) {
        this.acID = acID;
    }

    /**
     * @return the acTypes
     */
    public String getAcTypes() {
        return acTypes;
    }

    /**
     * @param acTypes the acTypes to set
     */
    public void setAcTypes(String acTypes) {
        this.acTypes = acTypes;
    }

    /**
     * @return the acSubj
     */
    public String getAcSubj() {
        return acSubj;
    }

    /**
     * @param acSubj the acSubj to set
     */
    public void setAcSubj(String acSubj) {
        this.acSubj = acSubj;
    }

    /**
     * @return the acActionsTaken
     */
    public String getAcActionsTaken() {
        return acActionsTaken;
    }

    /**
     * @param acActionsTaken the acActionsTaken to set
     */
    public void setAcActionsTaken(String acActionsTaken) {
        this.acActionsTaken = acActionsTaken;
    }

}
