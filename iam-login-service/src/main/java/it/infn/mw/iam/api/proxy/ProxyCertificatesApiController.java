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
package it.infn.mw.iam.api.proxy;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyCertificatesApiController {

  final ProxyCertificateService service;

  @Autowired
  public ProxyCertificatesApiController(ProxyCertificateService service) {
    this.service = service;
  }

  @PreAuthorize("#oauth2.hasScope('proxy:generate') and hasRole('USER')")
  public ProxyCertificateDTO generateProxy(Principal authenticatedUser,
      ProxyCertificateRequestDTO request) {
    return service.generateProxy(authenticatedUser, request);
  }



  @PreAuthorize("#oauth2.hasScope('proxy:read') and hasRole('USER')")
  public List<ProxyCertificateDTO> listProxies(Principal authenticatedUser) {
    return service.listProxies(authenticatedUser);
  }

}
