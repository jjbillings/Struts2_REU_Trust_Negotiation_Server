/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileTransferPackage;

import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

/**
 *
 * @author jjbillings
 */
public class MultiFileUpload extends ActionSupport implements ServletRequestAware{
    
    private List<File> uploadedFiles;
    private List<String> uploadedFilesFileName;
    private List<String> uploadedFilesContentType;
    private List<String> uploadedFilesFilePath;
    
    private HttpServletRequest servletReq;
    private String path;
    
    public String execute()
    {
        return SUCCESS;
    }
    
    public String uploadACs()
    {
        path = servletReq.getSession().getServletContext().getRealPath("/") + "UploadedCerts/ACs";
        if(saveACFiles(path))
        {
            System.out.println("ACs saved to File");
            return SUCCESS;
        }else
        {
            System.out.println("ACs FAILED saved to File");
            return "failure";
        }
    }
    
    public String uploadIDCerts()
    {
        path = servletReq.getSession().getServletContext().getRealPath("/") + "UploadedCerts/IDCerts";
        if(saveIDCertFiles(path))
        {
            return SUCCESS;
        }else
        {
            return "failure";
        }
    }
    
    public String uploadCertChains()
    {
        path = servletReq.getSession().getServletContext().getRealPath("/") + "UploadedCerts/IDCerts";
        if(saveChainFiles(path))
        {
            return SUCCESS;
        }else
        {
            return "failure";
        }
    }
    
    public String uploadACChains()
    {
        path = servletReq.getSession().getServletContext().getRealPath("/") + "UploadedCerts/ACs";
        if(saveChainFiles(path))
        {
            return SUCCESS;
        }else
        {
            return "failure";
        }
    }
    
    public String uploadCSR()
    {
        path = servletReq.getSession().getServletContext().getRealPath("/") + "UploadedCSR";
        if(saveFiles(path))
        {
            return SUCCESS;
        }else
        {
            return "failure";
        }
    }
    
    public boolean saveFiles(String filePath)
    {
        for(int i = 0; i < uploadedFiles.size(); ++i)
        {
            System.out.println("FILE_NAME: " + getUploadedFilesFileName().get(i));
            System.out.println("FILE_CONTENT_TYPE: " + getUploadedFilesContentType().get(i));
                        
            File dir = new File(path);

            if(!dir.exists())
            {
               dir.mkdirs();
            }else if (dir.isDirectory()) {
                //Clear out the directory
                String[] children = dir.list();
                for (String children1 : children) {
                    new File(dir, children1).delete();
                }
            }
            try {
                FileUtils.copyFile(getUploadedFiles().get(i), new File(path + "/" + getUploadedFilesFileName().get(i)));
            } catch (IOException ex) {
                Logger.getLogger(MultiFileUpload.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }//End-For
        return true;
    }
    
    public boolean saveACFiles(String path)
    {
        for(int i = 0; i < uploadedFiles.size(); ++i)
        {
            System.out.println("FILE_NAME: " + getUploadedFilesFileName().get(i));
            System.out.println("FILE_CONTENT_TYPE: " + getUploadedFilesContentType().get(i));
                        
            File dir = new File(path);

            if(!dir.exists())
            {
               dir.mkdirs();
            }else if (dir.isDirectory()) {
                //Clear out the directory
                String[] children = dir.list();
                for (String children1 : children) {
                    new File(dir, children1).delete();
                }
            }
            
            String name = getUploadedFilesFileName().get(i);
            String targetDir = name.substring(0, name.length() - 4);
            File acDir = new File(path + "/" + targetDir);
            if(!acDir.exists())
            {
                acDir.mkdir();
            }
            
            try {
                FileUtils.copyFile(getUploadedFiles().get(i), new File(path + "/" + targetDir + "/" + name));
            } catch (IOException ex) {
                Logger.getLogger(MultiFileUpload.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }//End-For
        return true;
    }
    
    public boolean saveIDCertFiles(String path)
    {
        for(int i = 0; i < uploadedFiles.size(); ++i)
        {
            System.out.println("FILE_NAME: " + getUploadedFilesFileName().get(i));
            System.out.println("FILE_CONTENT_TYPE: " + getUploadedFilesContentType().get(i));
                        
            File dir = new File(path);

            if(!dir.exists())
            {
               dir.mkdirs();
            }else if (dir.isDirectory()) {
                //Clear out the directory
                String[] children = dir.list();
                for (String children1 : children) {
                    new File(dir, children1).delete();
                }
            }
            String name = getUploadedFilesFileName().get(i);
            String targetDir = name.substring(0, name.length() - 4);
            File certDir = new File(path + "/" + targetDir);
            if(!certDir.exists())
            {
                certDir.mkdir();
            }
            try {
                FileUtils.copyFile(getUploadedFiles().get(i), new File(path + "/" + targetDir + "/" + name));
            } catch (IOException ex) {
                Logger.getLogger(MultiFileUpload.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }//End-For
        return true;
    }
    
    public boolean saveChainFiles(String path)
    {
        System.out.println("Preparing to save Chain Files");
        File dir = new File(path);
        
        System.out.println("SAVING CHAIN FILES: ");
        for(String s : getUploadedFilesFileName())
        {
            System.out.println("FILE NAME: " + s);
        }
        for(int i = 0; i < getUploadedFilesFileName().size(); ++i)
        {
            String name = getUploadedFilesFileName().get(i);
            String targetDir = name.substring(1, name.length() - 4);
            System.out.println("Target Dir: " + targetDir);
            
            for(File certDir : dir.listFiles())
            {
                if(targetDir.equalsIgnoreCase(certDir.getName()))
                {
                    File d = new File(path + "/" + targetDir + "/Chain");
                    d.mkdir();
                    try {
                        FileUtils.copyFile(getUploadedFiles().get(i), new File(path + "/" + targetDir + "/Chain/" + name));
                    } catch (IOException ex) {
                        Logger.getLogger(MultiFileUpload.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return true;
    }

    /**
     * @return the uploadedFiles
     */
    public List<File> getUploadedFiles() {
        return uploadedFiles;
    }

    /**
     * @param uploadedFiles the uploadedFiles to set
     */
    public void setUploadedFiles(List<File> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    /**
     * @return the uploadedFilesFileName
     */
    public List<String> getUploadedFilesFileName() {
        return uploadedFilesFileName;
    }

    /**
     * @param uploadedFilesFileName the uploadedFilesFileName to set
     */
    public void setUploadedFilesFileName(List<String> uploadedFilesFileName) {
        this.uploadedFilesFileName = uploadedFilesFileName;
    }

    /**
     * @return the uploadedFilesFilePath
     */
    public List<String> getUploadedFilesFilePath() {
        return uploadedFilesFilePath;
    }

    /**
     * @param uploadedFilesFilePath the uploadedFilesFilePath to set
     */
    public void setUploadedFilesFilePath(List<String> uploadedFilesFilePath) {
        this.uploadedFilesFilePath = uploadedFilesFilePath;
    }

    /**
     * @return the uploadedFilesContentType
     */
    public List<String> getUploadedFilesContentType() {
        return uploadedFilesContentType;
    }

    /**
     * @param uploadedFilesContentType the uploadedFilesContentType to set
     */
    public void setUploadedFilesContentType(List<String> uploadedFilesContentType) {
        this.uploadedFilesContentType = uploadedFilesContentType;
    }

    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        this.servletReq = hsr;
    }
    
}
