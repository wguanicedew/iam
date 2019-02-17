package it.infn.mw.iam.rcauth.x509;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import eu.emi.security.authn.x509.proxy.ProxyCertificate;

public interface ProxyHelperService {
  
  public ProxyCertificate generateProxy(X509Certificate cert, PrivateKey key);
  
  public String proxyCertificateToPemString(ProxyCertificate proxy);

}
