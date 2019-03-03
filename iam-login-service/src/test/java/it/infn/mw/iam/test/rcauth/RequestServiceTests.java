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
package it.infn.mw.iam.test.rcauth;

import static it.infn.mw.iam.rcauth.DefaultRcAuthRequestService.CLIENT_ID_PARAM;
import static it.infn.mw.iam.rcauth.DefaultRcAuthRequestService.CODE;
import static it.infn.mw.iam.rcauth.DefaultRcAuthRequestService.DEFAULT_SCOPE;
import static it.infn.mw.iam.rcauth.DefaultRcAuthRequestService.NONCE_PARAM;
import static it.infn.mw.iam.rcauth.DefaultRcAuthRequestService.REDIRECT_URI_PARAM;
import static it.infn.mw.iam.rcauth.DefaultRcAuthRequestService.RESPONSE_TYPE_PARAM;
import static it.infn.mw.iam.rcauth.DefaultRcAuthRequestService.SCOPE_PARAM;
import static it.infn.mw.iam.rcauth.DefaultRcAuthRequestService.STATE_PARAM;
import static it.infn.mw.iam.rcauth.RCAuthController.CALLBACK_PATH;
import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.openid.connect.client.service.ServerConfigurationService;
import org.mitre.openid.connect.config.ServerConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import it.infn.mw.iam.authn.oidc.OidcTokenRequestor;
import it.infn.mw.iam.config.saml.IamProperties;
import it.infn.mw.iam.rcauth.DefaultRcAuthRequestService;
import it.infn.mw.iam.rcauth.RCAuthProperties;

@RunWith(MockitoJUnitRunner.class)
public class RequestServiceTests extends RCAuthTestSupport {


  @Mock
  IamProperties iamProps;

  @Mock
  RCAuthProperties props;

  @Mock
  ServerConfigurationService scs;

  @Mock
  HttpSession session;

  @Mock
  ServerConfiguration serverConfig;
  
  @Mock
  OidcTokenRequestor tokenRequestor;

  @InjectMocks
  DefaultRcAuthRequestService service;

  @Before
  public void setup() {
    when(props.getClientId()).thenReturn(CLIENT_ID);
    when(props.getIssuer()).thenReturn(ISSUER);
    when(scs.getServerConfiguration(Mockito.anyString())).thenReturn(serverConfig);
    when(serverConfig.getAuthorizationEndpointUri()).thenReturn(AUTHORIZATION_URI);
    when(iamProps.getBaseUrl()).thenReturn(IAM_BASE_URL);
  }


  @Test
  public void testUrlGeneration() throws UnsupportedEncodingException {
    String url = service.buildAuthorizationRequest(session);

    UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();

    assertThat(uri.getHost(), is(RCAUTH_HOST));
    assertThat(uri.getScheme(), is(HTTPS));
    assertThat(uri.getQueryParams().getFirst(STATE_PARAM), notNullValue());
    assertThat(uri.getQueryParams().getFirst(NONCE_PARAM), notNullValue());
    assertThat(uri.getQueryParams().getFirst(SCOPE_PARAM),
        is(URLEncoder.encode(DEFAULT_SCOPE, "UTF-8")));
    assertThat(uri.getQueryParams().getFirst(RESPONSE_TYPE_PARAM), is(CODE));
    assertThat(uri.getQueryParams().getFirst(CLIENT_ID_PARAM), is(CLIENT_ID));
    assertThat(uri.getQueryParams().getFirst(REDIRECT_URI_PARAM),
        is(URLEncoder.encode(format("%s%s", IAM_BASE_URL, CALLBACK_PATH), "UTF-8")));

  }
}
