/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrustNegotiationPackage;

import com.opensymphony.xwork2.ActionSupport;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trust Negotiator Action.
 * 
 * @author jjbillings
 */
public class TrustNegotiatorAction extends ActionSupport{
    
    //The action we will request the user to carry out.
    private String action;
    private String next;
    private String error;
      
    /**
     * Triggered when the client initially connects to the server.
     * @return SUCCESS
     */
    public String execute()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        System.out.println("Telling client to upload IDCerts");
        setAction(TNActions.UPLOAD_ID_CERTS);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/UploadIDCerts.action");
        
        System.out.println(getAction());
        return SUCCESS;
    }
    
    public String removePreviousCerts()
    {
        return SUCCESS;
    }
            
    public String idCertsUploaded()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        System.out.println("Telling client to upload Cert Chains");
        setAction(TNActions.UPLOAD_CHAINS);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/UploadChains.action");
        return SUCCESS;
    }
    
    public String verifiedIDCerts()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        System.out.println("Telling client to upload ACs");
        setAction(TNActions.UPLOAD_ACS);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/UploadACs.action");
        return SUCCESS;
    }
    
    public String acsUploaded()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        System.out.println("Telling client to upload Cert Chains");
        setAction(TNActions.UPLOAD_AC_CHAINS);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/UploadACChains.action");
        return SUCCESS;
    }
    
    //TODO: This method should actually redirect to an access policy module that
    //checks to see if the ACs are enough to grant the client access to the EMR
    public String verifiedACs()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        //TODO: something when the ACs have been verified
        setAction("...");
        setNext("https://...");
        return SUCCESS;
    }
    
    public String failedToVerifyIDCerts()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        setAction(TNActions.SEND_MORE_ACS);
        setError("None of the ID Certificates could be verified.");
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/UploadIDCerts.action");
        return SUCCESS;
    }
    
    public String failedToVerifyACs()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        setAction(TNActions.SEND_MORE_ACS);
        setError("None of the Attribute Certificates could be verified");
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/UploadACs.action");
        return SUCCESS;
    }
    
    public String authorizedAccess()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        System.out.println("Telling client to check the downloads");
        setAction(TNActions.CHECK_DOWNLOAD);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/TrustNegotiator/CheckDownload.action");
        return SUCCESS;
    }
    
    public String accessDenied()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        setAction(TNActions.SEND_MORE_ACS);
        setError("The credentials supplied were insufficient to grant access to the requested data.");
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/UploadIDCerts.action");
        return SUCCESS;
    }
    
    public String noMoreIDCerts()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        System.out.println("No more IDs to download, telling client to download ACs");
        setAction(TNActions.DOWNLOAD_ACS);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/DownloadACs.action");
        return SUCCESS;
    }
    
    public String noMoreACs()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        System.out.println("Telling client to download EMR");
        setAction(TNActions.DOWNLOAD_EMR);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/DownloadEMR.action");
        return SUCCESS;
    }
    
    public String noMoreEMRs()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        System.out.println("No more EMRs to download, telling client to disconnect");
        setAction(TNActions.DISCONNECT);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/TrustNegotiator/InitiateConnection.action");
        return SUCCESS;
    }
    
    public String requestCSR()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        System.out.println("Telling client to upload CSR");
        setAction(TNActions.UPLOAD_CSR);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/FileTransfer/UploadCSR.action");
        return SUCCESS;
    }
    
    public String acGenerated()
    {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TrustNegotiatorAction.class.getName()).log(Level.SEVERE, null, ex);
            return "failure";
        }
        
        System.out.println("AC Generated: Telling client to check download.");
        setAction(TNActions.CHECK_DOWNLOAD);
        setNext("https://" + ip + ":8443/Struts2_REU_Trust_Negotiation_Server/TrustNegotiator/CheckDownload.action");
        return SUCCESS;
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

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

}
