/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrustNegotiationPackage;

import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

/**
 * This Action is used to check and see what files (if any) the client needs
 * to download.
 * This could definitely be refactored into the TrustNegotiatorAction class,
 * but then we would need to exclude downloadURLs/downloadActions from each
 * result.
 * @author jjbillings
 */
public class FileDownloadNegotiatorAction extends ActionSupport implements ServletRequestAware {
    
    
    private String[] downloadURLs;
    private String[] downloadActions;
    private String action;
    private String next;
    private HttpServletRequest servletReq;
    
    public String execute()
    {
        return SUCCESS;
    }
    
    /**
     * This method checks the directories that the client will download
     * the AC,EMR,ID_Cert, ID_Cert_Chain, and AC_Cert_Chain from. it adds a download URL
     * and action for each download that the user needs to do.
     * @return 
     */
    public String checkDownload()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        ArrayList<String> tempActions = new ArrayList();
        ArrayList<String> tempURLs = new ArrayList();
        String baseURL = "https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/";  
        String basePath = servletReq.getSession().getServletContext().getRealPath("/");
        
        setAction(TNActions.CHECK_DOWNLOAD);
        setNext("DUMMY");
        
        System.out.println("Checking to see what needs to be downloaded");
        File dir  = new File(basePath + "/GeneratedIDCerts");
        File[] files = dir.listFiles();
        
        //>1 because the .DS_Store is in the directory.
        if(files.length > 1)
        {
            System.out.println("The client should download IDCerts... and Chain");
            tempActions.add(TNActions.DOWNLOAD_ID_CERTS);
            tempURLs.add(baseURL + "DownloadIDCerts.action");
            
            //If a new ID cert was generated, the user should also download the
            //corresponding certificate chain.
            tempActions.add(TNActions.DOWNLOAD_CHAINS);
            tempURLs.add(baseURL + "DownloadChain.action");
        }
        
        dir = new File(basePath + "/GeneratedACs");
        files = dir.listFiles();
        if(files.length > 1)
        {
            System.out.println("The client should download ACs...");
            tempActions.add(TNActions.DOWNLOAD_ACS);
            tempURLs.add(baseURL + "DownloadACs.action");
            
            tempActions.add(TNActions.DOWNLOAD_CHAINS);
            tempURLs.add(baseURL + "DownloadACChain.action");
        }
        
        dir = new File(basePath + "/EMRDownload");
        files = dir.listFiles();
        if(files.length >= 1)
        {
            System.out.println("The client should download EMR...");
            tempActions.add(TNActions.DOWNLOAD_EMR);
            tempURLs.add(baseURL + "DownloadEMR.action");
        }
        
        
        if(tempActions.size() <= 0 || tempURLs.size() <= 0)
        {
            setAction(TNActions.DISCONNECT);
            setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/InitiateConnection.action");
            tempActions.add(TNActions.DISCONNECT);
            tempActions.add("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/InitiateConnection.action");
            System.out.println("Client shouldn't download anything");
            String[] ta = tempActions.toArray(new String[tempActions.size()]);
            String[] turl = tempURLs.toArray(new String[tempURLs.size()]);
            setDownloadActions(ta);
            setDownloadURLs(turl);
            return "NONE";
        }else
        {
            String[] ta = tempActions.toArray(new String[tempActions.size()]);
            String[] turl = tempURLs.toArray(new String[tempActions.size()]);
            setDownloadActions(ta);
            setDownloadURLs(turl);
            return SUCCESS;
        }
        
    }

    /**
     * @return the downloadURLs
     */
    public String[] getDownloadURLs() {
        return downloadURLs;
    }

    /**
     * @param downloadURLs the downloadURLs to set
     */
    public void setDownloadURLs(String[] downloadURLs) {
        this.downloadURLs = downloadURLs;
    }

    /**
     * @return the downloadActions
     */
    public String[] getDownloadActions() {
        return downloadActions;
    }

    /**
     * @param downloadActions the downloadActions to set
     */
    public void setDownloadActions(String[] downloadActions) {
        this.downloadActions = downloadActions;
    }

    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        this.servletReq = hsr;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the next
     */
    public String getNext() {
        return next;
    }

    /**
     * @param next the next to set
     */
    public void setNext(String next) {
        this.next = next;
    }
    
}
