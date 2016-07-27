/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileTransferPackage;

import AAModulePackage.ACHelper;
import CAModulePackage.CertificateHelper;
import com.opensymphony.xwork2.ActionSupport;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;

/**
 *
 * @author jbillz
 */
public class FileDownload extends ActionSupport implements ServletRequestAware{
    
    private InputStream stream;
    private String filename;
    private HttpServletRequest servletReq;
    
    public String execute() throws Exception
    {   
        return SUCCESS;
    }
    
    public String checkDownload()
    {
        System.out.println("CHecking to see what needs to be downloaded");
        File dir  = new File(servletReq.getSession().getServletContext().getRealPath("/") + "/GeneratedIDCerts");
        File[] files = dir.listFiles();
        if(files.length > 1)
        {
            System.out.println("The client should download IDCerts...");
            return "IDCerts";
        }
        
        dir = new File(servletReq.getSession().getServletContext().getRealPath("/") + "/GeneratedACs");
        files = dir.listFiles();
        if(files.length > 1)
        {
            System.out.println("The client should download ACs...");
            return "ACs";
        }
        
        dir = new File(servletReq.getSession().getServletContext().getRealPath("/") + "/EMRDownload");
        files = dir.listFiles();
        if(files.length > 1)
        {
            System.out.println("The client should download EMR...");
            return "EMRs";
        }
        
        System.out.println("Client shouldn't download anything");
        return "NONE";
    }
    
    
    public String downloadIDCert()
    {
        System.out.println("Client downloading ID");
        File dir  = new File(servletReq.getSession().getServletContext().getRealPath("/") + "/GeneratedIDCerts");
        File[] files = dir.listFiles();
        X509CertificateHolder cert = null;
        
        for(File f : files)
        {
            if(f.getName().equalsIgnoreCase(".DS_Store") || f.isDirectory())
            {
                continue;
            }
            
            cert = CertificateHelper.loadCertFromFile(f);
            try {
                stream = new ByteArrayInputStream(cert.getEncoded());
            } catch (IOException ex) {
                Logger.getLogger(FileDownload.class.getName()).log(Level.SEVERE, null, ex);
                return "failure";
            }
            f.delete();
        }
        
        return SUCCESS;
    }
    
    public String downloadChain()
    {
        System.out.println("Client downloading Chain");
        File dir  = new File(servletReq.getSession().getServletContext().getRealPath("/") + "/GeneratedIDCerts/Chain");
        File[] files = dir.listFiles();
        
        for(File f : files)
        {
            if(f.getName().equalsIgnoreCase(".DS_Store"))
            {
                continue;
            }
            X509CertificateHolder cert = null;
            
            cert = CertificateHelper.loadCertFromFile(f);
            try {
                stream = new ByteArrayInputStream(cert.getEncoded());
            } catch (IOException ex) {
                Logger.getLogger(FileDownload.class.getName()).log(Level.SEVERE, null, ex);
                return "failure";
            }
            f.delete();
            
        }
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException ex) {
            Logger.getLogger(FileDownload.class.getName()).log(Level.SEVERE, null, ex);
        }
        return SUCCESS;
    }
    
    public String downloadACChain()
    {
        System.out.println("Client downloading Chain");
        File dir  = new File(servletReq.getSession().getServletContext().getRealPath("/") + "/GeneratedACs/Chain");
        File[] files = dir.listFiles();
        
        for(File f : files)
        {
            if(f.getName().equalsIgnoreCase(".DS_Store"))
            {
                continue;
            }
            X509CertificateHolder cert = null;
            
            cert = CertificateHelper.loadCertFromFile(f);
            try {
                stream = new ByteArrayInputStream(cert.getEncoded());
            } catch (IOException ex) {
                Logger.getLogger(FileDownload.class.getName()).log(Level.SEVERE, null, ex);
                return "failure";
            }
            f.delete();
            
        }
        dir.delete();
        return SUCCESS;
    }
    
    public String downloadAC()
    {
        System.out.println("Client downloading AC");
        File dir  = new File(servletReq.getSession().getServletContext().getRealPath("/") + "/GeneratedACs");
        File[] files = dir.listFiles();
        X509AttributeCertificateHolder ac = null;
        
        for(File f : files)
        {
            //Skip .DS_Store and the chain directory
            if(f.getName().equalsIgnoreCase(".DS_Store") || f.isDirectory())
            {
                continue;
            }
            
            ac = ACHelper.loadAttributeCertFromFile(f);
            try {
                stream = new ByteArrayInputStream(ac.getEncoded());
            } catch (IOException ex) {
                Logger.getLogger(FileDownload.class.getName()).log(Level.SEVERE, null, ex);
                return "failure";
            }
            f.delete();
        }
        
        return SUCCESS;
    }
    
    public String downloadEMR()
    {        
        System.out.println("Client downloading EMR");
        //TODO: Put this wherever we get the EMR and save it to the directory.
        //As it is, if the dir doesn't exist, there's definitely nothing to download...
        File dir  = new File(servletReq.getSession().getServletContext().getRealPath("/") + "/EMRDownload");
        if(!dir.exists())
        {
            return "failure";
        }
        File[] files = dir.listFiles();
        
        for(File f : files)
        {
            if(f.getName().equalsIgnoreCase(".DS_Store"))
            {
                continue;
            }
            
            try {
                stream = new FileInputStream(f);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileDownload.class.getName()).log(Level.SEVERE, null, ex);
                return "failure";
            }
            f.delete();
        }
        
        return SUCCESS;
    }
    
    //**************************GETTERS/SETTERS*******************************\\
    public InputStream getStream()
    {
        return this.stream;
    }

    @Override
    public void setServletRequest(HttpServletRequest hsr) {
           this.servletReq = hsr;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
