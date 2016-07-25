/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CAModulePackage;

import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;


/**
 * The CAService (Certificate Authority Service) handles setting up the server's
 * folder structure, Keys, and Identity Certificates. It also has functionality
 * for validating certificates and their chains, as well as examining the
 * information contained in a certificate.
 * @author jjbillings
 */
public class CAService {
       
    private final String PRIVATE_KEY_FILENAME = "privateKey.key";
    private final String PUBLIC_KEY_FILENAME = "publicKey.key";
    private final String ROOT_CERTIFICATE_FILENAME = "root_cert.pem";
    private final String ID_CERTIFICATE_FILENAME = "idCert.pem";
    
    private String pathToKeystore, serverPath, pathToCerts, pathToRootCert,
                pathToRootKeys, pathToGeneratedCerts, pathToGeneratedACs,
                pathToUploadedCSR, pathToUploadedIDCerts, pathToUploadedCerts,
                pathToUploadedACs, pathToAA, pathToRootAACert, pathToRootAAKeys,
                pathToAAKeystore, pathToAACerts, pathToGoodIDCert, pathToEMRDownload;
    private KeyPairGenerator keyGen;
    private KeyPair keys,rootKeys;
    private X509CertificateHolder rootCert,idCert;
    
    public CAService(String path)
    {
        System.out.println("Server Path: " + path);
        serverPath = path;
    }
    
    /**
     * This method ensures that all files/folders the server needs are ready
     * to use.
     */
    public void execute()
    {
        validateDirectories();
        setupRootKeys();
        setupRootCertificate();
        setupKeys();
        setupIDCertificate();
    }
    
    /**
     * Goes through and creates any directories that are missing.
     */
    private void validateDirectories()
    {
        File f = new File(serverPath + "/Security");
        if(!f.exists())
        {
            System.out.println("CREATING SECURITY FOLDER");
            f.mkdir();
        }
        
        pathToKeystore = serverPath + "/Security/Keystore";
        f = new File(pathToKeystore);
        if(!f.exists())
        {
            System.out.println("CREATING KEYSTORE FOLDER");
            f.mkdir();
        }
        
        pathToCerts = serverPath + "/Security/Certificates";
        f = new File(pathToCerts);
        if(!f.exists())
        {
            System.out.println("CREATING CERTIFICATE FOLDER");
            f.mkdir();
        }
        
        pathToRootCert = pathToCerts + "/Root_CA";
        f = new File(pathToRootCert);
        if(!f.exists())
        {
            System.out.println("CREATING ROOT_CA_CERT FOLDER");
            f.mkdir();
        }
        
        pathToRootKeys = pathToKeystore + "/Root_CA";
        f = new File(pathToRootKeys);
        if(!f.exists())
        {
            System.out.println("CREATING ROOT_CA_KEYSTORE FOLDER");
            f.mkdir();
        }
        
        pathToGeneratedCerts = serverPath + "/GeneratedIDCerts";
        f = new File(pathToGeneratedCerts);
        if(!f.exists())
        {
            System.out.println("CREATING GeneratedIDCerts FOLDER");
            f.mkdir();
        }
        
        pathToGeneratedACs = serverPath + "/GeneratedACs";
        f = new File(pathToGeneratedACs);
        if(!f.exists())
        {
            System.out.println("CREATING GeneratedACs FOLDER");
            f.mkdir();
        }
        
        pathToUploadedCSR = serverPath + "/UploadedCSR";
        f = new File(pathToUploadedCSR);
        if(!f.exists())
        {
            System.out.println("CREATING UploadedCSR FOLDER");
            f.mkdir();
        }
        
        pathToUploadedCerts = serverPath + "/UploadedCerts";
        f = new File(pathToUploadedCerts);
        if(!f.exists())
        {
            System.out.println("CREATING UploadedCerts FOLDER");
            f.mkdir();
        }
        
        pathToUploadedIDCerts = pathToUploadedCerts + "/IDCerts";
        f = new File(pathToUploadedIDCerts);
        if(!f.exists())
        {
            System.out.println("CREATING UploadedIDCerts FOLDER");
            f.mkdir();
        }
        
        pathToUploadedACs = pathToUploadedCerts + "/ACs";
        f = new File(pathToUploadedACs);
        if(!f.exists())
        {
            System.out.println("CREATING uploadedACs FOLDER");
            f.mkdir();
        }
        
        pathToAA = serverPath + "/AA";
        f = new File(pathToAA);
        if(!f.exists())
        {
            System.out.println("CREATING AA FOLDER");
            f.mkdir();
        }
        
        pathToAAKeystore = pathToAA + "/Keystore";
        f = new File(pathToAAKeystore);
        if(!f.exists())
        {
            System.out.println("CREATING AA Keystore FOLDER");
            f.mkdir();
        }
        
        pathToRootAAKeys = pathToAAKeystore + "/Root_AA";
        f = new File(pathToRootAAKeys);
        if(!f.exists())
        {
            System.out.println("CREATING Root AA Keys FOLDER");
            f.mkdir();
        }
        
        pathToAACerts = pathToAA + "/Certs";
        f = new File(pathToAACerts);
        if(!f.exists())
        {
            System.out.println("CREATING AA Certs FOLDER");
            f.mkdir();
        }
        
        pathToRootAACert = pathToAACerts + "/Root_AA";
        f = new File(pathToRootAACert);
        if(!f.exists())
        {
            System.out.println("CREATING Root AA Cert FOLDER");
            f.mkdir();
        }
        
        pathToGoodIDCert = pathToUploadedIDCerts + "/GoodID";
        f = new File(pathToGoodIDCert);
        if(!f.exists())
        {
            System.out.println("CREATING Good ID Cert FOLDER");
            f.mkdir();
        }
        
        pathToEMRDownload = serverPath + "/EMRDownload";
        f = new File(pathToEMRDownload);
        if(!f.exists())
        {
            System.out.println("CREATING EMRDownload FOLDER");
            f.mkdir();
        }
    }
    
    /**
     * This method checks to see if the Root_CA's public/private keys exists.
     * If they do exist, they are loaded, if not, they are generated.
     */
    private void setupRootKeys()
    {
        String pubFN = pathToRootKeys + "/" + PUBLIC_KEY_FILENAME;
        String privFN = pathToRootKeys + "/" + PRIVATE_KEY_FILENAME;
        File pub = new File(pubFN);
        File priv = new File(privFN);
        
        if(!pub.exists() || !priv.exists())
        {
            System.out.println("At least one root key doesn't exist");
            rootKeys = CertificateHelper.generateKeyPair();
            CertificateHelper.saveKeysToFile(rootKeys, pubFN, privFN);
        }else
        {
            System.out.println("Loading root keys from file");
            rootKeys = CertificateHelper.loadKeysFromFile(pubFN, privFN);
        }
    }
    
    /**
     * This method checks to see if this system's CA's public/private keys exists.
     * If they do exist, they are loaded, if not, they are generated.
     */
    private void setupKeys()
    {
        String pubFN = pathToKeystore + "/" + PUBLIC_KEY_FILENAME;
        String privFN = pathToKeystore + "/" + PRIVATE_KEY_FILENAME;
        File pub = new File(pubFN);
        File priv = new File(privFN);
        
        if(!pub.exists() || !priv.exists())
        {
            System.out.println("At least one of this CA's keys doesn't exist");
            keys = CertificateHelper.generateKeyPair();
            CertificateHelper.saveKeysToFile(keys, pubFN, privFN);
        }else
        {
            System.out.println("Loading CA's keys from file");
            keys = CertificateHelper.loadKeysFromFile(pubFN, privFN);
        }
    }
    
    /**
     * This method checks to see if the Root_CA's X.509 Certificate exists.
     * If it does, then that cert is loaded. If it DNE then a new one is
     * generated and self-signed.
     */
    private void setupRootCertificate()
    {
        //String certFN = pathToCerts + "/" + ROOT_CERTIFICATE_FILENAME;
        String certFN = pathToRootCert + "/" + ROOT_CERTIFICATE_FILENAME;

        File rootCertFile = new File(certFN);
        
        //If the root cert doesn't exist, generate a self-signed root cert.
        if(!rootCertFile.exists())
        {
            try {
                System.out.println("ABOUT TO GENERATE ROOT CERT");
                rootCert = CertificateHelper.generateCertificate(rootKeys.getPublic(), rootKeys.getPrivate(), "CN=ROOT", "CN=ROOT");
            } catch (OperatorCreationException ex) {
                Logger.getLogger(CAService.class.getName()).log(Level.SEVERE, null, ex);
            }
            CertificateHelper.saveCertToFile(rootCert, certFN);
            rootCert = CertificateHelper.loadCertFromFile(certFN);
        }else
        {
            rootCert = CertificateHelper.loadCertFromFile(certFN);
        }
    }

    /**
     * Checks to see if this system's CA's X.509 Certificate exists.
     * If it does, the certificate is loaded. If not, a new one is generated.
     */
    private void setupIDCertificate()
    {
        //String certFN = pathToCerts + "/" + ROOT_CERTIFICATE_FILENAME;
        String certFN = pathToCerts + "/" + ID_CERTIFICATE_FILENAME;

        File certFile = new File(certFN);
        
        if(!certFile.exists())
        {
            try {
                System.out.println("ABOUT TO GENERATE ID CERT");
                //We use a default value for the hospital's CN.
                idCert = CertificateHelper.generateCertificate(keys.getPublic(),rootKeys.getPrivate(),"CN=ROOT", "CN=HOSPITAL1CA");
            } catch (OperatorCreationException ex) {
                Logger.getLogger(CAService.class.getName()).log(Level.SEVERE, null, ex);
            }
            CertificateHelper.saveCertToFile(idCert, certFN);
            idCert = CertificateHelper.loadCertFromFile(certFN);
        }else
        {
            idCert = CertificateHelper.loadCertFromFile(certFN);
            System.out.println("Loaded this system's CA Certificate.");
            System.out.println("This System's CA Subject: " + idCert.getSubject().toString());
        }
        
    }
    
     /**
     * This method deletes the certificates left over from the previous
     * connection from a client.
     */
    public void clearPreviousCerts()
    {
        File prevCerts = new File(pathToUploadedCerts);
        for(File f : prevCerts.listFiles())
        {
            if(f.isDirectory())
            {
                try {
                    FileUtils.deleteDirectory(f);
                } catch (IOException ex) {
                    Logger.getLogger(CAService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public boolean validateIDCerts()
    {
        File dir = new File(pathToUploadedIDCerts);
        ArrayList<X509CertificateHolder> idCerts = new ArrayList<X509CertificateHolder>();
        ArrayList<X509CertificateHolder> chainCerts = null;
        Set<X509CertificateHolder> taCerts = new HashSet();
        taCerts.add(rootCert);
        
        //dir's files should all be directories containing certificates/their chains
        ID:
        for(File d : dir.listFiles())
        {
            System.out.println("d: " + d.getName());
            if(d.getName().equalsIgnoreCase(".DS_Store"))
            {
                continue;
            }
            
            
            //d's files should be either an ID_Cert or that Cert's Chain folder
            for(File idFile : d.listFiles())
            {
                
                //if its the ds_store or a folder (chain certs folder)
                if(idFile.getName().equals(".DS_Store") || idFile.isDirectory())
                {
                    continue;
                }
                
                //--------- Certificate Chain Validation -----------
                chainCerts = new ArrayList();
                
                //temp store the idCert so we can add it to the validated list if its all good.
                X509CertificateHolder clientIDCert = CertificateHelper.loadCertFromFile(idFile);
                chainCerts.add(clientIDCert);
                System.out.println("Preparing to validate " + clientIDCert.getSubject().toString() + "'s ID_Cert");
                
                //Aquire the certificate chain for this particular ID_Cert
                File chainDir = new File(d.getPath() + "/Chain");
                for(File c : chainDir.listFiles())
                {
                    if(c.getName().equalsIgnoreCase(".DS_Store"))
                    {
                        continue;
                    }                    
                    chainCerts.add(CertificateHelper.loadCertFromFile(c));
                }
                
                System.out.println("Certificate Chain for " + clientIDCert.getSubject().toString() + " successfully loaded");
                if(!CertificateHelper.validateCertificatePath(taCerts, chainCerts))
                {
                    System.out.println("Certificate Chain for " + clientIDCert.getSubject().toString() + " couldn't be validated");
                    d.delete(); //delete the whole directory.
                    continue ID;
                    
                }
                
                System.out.println("Certificate Chain for " + clientIDCert.getSubject().toString() + " successfully validated");
                
                /*
                This won't work for certificates generated by CA's that aren't THIS Hospital.
                I believe validating the certificate path is enough. I'm still looking in the source
                code for it to see if it checks the necessary things (date, signature, etc.)
                //ValidateCertificate checks the validity period and signature
                if(!CertificateHelper.validateCertificate(clientIDCert, idCert))
                {
                    d.delete();
                    continue ID;
                }
                */
                //If it passes the validation.
                idCerts.add(clientIDCert);
            }
            
        }
        
        //If at least one of the ID_Certs was validated, we may proceed.
        if(idCerts.size() > 0)
        {
            return true;
        }
        
        return false;
    }

    /**
     * This method checks the upload directory for a Certificate Signing
     * Request. If one exists, we use it to generate a new X.509 Identity
     * Certificate.
     */
    public void signCSR()
    {
        File dir = new File(pathToUploadedCSR);
        PKCS10CertificationRequest req = null;
                
        System.out.println("Preparing to sign CSR.");
        for(File f : dir.listFiles())
        {
            if(!f.getName().equalsIgnoreCase(".DS_Store"))
            {
                System.out.println("CSR Will be signed with this system's CA_Cert, whose subject is: " + idCert.getSubject().toString());
                
                req = CertificateHelper.loadCSRFromFile(f);
                X509CertificateHolder newCert = CertificateHelper.signCSR(req, idCert.getSubject().toString(), keys.getPrivate());
                System.out.println("The CSR has been signed.\nSubject: " + newCert.getSubject().toString() + "\nIssuer:  " + newCert.getIssuer().toString());
                CertificateHelper.saveCertToFile(newCert, pathToGeneratedCerts + "/" + newCert.getIssuer().toString() + ".pem");
                
                //Save a copy to the GoodIDCert directory.
                CertificateHelper.saveCertToFile(newCert, pathToGoodIDCert + "/" + newCert.getIssuer().toString() + ".pem");
                
                File chainDir = new File(pathToGeneratedCerts + "/Chain");
                if(!chainDir.exists())
                {
                    chainDir.mkdir();
                }
                CertificateHelper.saveCertToFile(idCert, pathToGeneratedCerts + "/Chain/" + "0" + newCert.getIssuer().toString() + ".pem");
            }
        }
    }
    
    /**
     * This method is called if we ended up generating a new ID_Cert.
     * We save a copy this CA's ID_Cert to the chain directory, so the client
     * may download it.
     */
    public void prepareChainForDownload()
    {
        File dir = new File(pathToGeneratedCerts + "/Chain");
        if(!dir.exists())
        {
            dir.mkdir();
        }else
        {
            //clear out old files from the Chain dir.
            for(File child : dir.listFiles())
            {
                child.delete();
            }
        }
        
        //Currently the only certificate in the chain would be this hospital's certificate
        //and the root cert which is not transfered to the client.
        CertificateHelper.saveCertToFile(idCert, pathToGeneratedCerts + "/Chain/0" + idCert.getSubject().toString() + ".pem");
    }
    
    /**
     * This method we want to check and see if the user possesses an id_cert
     * specifically for THIS hospital, if not we need to generate a new id_Cert
     * for them.
     * @return True if they have one, false otherwise
     */
    public boolean checkForGoodID()
    {
        
        File certDir = new File(pathToUploadedIDCerts + "/" + idCert.getSubject().toString());
        if(certDir.exists())
        {
            String sourceName = pathToUploadedIDCerts + "/" + idCert.getSubject().toString() + "/" + idCert.getSubject().toString() + ".pem";
            String destName = pathToGoodIDCert + "/" + idCert.getSubject().toString() + ".pem";
            
            try {
                FileUtils.copyFile(new File(sourceName), new File(destName));
            } catch (IOException ex) {
                System.out.println("Couldn't copy GoodIDCert");
                Logger.getLogger(CAService.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
        return false;
    }
    
    /**
     * This method resets the Certificate Authority, by generating a new
     * X.509 Identity Certificate with the specified CN.
     * @param newCACN - Common Name for the Certificate Authority's certificate.
     */
    public void resetCACert(String newCACN)
    {
        String certFN = pathToCerts + "/" + ID_CERTIFICATE_FILENAME;

        File certFile = new File(certFN);
        
        if(certFile.exists())
        {
            certFile.delete();
        }
        
        try {
            idCert = CertificateHelper.generateCertificate(keys.getPublic(),rootKeys.getPrivate(),"CN=ROOT", newCACN);
        } catch (OperatorCreationException ex) {
            Logger.getLogger(CAService.class.getName()).log(Level.SEVERE, null, ex);
        }
        CertificateHelper.saveCertToFile(idCert, certFN);
        idCert = CertificateHelper.loadCertFromFile(certFN);
        
    }
    
    /**
     * 
     * @return - Information about this CA's Certificate and the Root Certificate.
     */
    public String checkCerts()
    {
       
        StringBuilder strb = new StringBuilder();
        strb.append("--------CA Certificates--------\n");
        strb.append("RootCA Subject: ");
        strb.append(rootCert.getSubject().toString());
        strb.append("\n");
        strb.append("RootCA Issuer:  ");
        strb.append(rootCert.getIssuer().toString());
        strb.append("\n");
        strb.append("CA Subject: ");
        strb.append(idCert.getSubject().toString());
        strb.append("\n");
        strb.append("CA Issuer:  ");
        strb.append(idCert.getIssuer().toString());
        strb.append("\n");
        
        String s = strb.toString();
        return s;
    }
        
}
