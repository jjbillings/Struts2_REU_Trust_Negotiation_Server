
package AAModulePackage;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.X509AttributeIdentifiers;

/**
 * This class provides constant OIDs.
 * These OIDs are used in the X.509 Attribute Certificates to indicate various
 * attributes that are stored in the certs. OIDs were chosen arbitrarily.
 * Created by jjbillings on 6/17/16.
 */
public class NewAttributeIdentifiers implements X509AttributeIdentifiers {

    public static final ASN1ObjectIdentifier role = new ASN1ObjectIdentifier("1.2.3.1");

    public static final ASN1ObjectIdentifier record_type = new ASN1ObjectIdentifier("1.2.3.2");

    public static final ASN1ObjectIdentifier time_stamp = new ASN1ObjectIdentifier("1.2.3.3");

    public static final ASN1ObjectIdentifier record_id = new ASN1ObjectIdentifier("1.2.3.4");
    
    public static final ASN1ObjectIdentifier record_subject = new ASN1ObjectIdentifier("1.2.3.5");
    
    public static final ASN1ObjectIdentifier actions_taken = new ASN1ObjectIdentifier("1.2.3.6");
}
