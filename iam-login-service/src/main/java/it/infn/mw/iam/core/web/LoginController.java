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
package it.infn.mw.iam.core.web;

import static it.infn.mw.iam.authn.ExternalAuthenticationHandlerSupport.EXT_AUTHN_UNREGISTERED_USER_AUTH;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.infn.mw.iam.config.saml.IamSamlProperties;

@Controller
public class LoginController {

  public static final String LOGIN_PAGE_CONFIGURATION_KEY = "loginPageConfiguration";

  @Autowired
  LoginPageConfiguration loginPageConfiguration;
    
  @Autowired
  IamSamlProperties samlProperties;
  
  @RequestMapping("/login")
  public String login(Authentication authentication, Model model, HttpServletRequest request) {

    if (authentication == null) {
      return "iam/login";
    }

    if (authentication.getAuthorities().contains(EXT_AUTHN_UNREGISTERED_USER_AUTH)) {
      return "iam/register";
    }

    return "redirect:/";

  }

  @RequestMapping("/start-registration")
  public String startRegistration() {
    return "iam/register";
  }

  @RequestMapping("/reset-session")
  public String resetSession(HttpSession session) {

    session.invalidate();

    return "redirect:/";
  }

}
