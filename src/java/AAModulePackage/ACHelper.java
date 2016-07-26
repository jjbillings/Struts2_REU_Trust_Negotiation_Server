/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AAModulePackage;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERGeneralString;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v2AttributeCertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.ASN1Set;

/**
 * Created by jjbillings on 6/28/16.
 */
public class ACHelper {

    /**
     * Pretty simple. Take the input X.509 AC and write it out to a .PEM file.
     * @param cert - X.509 Attribute Certificate
     * @param fileName - Full path/filename for the AC.
     */
    public static void saveAttributeCertToFile(X509AttributeCertificateHolder cert, String fileName)
    {
        File atrCertFile = new File(fileName);
        JcaPEMWriter pw = null;
        try {
            pw = new JcaPEMWriter(new FileWriter(atrCertFile));
            pw.writeObject(cert);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an AC from a given file
     * @param acFile - File that should contain an attribute certificate
     * @return X509AttributeCertificateHolder - the X.509 attribute certificate loaded from the file.
     */
    public static X509AttributeCertificateHolder loadAttributeCertFromFile(File acFile)
    {
        PemReader reader = null;
        try {
            reader = new PemReader(new FileReader(acFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        X509AttributeCertificateHolder ac = null;
        try {
            ac = new X509AttributeCertificateHolder(reader.readPemObject().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ac;
    }

    /**
     * Validate the AC's cryptographic signature, checking against trusted
     * issuing entity's ACs.
     * @param ac - X.509 Attribute Certificate to have its signature checked.
     * @param trustedIssuers - Set of certificates owned by trust AC Issuing
     * Entities.
     * @return True if the signature is valid, False if it is not.
     */
    public static boolean validateACSignature(X509AttributeCertificateHolder ac, HashSet<X509CertificateHolder> trustedIssuers)
    {
        JcaContentVerifierProviderBuilder b = new JcaContentVerifierProviderBuilder();
        b.setProvider("BC");
        
        for(X509CertificateHolder issuerCert : trustedIssuers)
        {
            try 
            {
                if(ac.isSignatureValid(b.build(issuerCert)))
                {
                    System.out.println("Signature for AC has been validated");
                    return true;
                }
            } catch (CertException ex) {
                Logger.getLogger(ACHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (OperatorCreationException ex) {
                Logger.getLogger(ACHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CertificateException ex) {
                Logger.getLogger(ACHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    
    /**
     * This method takes in an AC and wraps it up in the wrapper class.
     * @param ac - X509AttributeCertificateHold that we want to wrap.
     * @param fileName - name of the file where "ac" is stored.
     * @return wrapped up AC.
     */
    public static AttributeCertificateWrapper extractAttributes(X509AttributeCertificateHolder ac)
    {
        AttributeCertificateWrapper wrapper = new AttributeCertificateWrapper(ac);

        for(Attribute a : ac.getAttributes(NewAttributeIdentifiers.role))
        {
            ASN1Set set = a.getAttrValues();
            String s = DERGeneralString.getInstance(set.getObjectAt(0)).getString();
            wrapper.setRole(s);
        }

        for(Attribute a : ac.getAttributes(NewAttributeIdentifiers.record_id))
        {
            ASN1Set set =  a.getAttrValues();
            String s = DERGeneralString.getInstance(set.getObjectAt(0)).getString();
            wrapper.setRecordId(s);
            wrapper.setCertFileName(s + ".pem");
        }

        for(Attribute a : ac.getAttributes(NewAttributeIdentifiers.time_stamp))
        {
            ASN1Set set = a.getAttrValues();
            Time t = new Time(set.getObjectAt(0).toASN1Primitive());
            wrapper.setTimeStamp(t);
        }

        for (Attribute a : ac.getAttributes(NewAttributeIdentifiers.record_type))
        {
            ASN1Set set = a.getAttrValues();
            String[] arr = new String[set.size()];
            for (int i = 0; i < set.size(); ++i)
            {
                arr[i] = DERGeneralString.getInstance(set.getObjectAt(i)).getString();
            }
            wrapper.setRecordTypes(arr);
        }
        
        for(Attribute a : ac.getAttributes(NewAttributeIdentifiers.record_subject))
        {
            ASN1Set set = a.getAttrValues();
            String s = DERGeneralString.getInstance(set.getObjectAt(0)).getString();
            wrapper.setRecord_subject(s);
        }

        for (Attribute a : ac.getAttributes(NewAttributeIdentifiers.actions_taken))
        {
            ASN1Set set = a.getAttrValues();
            String[] arr = new String[set.size()];
            for (int i = 0; i < set.size(); ++i)
            {
                arr[i] = DERGeneralString.getInstance(set.getObjectAt(i)).getString();
            }
            wrapper.setActions_taken(arr);
        }
        return wrapper;
    }

    
    public static X509AttributeCertificateHolder generateAttributeCertificate(X509CertificateHolder issuerCert, X509CertificateHolder associatedCert, PrivateKey pk,
                String role, String record_id, String record_subject, String[] record_types, String[] actions_taken)
    {
        //Set up the validity period.
        Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

        //AttributeCertificateHolder is a wrapper class for AttributeCertificates, courtesy of the Legion of Bouncy Castle.
        AttributeCertificateIssuer certIssuer = new AttributeCertificateIssuer(issuerCert.getSubject());

        /*
        Please note the distinction between AttributeCertificateHolder which appears to be the
        Entity in possession of the certificate, while X509AttributeCertificateHolder is a
        wrapper class for the actual certificate itself.
         */
        
        AttributeCertificateHolder holder = new AttributeCertificateHolder(associatedCert);
        X509v2AttributeCertificateBuilder builder = new X509v2AttributeCertificateBuilder(
                holder,
                certIssuer,
                BigInteger.valueOf(System.currentTimeMillis()),
                startDate,
                endDate);

        
        builder.addAttribute(NewAttributeIdentifiers.role, new DERGeneralString(role));
        builder.addAttribute(NewAttributeIdentifiers.record_id, new DERGeneralString(record_id));
        builder.addAttribute(NewAttributeIdentifiers.record_subject, new DERGeneralString(record_subject));
        builder.addAttribute(NewAttributeIdentifiers.time_stamp, new DERGeneralizedTime(new Date()));

        //record_types
        ArrayList<ASN1Encodable> rts = new ArrayList();
        for(String s : record_types)
        {
            rts.add(new DERGeneralString(s));
        }
        ASN1Encodable[] recTypes = rts.toArray(new DERGeneralString[rts.size()]);
        
        builder.addAttribute(NewAttributeIdentifiers.record_type, recTypes);
        
        //actions_taken
        ArrayList<ASN1Encodable> acts = new ArrayList();
        for(String s : actions_taken)
        {
            acts.add(new DERGeneralString(s));
        }
        ASN1Encodable[] actionsTaken = acts.toArray(new DERGeneralString[acts.size()]);
        builder.addAttribute(NewAttributeIdentifiers.actions_taken, actionsTaken);

        //Build the certificate
        X509AttributeCertificateHolder attrCert = null;
        try {
            //builds the attribute certificate, and signs it with the owner's private key.
            attrCert = builder.build(new JcaContentSignerBuilder("SHA256withRSAEncryption").setProvider("BC").build(pk));
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }

        System.out.println("ATTRIBUTE CERTIFICATE Successfully generated.");

        return attrCert;
    }
    

    /**
     * Checks to see if the X.509 ID Certificate "Owns" the AC.
     * @param ac - X.509 Attribute Certificate
     * @param associatedCert - "Owner's" ID Certificate.
     * @return - True if the ID Cert "owns" the AC, False otherwise.
     */
    public static boolean checkAssociatedCertificate(X509AttributeCertificateHolder ac, X509CertificateHolder associatedCert)
    {
        BigInteger acSerial = ac.getHolder().getSerialNumber();
        
        if(acSerial.equals(associatedCert.getSerialNumber()))
        {
            return true;
        }
        return false;
    }
}

