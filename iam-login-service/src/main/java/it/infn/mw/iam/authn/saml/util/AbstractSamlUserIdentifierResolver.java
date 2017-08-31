package it.infn.mw.iam.authn.saml.util;

public abstract class AbstractSamlUserIdentifierResolver
    implements NamedSamlUserIdentifierResolver {

  private final String name;
  
  public AbstractSamlUserIdentifierResolver(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

}
