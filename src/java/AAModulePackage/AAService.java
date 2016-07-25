package AAModulePackage;

import CAModulePackage.CAService;
import CAModulePackage.CertificateHelper;
import java.io.*;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;

/**
 * The AAService (Attribute Authority Service) handles setting up the server's
 * folder structure, Keys, and Identity Certificates for the Attribute Authority.
 * It's primary purpose is the validating and generating of X.509
 * Attribute Certificates.
 * @author jjbillings
 */
public class AAService {
    
    private final String PRIVATE_KEY_FILENAME = "privateKey.key";
    private final String PUBLIC_KEY_FILENAME = "publicKey.key";
    private final String ROOT_CERTIFICATE_FILENAME = "root_cert.pem";
    private final String ID_CERTIFICATE_FILENAME = "idCert.pem";
    
    private String serverPath, pathToUploadedCerts, pathToUploadedIDCerts,
                   pathToUploadedACs, pathToAA, pathToRootAACert, pathToRootAAKeys,
                   pathToAAKeystore, pathToAACerts, pathToGeneratedCerts,
                   pathToGoodIDCert, pathToGeneratedACs, pathToTrustedIssuerCerts;
    
    private KeyPair aaKeys,rootAAKeys;
    private X509CertificateHolder rootAACert,aaIDCert;
    
    public AAService(String path)
    {
        serverPath = path;
        validateDirectories();
        setupRootKeys();
        setupRootCertificate();
        setupKeys();
        setupCertificates();
        
    }
    
    /**
     * Set up any missing directories.
     */
    private void validateDirectories()
    {
        File f = new File(serverPath);
        
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
        
        pathToTrustedIssuerCerts = pathToAACerts + "/Trusted_AC_Issuer_Certs";
        f = new File(pathToTrustedIssuerCerts);
        if(!f.exists())
        {
            System.out.println("CREATING Trusted AC Issuer FOLDER");
            f.mkdir();
        }
        
        pathToGeneratedCerts = serverPath + "/GeneratedIDCerts";
        f = new File(pathToGeneratedCerts);
        if(!f.exists())
        {
            System.out.println("CREATING GeneratedIDCerts FOLDER");
            f.mkdir();
        }
        
        pathToGoodIDCert = pathToUploadedIDCerts + "/GoodID";
        f = new File(pathToGoodIDCert);
        if(!f.exists())
        {
            System.out.println("CREATING Good ID Cert FOLDER");
            f.mkdir();
        }
        
        pathToGeneratedACs = serverPath + "/GeneratedACs";
        f = new File(pathToGeneratedACs);
        if(!f.exists())
        {
            System.out.println("CREATING GeneratedACs FOLDER");
            f.mkdir();
        }
    }
    
    /**
     * Load this system's Attribute Authority's Public/Private Keys.
     * If no such keys exists, a new pair is generated.
     */
    public void setupKeys()
    {
        //AA Keys
        File aaPub = new File(pathToAAKeystore + "/" + PUBLIC_KEY_FILENAME);
        File aaPriv = new File(pathToAAKeystore + "/" + PRIVATE_KEY_FILENAME);
        if(!aaPub.exists() || !aaPriv.exists())
        {
            System.out.println("HTTPSHELPER: at least one key doesn't exist");
            aaKeys = CertificateHelper.generateKeyPair();
            CertificateHelper.saveKeysToFile(aaKeys, pathToAAKeystore + "/" + PUBLIC_KEY_FILENAME, pathToAAKeystore + "/" + PRIVATE_KEY_FILENAME);
        }else
        {
            System.out.println("HTTPSHELPER: Loading AA keys from file");
            aaKeys = CertificateHelper.loadKeysFromFile(pathToAAKeystore + "/" + PUBLIC_KEY_FILENAME, pathToAAKeystore + "/" + PRIVATE_KEY_FILENAME);
        }
    }
    
    /**
     * Load the Root Attribute Authority's Public/Private Keys.
     * If no such keys exists, a new pair is generated.
     * NOTE: Unless this is the actual Root system, we really shouldn't
     * generate keys for the root. The ConfigAction coupled with the
     * ConfigurationPage.JSP provide functionality for uploading the actual
     * root's credentials.
     */
    public void setupRootKeys()
    {
        //Root AA Keys
        File aaPub = new File(pathToRootAAKeys + "/" + PUBLIC_KEY_FILENAME);
        File aaPriv = new File(pathToRootAAKeys + "/" + PRIVATE_KEY_FILENAME);
        if(!aaPub.exists() || !aaPriv.exists())
        {
            System.out.println("At least one of the Root_AA's keys doesn't exist");
            rootAAKeys = CertificateHelper.generateKeyPair();
            CertificateHelper.saveKeysToFile(rootAAKeys, pathToRootAAKeys + "/" + PUBLIC_KEY_FILENAME, pathToRootAAKeys + "/" + PRIVATE_KEY_FILENAME);
        }else
        {
            System.out.println("Loading Root AA keys from file");
            rootAAKeys = CertificateHelper.loadKeysFromFile(pathToRootAAKeys + "/" + PUBLIC_KEY_FILENAME, pathToRootAAKeys + "/" + PRIVATE_KEY_FILENAME);
        }
    }
    
    /**
     * Loads this system's Attribute Authority's X.509 Identity Certificate.
     * If no such certificate exists, a new one is generated using the root
     * AA's credentials.
     * NOTE: If no cert exists, we really should generate a CSR and send it
     * to the root AA. However, we don't have a standalone root set up
     * so we just use the root's credentials that we have stored on the server.
     */
    public void setupCertificates()
    {
        String certFN = pathToAACerts + "/" + ID_CERTIFICATE_FILENAME;

        File certFile = new File(certFN);
        
        if(!certFile.exists())
        {
            try {
                System.out.println("This system does not have an AA_Cert. Preparing to generate one.");
                aaIDCert = CertificateHelper.generateCertificate(aaKeys.getPublic(),rootAAKeys.getPrivate(),"CN=ROOTAA", "CN=HOSPITAL1AA");
            } catch (OperatorCreationException ex) {
                Logger.getLogger(CAService.class.getName()).log(Level.SEVERE, null, ex);
            }
            CertificateHelper.saveCertToFile(aaIDCert, certFN);
            //Save a copy of the certificate to the trusted issuer directory.
            CertificateHelper.saveCertToFile(aaIDCert, pathToTrustedIssuerCerts + "/" + ID_CERTIFICATE_FILENAME);
            aaIDCert = CertificateHelper.loadCertFromFile(certFN);
        }else
        {
            aaIDCert = CertificateHelper.loadCertFromFile(certFN);
            System.out.println("Loaded this Hospital's AA Cert. Subject: " + aaIDCert.getSubject().toString());
        }
    }
    
    /**
     * Loads the Root Attribute Authority's X.509 Identity Certificate.
     * If no such certificate exists on the server, one is generated.
     * * NOTE: Unless this is the actual Root system, we really shouldn't
     * generate a certificate for the root. The ConfigAction coupled with the
     * ConfigurationPage.JSP provide functionality for uploading the actual
     * root's credentials.
     */
    public void setupRootCertificate(){
        //String certFN = pathToCerts + "/" + ROOT_CERTIFICATE_FILENAME;
        String certFN = pathToRootAACert + "/" + ROOT_CERTIFICATE_FILENAME;

        File rootCertFile = new File(certFN);
        
        if(!rootCertFile.exists())
        {
            try {
                System.out.println("No Root_AA Certificate was found. Preparing to generate a new one...");
                rootAACert = CertificateHelper.generateCertificate(rootAAKeys.getPublic(),rootAAKeys.getPrivate(),"CN=ROOTAA","CN=ROOTAA");
            } catch (OperatorCreationException ex) {
                Logger.getLogger(CAService.class.getName()).log(Level.SEVERE, null, ex);
            }
            CertificateHelper.saveCertToFile(rootAACert, certFN);
            rootAACert = CertificateHelper.loadCertFromFile(certFN);
        }else
        {
            rootAACert = CertificateHelper.loadCertFromFile(certFN);
            System.out.println("Loaded this Hospital's Root_AA Cert. Subject: " + rootAACert.getSubject().toString());

        }
    }
    
    /**
     * This method checks the validity of the submitted Attribute Certificates
     * based on the standard defined in the RFC5755 Section 5.
     * It is quite a beast of a method...
     * @return True if there is at least one valid AC, False if there are none.
     */
    public boolean verifyACs()
    {
        ArrayList<X509CertificateHolder> clientIDCerts = new ArrayList<X509CertificateHolder>();
        ArrayList<X509AttributeCertificateHolder> clientACs = new ArrayList<X509AttributeCertificateHolder>();
        HashSet<X509CertificateHolder> trustedIssuerCerts = loadTrustedIssuerCerts();
        
        File idCertDir = new File(pathToUploadedIDCerts);
        File acDir = new File(pathToUploadedACs);
        
        System.out.println("Preparing to load client ID_Certs for AC Validation.");
        
        //idCertDir will contain folders containing id_certs/their chains
        for(File dir : idCertDir.listFiles())
        {
            if(dir.getName().equalsIgnoreCase(".DS_Store"))
            {
                    continue;
            }
            //dir will contain an id_cert and a folder with it's chain.
            for(File certFile : dir.listFiles())
            {
                
                if(certFile.getName().equalsIgnoreCase(".DS_Store") || certFile.isDirectory())
                {
                    continue;
                }
                
                X509CertificateHolder tempCert = CertificateHelper.loadCertFromFile(certFile);
                System.out.println("Loading client ID_Cert. Client ID_Cert Subject: " + tempCert.getSubject().toString());
                System.out.println("Loading client ID_Cert. Client ID_Cert Issuer:  " + tempCert.getIssuer().toString());
                clientIDCerts.add(tempCert);

            }
        }
        
        
        System.out.println("Preparing to load Client's ACs for AC validation.");
        AC:
        for(File dir : acDir.listFiles())
        {
            //Skip the .DS_Store
            if(dir.getName().equalsIgnoreCase(".DS_Store"))
            {
                continue;
            }
             
            for(File f : dir.listFiles())
            {
                //Again, skip .DS_Store and also skip the Chain folder
                if(f.getName().equalsIgnoreCase(".DS_Store") || f.isDirectory())
                {
                    continue;
                }
                
                X509AttributeCertificateHolder tempAC = ACHelper.loadAttributeCertFromFile(f);
                System.out.println("Loading client AC. Client AC Holder Issuer: " + Arrays.toString(tempAC.getHolder().getIssuer()));
                System.out.println("Loading client AC. Client AC Issuer: " + Arrays.toString(tempAC.getIssuer().getNames()));
                //Check to see if the user owns this AC.
                //cert refers to the ID_Cert associated with this AC, NOT the AC's issuer.
                boolean owned = false;
                for(X509CertificateHolder cert : clientIDCerts)
                {
                    if(ACHelper.checkAssociatedCertificate(tempAC, cert))
                    {
                        System.out.println("AC: " + tempAC.getHolder().toString() + " is owned by: " + cert.getSubject().toString());
                        owned = true;
                        break;
                    }
                    
                }
                //If the user doesn't "own" it with one of their ID_Certs
                if(!owned)
                {
                    System.out.println("User doesn't own this AC");
                    continue AC;
                }
                
                //Check to see if the AC Issuer's chain is valid.
                ArrayList<X509CertificateHolder> chain = getUploadedChain(ACHelper.extractAttributes(tempAC));
                    
                Set<X509CertificateHolder> trustAnchors = new HashSet();
                trustAnchors.add(rootAACert);

                if(!CertificateHelper.validateCertificatePath(trustAnchors, chain))
                {
                    System.out.println("Invalid Certificate Chain for AC Issuer");
                    continue AC;
                }
                
                //Check AC's Validity Period.
                if(!tempAC.isValidOn(new Date()))
                {
                    System.out.println("AC is no longer valid");
                    continue AC;
                }

                //Check to see if the AC's signature is cryptographically correct
                //and in doing so, check that the AC issuer is directly trusted.
                if(!ACHelper.validateACSignature(tempAC, trustedIssuerCerts))
                {
                    System.out.println("AC Signature coul not be validated or the issuer is untrusted.");
                    continue AC;
                }
                
                //Check to see if the AC contains unsupported extensions.
                if(tempAC.hasExtensions())
                {
                    System.out.println("AC Contains extensions... We do not currently support extensions");
                    continue AC;
                }
                
                System.out.println("AC successfully validated.");
                clientACs.add(tempAC);
                
            }
            
        }
        
        if(clientACs.size() == 0)
        {
            System.out.println("All ACs are unusable");
            return false;
        }
        return true;
    }
    
    /**
     * Generate a default Attribute Certificate.
     */
    public void generateAC()
    {
        //ownerCert is the user's ID_Cert for this system.
        X509CertificateHolder ownerCert = loadOwnerCert();
        
        //temp values
        String role = "psychiatrist";
        String rid = "LM54R5T7P2";
        String[] recTypes = {"medication_history","psychiatric_notes"};
        
        X509AttributeCertificateHolder newAC = ACHelper.generateAttributeCertificate(aaIDCert, ownerCert, aaKeys.getPrivate(), role, rid, recTypes);
        //X509AttributeCertificateHolder newAC = ACHelper.generateAttributeCertificate(aaIDCert, ownerCert, aaKeys.getPrivate());
        AttributeCertificateWrapper wrappedAC = ACHelper.extractAttributes(newAC);
        
        //Save AC to the generated AC directory
        //File acDir = new File(pathToGeneratedACs + "/" + wrappedAC.getRecordId());
        //acDir.mkdir();
        ACHelper.saveAttributeCertToFile(newAC, pathToGeneratedACs + "/"  + wrappedAC.getRecordId() + ".pem");
        
        //Save AC issuer chain with the corresponding AC.
        //Currently we only have one certificate in the chain.
        File chainDir = new File(pathToGeneratedACs  + "/Chain");
        chainDir.mkdir();
        CertificateHelper.saveCertToFile(aaIDCert, pathToGeneratedACs + "/Chain/0" + wrappedAC.getRecordId() + ".pem");
        
    }
    
    /**
     * Generates an X.509 Attribute Certificate based on the fields in the
     * acFile. Use ConfigurationPage.JSP to specify the attributes that are to
     * be contained in the generated AC.
     * @param path - path to the server.
     */
    public void generateAC(String path)
    {
        File acFile = new File(path + "/ACFields/ac.txt");
        if(!acFile.exists())
        {
            generateAC();
            return;
        }
        
        //ownerCert is the user's ID_Cert for this system.
        X509CertificateHolder ownerCert = loadOwnerCert();
        
        List<String> fields = loadACFields(acFile);
        
        String role = fields.get(0);
        String rid = fields.get(1);
        
        String[] recTypes = new String[fields.size()-2];
        for(int i = 2; i < fields.size(); ++i)
        {
            recTypes[i-2] = fields.get(i);
        }
        
        X509AttributeCertificateHolder newAC = ACHelper.generateAttributeCertificate(aaIDCert, ownerCert, aaKeys.getPrivate(), role, rid, recTypes);
        AttributeCertificateWrapper wrappedAC = ACHelper.extractAttributes(newAC);
        
        //Save AC to the generated AC directory
        ACHelper.saveAttributeCertToFile(newAC, pathToGeneratedACs + "/"  + wrappedAC.getRecordId() + ".pem");
        
        //Save AC issuer chain with the corresponding AC.
        //Currently we only have one certificate in the chain.
        File chainDir = new File(pathToGeneratedACs  + "/Chain");
        chainDir.mkdir();
        CertificateHelper.saveCertToFile(aaIDCert, pathToGeneratedACs + "/Chain/0" + wrappedAC.getRecordId() + ".pem");
        
    }
    
    /**
     * Retrieves the client's uploaded Certificate Chain.
     * TODO: Consider using a Set of ACs instead of ArrayList. 
     * @param ac - X.509 Attribute Certificate whose chain we want to load.
     * @return - ArrayList containing the certificates in the chain.
     */
    public ArrayList<X509CertificateHolder> getUploadedChain(AttributeCertificateWrapper ac)
    {
        ArrayList<X509CertificateHolder> chain = new ArrayList();
        //Chain certificates are stored in this directory.
        File chainDir = new File(pathToUploadedACs + "/" + ac.getRecordId() + "/Chain");
        for(File chainFile : chainDir.listFiles())
        {
            chain.add(CertificateHelper.loadCertFromFile(chainFile));
        }
        return chain;
    }
    
    /**
     * Loads the X.509 Identity Certificate that authenticates the user to
     * THIS SYSTEM. 
     * @return - User's X.509 Identity Certificate for this system.
     */
    public X509CertificateHolder loadOwnerCert()
    {
        X509CertificateHolder ownerCert = null;
        
        //Since the ownerCert wasn't just generated, it's in the GoodIDCert dir
        File ownerCertDir = new File(pathToGoodIDCert);
        System.out.println("Preparing to load the owner ID_Cert to be used for AC Generation.");
        for(File f : ownerCertDir.listFiles())
        {
            if(!f.getName().equalsIgnoreCase(".DS_Store"))
            {
                ownerCert = CertificateHelper.loadCertFromFile(f);
                System.out.println("---ID-CERT---");
                System.out.println("Owner's ID_Cert Subject: " + ownerCert.getSubject().toString());
                System.out.println("Owner's ID_Cert Issuer: " + ownerCert.getIssuer().toString());

                break;
            }
        }
        return ownerCert;
    }
    
    /**
     * Load the certificates of the entities that are directly trusted
     * to issue Attribute Certificates
     * @return Set of certificates that are trusted to issue ACs
     */
    public HashSet<X509CertificateHolder> loadTrustedIssuerCerts()
    {
        HashSet<X509CertificateHolder> trustedCerts = new HashSet();
        
        File trustedDir = new File(pathToTrustedIssuerCerts);
        
        for(File cert : trustedDir.listFiles())
        {
            if(cert.getName().equalsIgnoreCase(".DS_Store"))
            {
                continue;
            }
            trustedCerts.add(CertificateHelper.loadCertFromFile(cert));
        }
        
        return trustedCerts;
    }
    
    public List<String> loadACFields(File acFile)
    {
        
        ArrayList<String> fields = new ArrayList();
        Scanner fReader = null;
        try {
            fReader = new Scanner(acFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AAService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while(fReader.hasNextLine())
        {
            fields.add(fReader.nextLine());
        }
        List<String> tempTypes = Arrays.asList(fields.get(0).split("\\s*,\\s*"));
        acFile.delete();
        return tempTypes;
        
    }
    
    /**
     * Resets this system's Attribute Authority's credentials.
     * Generates a new X.509 Identity Certificate using the specified CN.
     * @param newAACN - New Common Name for the Attribute Authority.
     */
    public void resetAACert(String newAACN)
    {
        String certFN = pathToAACerts + "/" + ID_CERTIFICATE_FILENAME;

        File certFile = new File(certFN);
        
        if(certFile.exists())
        {
            certFile.delete();
        }
        
        try {
            System.out.println("ABOUT TO RESET AA ID CERT");
            aaIDCert = CertificateHelper.generateCertificate(aaKeys.getPublic(),rootAAKeys.getPrivate(),"CN=ROOTAA", newAACN);
        } catch (OperatorCreationException ex) {
            Logger.getLogger(CAService.class.getName()).log(Level.SEVERE, null, ex);
        }
        CertificateHelper.saveCertToFile(aaIDCert, certFN);
        //Save a copy of the certificate to the trusted issuer directory.
        CertificateHelper.saveCertToFile(aaIDCert, pathToTrustedIssuerCerts + "/" + ID_CERTIFICATE_FILENAME);
        aaIDCert = CertificateHelper.loadCertFromFile(certFN);
        
        
    }
    
    /**
     * Examines information contained in the AA's certificates.
     * This information is made available to the ConfigPage.
     * @return - String representation of the certificates relevant to the AA.
     */
    public String checkCerts()
    {
        StringBuilder strb = new StringBuilder();
        strb.append("--------AA Certificates--------\n");
        strb.append("Root AA Subject: " + rootAACert.getSubject().toString() + "\n");
        strb.append("Root AA Issuer:  " + rootAACert.getIssuer().toString() + "\n");
        strb.append("This system's AA Subject: " + aaIDCert.getSubject().toString() + "\n");
        strb.append("This system's AA Issuer:  " + aaIDCert.getIssuer().toString() + "\n");
        
        strb.append("--------Trusted AC Issuers--------\n");
        File trusted = new File(pathToTrustedIssuerCerts);
        for(File f : trusted.listFiles())
        {
            if(!f.isDirectory() && !f.getName().equalsIgnoreCase(".DS_Store"))
            {
                X509CertificateHolder tempCert = CertificateHelper.loadCertFromFile(f);
                strb.append("Trusted Issuer's Subject: " + tempCert.getSubject().toString() + "\n");
                strb.append("Trusted Issuer's Issuer:  " + tempCert.getIssuer().toString() + "\n");
            }
        }
        
        return strb.toString();
    }
}
