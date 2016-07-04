package it.infn.mw.iam.authn.saml.util;

public class SamlIdResolvers {
  
  public static final String EPUID_NAME = "urn:oid:1.3.6.1.4.1.5923.1.1.1.13";
  public static final String EPPN_NAME = "urn:oid:1.3.6.1.4.1.5923.1.1.1.6";
  public static final String MAIL_NAME = "urn:oid:0.9.2342.19200300.100.1.3";
  
  private SamlIdResolvers() {}
  
  public static SamlUserIdentifierResolver epuid(){
    return new AttributeUserIdentifierResolver(EPPN_NAME);
  }
  
  public static SamlUserIdentifierResolver eppn(){
    return new AttributeUserIdentifierResolver(EPPN_NAME);
  }
  
  public static SamlUserIdentifierResolver mail(){
    return new AttributeUserIdentifierResolver(MAIL_NAME);
  }

  public static SamlUserIdentifierResolver nameid(){
    return new NameIdUserIdentifierResolver();
  }
}
