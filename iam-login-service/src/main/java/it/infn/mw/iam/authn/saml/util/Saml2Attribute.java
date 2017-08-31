package it.infn.mw.iam.authn.saml.util;

public enum Saml2Attribute {

  EPUID("eduPersonUniqueId", "urn:oid:1.3.6.1.4.1.5923.1.1.1.13"),
  EPTID("eduPersonTargetedId", "urn:oid:1.3.6.1.4.1.5923.1.1.1.10"),
  EPPN("eduPersonPrincipalName", "urn:oid:1.3.6.1.4.1.5923.1.1.1.6"),
  EPORCID("eduPersonOrcid", "urn:oid:1.3.6.1.4.1.5923.1.1.1.16"),
  MAIL("mail", "urn:oid:0.9.2342.19200300.100.1.3"),
  GIVEN_NAME("givenName", "urn:oid:2.5.4.42"),
  SN("sn", "urn:oid:2.5.4.4"),
  CN("cn", "urn:oid:2.5.4.3"),
  EMPLOYEE_NUMBER("employeeNumber", "urn:oid:2.16.840.1.113730.3.1.3"),
  SPID_CODE("spidCode", "spidCode");

  private String alias;
  private String attributeName;
  
  private Saml2Attribute(String alias, String attributeName) {
    this.alias = alias;
    this.attributeName = attributeName;
  }
  
  public String getAlias() {
    return alias;
  }

  public String getAttributeName() {
    return attributeName;
  }
  
}
