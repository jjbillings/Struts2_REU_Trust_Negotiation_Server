/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileTransferPackage;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

/**
 *
 * @author jbillz
 */
public class ConfigFileUpload extends ActionSupport implements ServletRequestAware {
    
    private File rootCertFile, rootPubFile, rootPrivFile, trustedIssuerFile;
    private String rootCertFileContentType, rootPubFileContentType, rootPrivFileContentType,trustedIssuerFileContentType;
    private String rootCertFileFileName, rootPubFileFileName, rootPrivFileFileName, trustedIssuerFileFileName;
    private String rootCertFileFilePath, rootPubFileFilePath, rootPrivFileFilePath, trustedIssuerFileFilePath;
    private HttpServletRequest servletRequest;
    
    public String execute()
    {
        return SUCCESS;
    }
    
    public String uploadRootCACredentials()
    {
        String path = getServletRequest().getSession().getServletContext().getRealPath("/");
        File certFile = new File(path + "Security/Certificates/Root_CA/root_cert.pem");
        File publicFile = new File(path + "Security/Keystore/Root_CA/publicKey.key");
        File privateFile = new File(path + "Security/Keystore/Root_CA/privateKey.key");
        
        
        if(certFile.exists())
        {
            certFile.delete();
        }
        
        if(publicFile.exists())
        {
            publicFile.delete();
        }
        
        if(privateFile.exists())
        {
            privateFile.delete();
        }
        
        try {
            FileUtils.copyFile(getRootCertFile(), certFile);
            FileUtils.copyFile(getRootPubFile(), publicFile);
            FileUtils.copyFile(getRootPrivFile(), privateFile);
        } catch (IOException ex) {
            Logger.getLogger(ConfigFileUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return SUCCESS;
    }
    
    public String uploadRootAACredentials()
    {
        String path = getServletRequest().getSession().getServletContext().getRealPath("/");
        File certFile = new File(path + "AA/Certs/Root_AA/root_cert.pem");
        File publicFile = new File(path + "AA/Keystore/Root_AA/publicKey.key");
        File privateFile = new File(path + "AA/Keystore/Root_AA/privateKey.key");
        
        if(certFile.exists())
        {
            certFile.delete();
        }
        
        if(publicFile.exists())
        {
            publicFile.delete();
        }
        
        if(privateFile.exists())
        {
            privateFile.delete();
        }
        
        try {
            FileUtils.copyFile(getRootCertFile(), certFile);
            FileUtils.copyFile(getRootPubFile(), publicFile);
            FileUtils.copyFile(getRootPrivFile(), privateFile);
        } catch (IOException ex) {
            Logger.getLogger(ConfigFileUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return SUCCESS;
    }
    
    public String uploadTrustedACIssuerCert()
    {
        String path = getServletRequest().getSession().getServletContext().getRealPath("/");
        File certFile = new File(path + "AA/Certs/Trusted_AC_Issuer_Certs/" + getTrustedIssuerFileFileName());
        
        try {
            FileUtils.copyFile(getTrustedIssuerFile(), certFile);
        } catch (IOException ex) {
            Logger.getLogger(ConfigFileUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        return SUCCESS;
    }
    

    //***********************GETTERS/SETTERS**********************************\\
    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        this.servletRequest = hsr;
    }

    /**
     * @return the rootCertFile
     */
    public File getRootCertFile() {
        return rootCertFile;
    }

    /**
     * @param rootCertFile the rootCertFile to set
     */
    public void setRootCertFile(File rootCertFile) {
        this.rootCertFile = rootCertFile;
    }

    /**
     * @return the rootPubFile
     */
    public File getRootPubFile() {
        return rootPubFile;
    }

    /**
     * @param rootPubFile the rootPubFile to set
     */
    public void setRootPubFile(File rootPubFile) {
        this.rootPubFile = rootPubFile;
    }

    /**
     * @return the rootPrivFile
     */
    public File getRootPrivFile() {
        return rootPrivFile;
    }

    /**
     * @param rootPrivFile the rootPrivFile to set
     */
    public void setRootPrivFile(File rootPrivFile) {
        this.rootPrivFile = rootPrivFile;
    }

    /**
     * @return the rootCertFileContentType
     */
    public String getRootCertFileContentType() {
        return rootCertFileContentType;
    }

    /**
     * @param rootCertFileContentType the rootCertFileContentType to set
     */
    public void setRootCertFileContentType(String rootCertFileContentType) {
        this.rootCertFileContentType = rootCertFileContentType;
    }

    /**
     * @return the rootPubFileContentType
     */
    public String getRootPubFileContentType() {
        return rootPubFileContentType;
    }

    /**
     * @param rootPubFileContentType the rootPubFileContentType to set
     */
    public void setRootPubFileContentType(String rootPubFileContentType) {
        this.rootPubFileContentType = rootPubFileContentType;
    }

    /**
     * @return the rootPrivFileContentType
     */
    public String getRootPrivFileContentType() {
        return rootPrivFileContentType;
    }

    /**
     * @param rootPrivFileContentType the rootPrivFileContentType to set
     */
    public void setRootPrivFileContentType(String rootPrivFileContentType) {
        this.rootPrivFileContentType = rootPrivFileContentType;
    }

    /**
     * @return the rootCertFileFileName
     */
    public String getRootCertFileFileName() {
        return rootCertFileFileName;
    }

    /**
     * @param rootCertFileFileName the rootCertFileFileName to set
     */
    public void setRootCertFileFileName(String rootCertFileFileName) {
        this.rootCertFileFileName = rootCertFileFileName;
    }

    /**
     * @return the rootPubFileFileName
     */
    public String getRootPubFileFileName() {
        return rootPubFileFileName;
    }

    /**
     * @param rootPubFileFileName the rootPubFileFileName to set
     */
    public void setRootPubFileFileName(String rootPubFileFileName) {
        this.rootPubFileFileName = rootPubFileFileName;
    }

    /**
     * @return the rootPrivFileFileName
     */
    public String getRootPrivFileFileName() {
        return rootPrivFileFileName;
    }

    /**
     * @param rootPrivFileFileName the rootPrivFileFileName to set
     */
    public void setRootPrivFileFileName(String rootPrivFileFileName) {
        this.rootPrivFileFileName = rootPrivFileFileName;
    }

    /**
     * @return the rootCertFileFilePath
     */
    public String getRootCertFileFilePath() {
        return rootCertFileFilePath;
    }

    /**
     * @param rootCertFileFilePath the rootCertFileFilePath to set
     */
    public void setRootCertFileFilePath(String rootCertFileFilePath) {
        this.rootCertFileFilePath = rootCertFileFilePath;
    }

    /**
     * @return the rootPubFileFilePath
     */
    public String getRootPubFileFilePath() {
        return rootPubFileFilePath;
    }

    /**
     * @param rootPubFileFilePath the rootPubFileFilePath to set
     */
    public void setRootPubFileFilePath(String rootPubFileFilePath) {
        this.rootPubFileFilePath = rootPubFileFilePath;
    }

    /**
     * @return the rootPrivFileFilePath
     */
    public String getRootPrivFileFilePath() {
        return rootPrivFileFilePath;
    }

    /**
     * @param rootPrivFileFilePath the rootPrivFileFilePath to set
     */
    public void setRootPrivFileFilePath(String rootPrivFileFilePath) {
        this.rootPrivFileFilePath = rootPrivFileFilePath;
    }

    /**
     * @return the servletRequest
     */
    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    /**
     * @return the trustedIssuerFile
     */
    public File getTrustedIssuerFile() {
        return trustedIssuerFile;
    }

    /**
     * @param trustedIssuerFile the trustedIssuerFile to set
     */
    public void setTrustedIssuerFile(File trustedIssuerFile) {
        this.trustedIssuerFile = trustedIssuerFile;
    }

    /**
     * @return the trustedIssuerFileContentType
     */
    public String getTrustedIssuerFileContentType() {
        return trustedIssuerFileContentType;
    }

    /**
     * @param trustedIssuerFileContentType the trustedIssuerFileContentType to set
     */
    public void setTrustedIssuerFileContentType(String trustedIssuerFileContentType) {
        this.trustedIssuerFileContentType = trustedIssuerFileContentType;
    }

    /**
     * @return the trustedIssuerFileFileName
     */
    public String getTrustedIssuerFileFileName() {
        return trustedIssuerFileFileName;
    }

    /**
     * @param trustedIssuerFileFileName the trustedIssuerFileFileName to set
     */
    public void setTrustedIssuerFileFileName(String trustedIssuerFileFileName) {
        this.trustedIssuerFileFileName = trustedIssuerFileFileName;
    }

    /**
     * @return the trustedIssuerFileFilePath
     */
    public String getTrustedIssuerFileFilePath() {
        return trustedIssuerFileFilePath;
    }

    /**
     * @param trustedIssuerFileFilePath the trustedIssuerFileFilePath to set
     */
    public void setTrustedIssuerFileFilePath(String trustedIssuerFileFilePath) {
        this.trustedIssuerFileFilePath = trustedIssuerFileFilePath;
    }
}
