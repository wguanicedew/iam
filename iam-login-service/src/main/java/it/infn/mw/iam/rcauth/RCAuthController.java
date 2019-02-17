/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.iam.rcauth;

import static eu.emi.security.authn.x509.impl.X500NameUtils.getReadableForm;
import static it.infn.mw.iam.authn.x509.X509CertificateVerificationResult.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.security.Principal;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import it.infn.mw.iam.api.account_linking.DefaultAccountLinkingService;
import it.infn.mw.iam.authn.x509.IamX509AuthenticationCredential;
import it.infn.mw.iam.rcauth.x509.ProxyHelperService;

@Controller
@ConditionalOnProperty(name = "rcauth.enabled", havingValue = "true")
public class RCAuthController {

  public static final String GETCERT_PATH = "/rcauth/getcert";
  public static final String CALLBACK_PATH = "/rcauth/cb";

  final RcAuthRequestService requestService;
  final ProxyHelperService proxyHelper;
  final DefaultAccountLinkingService linkingService;

  @Autowired
  public RCAuthController(RcAuthRequestService service, ProxyHelperService proxyHelper,
      DefaultAccountLinkingService ls) {
    this.requestService = service;
    this.proxyHelper = proxyHelper;
    this.linkingService = ls;
  }

  @RequestMapping(method = GET, value = GETCERT_PATH)
  public RedirectView requestCert(HttpSession session) {
    return new RedirectView(requestService.buildAuthorizationRequest(session));
  }


  @RequestMapping(method = GET, value = CALLBACK_PATH)
  public String rcauthCallback(Principal authenticatedUser,
      @Valid RCAuthAuthorizationResponse response, HttpSession session) {
    RCAuthExchangeContext ctx = requestService.handleAuthorizationCodeResponse(session, response);

    ProxyCertificate proxy = proxyHelper.generateProxy(ctx.getCertificate(),
        ctx.getCertificateRequest().getKeyPair().getPrivate());

    String proxyPem = proxyHelper.proxyCertificateToPemString(proxy);
    IamX509AuthenticationCredential cred = IamX509AuthenticationCredential.builder()
      .certificateChain(new X509Certificate[] {ctx.getCertificate()})
      .subject(getReadableForm(ctx.getCertificate().getSubjectX500Principal()))
      .issuer(getReadableForm(ctx.getCertificate().getIssuerX500Principal()))
      .verificationResult(success())
      .build();

    linkingService.linkX509ProxyCertificate(authenticatedUser, cred,
        proxyPem);
    
    return "redirect:/dashboard";
  }
}
