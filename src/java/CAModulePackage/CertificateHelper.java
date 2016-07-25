/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CAModulePackage;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStoreBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.util.io.pem.PemReader;

/**
 *
 * The CertificateHelper class provides static methods that are used for various
 * tasks related to public/private keys, X.509 Certificates, etc.
 * @author jjbillings
 */
public class CertificateHelper {
    
    //Adds the BouncyCastle security provider.
    static {
        Security.addProvider(new BouncyCastleProvider());
    }    
    
    /**
     * Generates Public/Private Keypair
     * @return - New Public/Private Keypair.
     */
    public static KeyPair generateKeyPair()
    {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }
    
    /**
     * Saves a set of keys to the specified files.
     * @param keys - Public/Private KeyPair
     * @param publicFileName - Full Path/Filename for the Public Key
     * @param privateFileName - Full Path/Filename for the Private Key 
     */
    public static void saveKeysToFile(KeyPair keys, String publicFileName, String privateFileName)
    {
        byte[] privBytes = keys.getPrivate().getEncoded();
        byte[] pubBytes = keys.getPublic().getEncoded();

        //Our keypair has been generated, now we write them to files in the keystore.
        FileOutputStream fos = null;
        //TODO: Why to we use the X509EncodedKeySpec?? Why not just write the bytes?
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keys.getPublic().getEncoded());

        try {
            fos = new FileOutputStream(new File(publicFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Write the Public Key to file.
        try {
            fos.write(publicKeySpec.getEncoded());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privBytes);
        try
        {
            fos = new FileOutputStream(new File(privateFileName));
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        try
        {
            fos.write(privateKeySpec.getEncoded());
            fos.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Load a Public/Private KeyPair from their specified files.
     * We could definitely split this into two separate methods, one for 
     * loading public key, another for loading private key.
     * @param publicFileName - Full path and filename for the Public Key
     * @param privateFileName - Full path and filename for the Private Key
     * @return - Keypair with the loaded Public/Private Keys.
     */
    public static KeyPair loadKeysFromFile(String publicFileName, String privateFileName) {

        // ---------------- Making/Init KeyFact -----------------------------
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        
        //-------------- Getting Keys from file -----------------------------

        // Loading private Key from file
        InputStream fis = null;
        File privateKeyFile = new File(privateFileName);
        
        try {
            fis = new FileInputStream(privateKeyFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        DataInputStream dis = new DataInputStream(fis);

        byte[] keyBytes = new byte[(int) privateKeyFile.length()];

        try {
            dis.readFully(keyBytes);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            dis.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        PrivateKey priv = null;
        try {
            priv = kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            //PublicKey pubKey = kf.generatePublic(spec);
        } catch (InvalidKeySpecException e1) {
            e1.printStackTrace();
        }

        // Loading public Key from file
        File publicFile = new File(publicFileName);
        try {
            fis = new FileInputStream(publicFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        dis = new DataInputStream(fis);

        keyBytes = new byte[(int) publicFile.length()];


        try {
            dis.readFully(keyBytes);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            dis.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        PublicKey pub = null;
        try {
            pub = kf.generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (InvalidKeySpecException e1) {
            e1.printStackTrace();
        }
        return new KeyPair(pub,priv);
    }
    
    /**
     * Load a Certificate Signing Request from the specified File.
     * Note: CSR will be a .PEM File.
     * @param csrFile
     * @return 
     */
    public static PKCS10CertificationRequest loadCSRFromFile(File csrFile)
    {
        PemReader reader = null;
        PKCS10CertificationRequest req = null;
        try {
            reader = new PemReader(new FileReader(csrFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            req = new PKCS10CertificationRequest(reader.readPemObject().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return req;
    }

    
    /**
     * This method generates a new X.509 Identity Certificate.
     * This should only really be used for generating a new certificate
     * for a part of this system (CA's Cert/AA's Cert). For a client, we would
     * have them generate and send over a Certificate Signing Request.
     * @param subjectKey - The soon-to-be-holder's Public Key
     * @param issuerKey - The singing entity's Private Key
     * @param issuer - Common Name of the signing entity
     * @param subject - Common Name of the subject (soon-to-be-holder)
     * @return - New X.509 Identity Certificate.
     * @throws OperatorCreationException 
     */
    public static X509CertificateHolder generateCertificate(PublicKey subjectKey, PrivateKey issuerKey, String issuer, String subject) throws OperatorCreationException {

        //So I am unable to verify that the certificate is valid on my Mac, but the one's
        //generated by Amanda's app are also "untrusted" through terminal ssl...
        Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

        //Chose to use the JcaBuilder because they use the public key not the PublicKeyInfo...
        //Although, looking at the specs, it doesn't look like the default publickeyinfo is too bad to make...
        //TODO: Consider switching to the normal certBuidler.
        JcaX509v3CertificateBuilder b = new JcaX509v3CertificateBuilder(
                new X500Name(issuer),
                BigInteger.valueOf(System.currentTimeMillis()),
                startDate,
                endDate,
                new X500Name(subject), //I believe this field is incorrect TODO:Revise.
                subjectKey);

        X509CertificateHolder cert = b.build(new JcaContentSignerBuilder("SHA256withRSAEncryption").setProvider("BC").build(issuerKey));
        return cert;
    }
    
    /**
     * Generate a new X.509 Certificate based on the input Certificate Signing
     * Request.
     * This is the primary method that should be used for granting a user 
     * credentials on this system.
     * @param csr - Input Certificate Signing Request
     * @param issuer - Name of the Issuing Entity
     * @param issuerPriv - Private Key of the Issuing Entity.
     * @return X.509 Identity Certificate authenticating the user to this system
     */
    public static X509CertificateHolder signCSR(PKCS10CertificationRequest csr, String issuer, PrivateKey issuerPriv)
    {
        Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

        PublicKey pub = null;
        try {
            pub = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(csr.getSubjectPublicKeyInfo().getEncoded()));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                new X500Name(issuer),
                BigInteger.valueOf(System.currentTimeMillis()),
                startDate,
                endDate,
                csr.getSubject(),
                pub);

        X509CertificateHolder newCert = null;
        try {
            newCert = builder.build(new JcaContentSignerBuilder("SHA256withRSAEncryption").setProvider("BC").build(issuerPriv));
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }

        return newCert;
    }
    
    /**
     * Validates the certificate chain/path.
     * @param TACerts - Set of Certificates that are the Trust Anchors.
     * @param certificates - List of certificates in the chain/path.
     * @return True if the path is valid, False if it's not.
     */
    public static boolean validateCertificatePath(Set<X509CertificateHolder> TACerts, ArrayList<X509CertificateHolder> certificates)
    {
        Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
        
        //Convert all our TA Certificates to normal X509Certificates.
        for(X509CertificateHolder cert : TACerts)
        {
            
            X509Certificate tempCert = null;
            try {
                tempCert = (new JcaX509CertificateConverter()).getCertificate(cert);
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            trustAnchors.add(new TrustAnchor(tempCert,null));
        }
        
        PKIXBuilderParameters params = null;
        try {
            params = new PKIXBuilderParameters(trustAnchors,new X509CertSelector());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        //Build a Certificate Store with the certificates from the chain.
        JcaCertStoreBuilder builder = new JcaCertStoreBuilder();
        for(X509CertificateHolder c : certificates)
        {
            System.out.println("---Chain Cert---");
            System.out.println("SUBJ: " + c.getSubject().toString());
            System.out.println("ISSUER: " + c.getIssuer().toString());
            builder.addCertificate(c);
        }
        
        //Add the store to the build parameters
        try {
            params.addCertStore(builder.build());
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(CertificateHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        params.setRevocationEnabled(false);

        //Build the certificate chain - if a result is thrown, we failed.
        PKIXCertPathBuilderSpi pathBuilder = new PKIXCertPathBuilderSpi();
        PKIXCertPathBuilderResult resultPath = null;
        try {
            resultPath = (PKIXCertPathBuilderResult) pathBuilder.engineBuild(params);
        } catch (CertPathBuilderException e) {
            return false;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Pretty basic. Saves an X.509 Certificate to a .PEM File
     * @param cert - X.509 Certificate to be saved
     * @param filename - Full path/filename for the certificate file.
     */
    public static void saveCertToFile(X509CertificateHolder cert, String filename)
    {
        JcaPEMWriter pw = null;
        try {
            pw = new JcaPEMWriter(new FileWriter(new File(filename)));
            pw.writeObject(cert);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Also pretty basic. Load an X.509 Certificate from the .PEM
     * file specified.
     * @param filename - full path name to the certificate to be loaded.
     * @return The certificate loaded from the designated file.
     */
    public static X509CertificateHolder loadCertFromFile(String filename)
    {
        
        PemReader reader = null;
        X509CertificateHolder certificate = null;
        try {
            reader = new PemReader(new FileReader(new File(filename)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            certificate = new X509CertificateHolder(reader.readPemObject().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return certificate;
    }
    
    /**
     * 
     * @param certFile - file to load the cert from (PEM file)
     * @return The certificate loaded from the designated file.
     */
    public static X509CertificateHolder loadCertFromFile(File certFile)
    {
        
        PemReader reader = null;
        X509CertificateHolder certificate = null;
        try {
            reader = new PemReader(new FileReader(certFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            certificate = new X509CertificateHolder(reader.readPemObject().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return certificate;
    }
    
    /**
     *
     * @param cert - X.509 Certificate to be validated.
     * @param issuingCert - X.509 Certificate that signed the other Certificate.
     * @return - True if the Certificate is valid, False otherwise.
     */
    public static boolean validateCert(X509CertificateHolder cert, X509CertificateHolder issuingCert)
    {
        JcaContentVerifierProviderBuilder builder = new JcaContentVerifierProviderBuilder();
        ContentVerifierProvider verifier = null;
        try {
            verifier = builder.build(issuingCert);
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        if(!cert.isValidOn(new Date()))
        {
            return false;
        }

        try {
            if(!cert.isSignatureValid(verifier))
            {
                return false;
            }
        } catch (CertException e) {
            e.printStackTrace();
        }

        return true;
    }
}
