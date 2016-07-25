package AAModulePackage;

/**
 * This is a wrapper class for the attributes stored in an X.509 Attribute
 * Certificate. We actually don't use this class ever on the server...
 * Created by jjbillings on 6/10/16.
 */
public class AttributeWrapper {

    private String attributeType;
    private String attributeValue;
    private AttributeCertificateWrapper containingAC;

    public AttributeWrapper(String t, String v, AttributeCertificateWrapper ac)
    {
        attributeType = t;
        attributeValue = v;
        containingAC = ac;
    }

    public AttributeCertificateWrapper getContainingAC()
    {
        return containingAC;
    }

    public String getAttributeType()
    {
        return attributeType;
    }

    public String getAttributeValue()
    {
        return attributeValue;
    }

    public String toString()
    {
        return attributeType + ": " + attributeValue;
    }
}
