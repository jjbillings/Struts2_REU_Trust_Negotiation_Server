/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AAModulePackage;

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
    private Time time_stamp;
    private String[] record_types;
    private String certFileName;

    public AttributeCertificateWrapper(X509AttributeCertificateHolder ac, String r, String rid, Time t, String[] rts)
    {
        AC = ac;
        role = r;
        record_id = rid;
        time_stamp = t;
        record_types = rts;
    }

    public AttributeCertificateWrapper(X509AttributeCertificateHolder ac, String r, String rid, Time t, String[] rts, String fn)
    {
        AC = ac;
        role = r;
        record_id = rid;
        time_stamp = t;
        record_types = rts;
        certFileName = fn;
    }

    public AttributeCertificateWrapper(X509AttributeCertificateHolder ac)
    {
        AC = ac;
    }

    public AttributeCertificateWrapper(X509AttributeCertificateHolder ac, String fn)
    {
        AC = ac;
        certFileName = fn;
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

    public void setCertFileName(String fn)
    {
        certFileName = fn;
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

    public String getCertFileName()
    {
        return certFileName;
    }

    public String[] getAttributes()
    {
        String[] s = new String[3+record_types.length];
        s[0] = role;
        s[1] = record_id;
        s[2] = time_stamp.getDate().toString();
        for(int i = 0; i < record_types.length; ++i)
        {
            s[i+3] = record_types[i];
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
        return "Role: " + role + ", Record Id: " + record_id; //TODO: make it better.
    }
}

