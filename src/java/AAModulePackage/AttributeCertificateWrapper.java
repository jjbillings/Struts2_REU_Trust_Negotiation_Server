/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AAModulePackage;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

/**
 * This is a wrapper class for X.509 Attribute Certificates.
 * I actually would like to revise this so that it takes an
 * X509AttributeCertificateHolder and automatically extracts the attributes.
 * As it is, we extract the attributes using the ACHelper.
 * Created by jjbillings on 6/9/16.
 */
public class AttributeCertificateWrapper {

    private X509AttributeCertificateHolder AC;
    private String role;
    private String record_id;
    private String record_subject;
    private Time time_stamp;
    private String[] record_types;
    private String[] actions_taken;

    public AttributeCertificateWrapper(X509AttributeCertificateHolder ac, String r, String rid, Time t, String[] rts)
    {
        AC = ac;
        role = r;
        record_id = rid;
        time_stamp = t;
        record_types = rts;
    }
    
    public AttributeCertificateWrapper(X509AttributeCertificateHolder ac, String r, String rid, Time t, String[] rts, String[] actions, String subj)
    {
        AC = ac;
        role = r;
        record_id = rid;
        time_stamp = t;
        record_types = rts;
        this.record_subject = subj;
        this.actions_taken = actions;
    }

    public AttributeCertificateWrapper(X509AttributeCertificateHolder ac)
    {
        AC = ac;
    }
    
    public String getAc_issuer()
    {
        X500Name[] names = this.AC.getHolder().getIssuer();
        return names[0].toString();
    }

    public void setRecordId(String r)
    {
        record_id = r;
    }

    public void setRole(String r)
    {
        role = r;
    }

    public void setTimeStamp(Time t)
    {
        time_stamp = t;
    }

    public void setRecordTypes(String[] r)
    {
        record_types = r.clone();
    }

    public X509AttributeCertificateHolder getAC()
    {
        return AC;
    }

    public String getRole()
    {
        return role;
    }

    public String getRecordId()
    {
        return record_id;
    }

    public Time getTimeStamp()
    {
        return time_stamp;
    }

    public String[] getRecordTypes()
    {
        return record_types;
    }

    public String[] getAttributes()
    {
        String[] s = new String[4+record_types.length + actions_taken.length];
        s[0] = role;
        s[1] = record_id;
        s[2] = record_subject;
        s[3] = time_stamp.getDate().toString();
        for(int i = 4; i < record_types.length; ++i)
        {
            s[i] = record_types[i];
        }
        for(int i = 4 + record_types.length; i < actions_taken.length; ++i)
        {
            s[i] = actions_taken[i];
        }
        return s;
    }

    //Will hopefully be useful when searching for record types.
    public boolean containsRecordType(String type)
    {
        for(String s : record_types)
        {
            if(s.equals(type))
            {
                return true;
            }
        }
        return false;
    }

    public String toString()
    {
        return "Role: " + role + ", Record Id: " + record_id + ", Record Subject: " + record_subject; //TODO: make it better.
    }

    /**
     * @return the record_subject
     */
    public String getRecord_subject() {
        return record_subject;
    }

    /**
     * @param record_subject the record_subject to set
     */
    public void setRecord_subject(String record_subject) {
        this.record_subject = record_subject;
    }

    /**
     * @return the actions_taken
     */
    public String[] getActions_taken() {
        return actions_taken;
    }

    /**
     * @param actions_taken the actions_taken to set
     */
    public void setActions_taken(String[] actions_taken) {
        this.actions_taken = actions_taken;
    }
}

