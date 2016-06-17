package it.infn.mw.tc;

import java.security.Principal;
import java.util.Arrays;


import javax.servlet.http.HttpServletRequest;

import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class LocalController implements ErrorController{

  @Autowired
  IamClientConfig clientConfig;
  
  @Autowired
  ClientHttpRequestFactory requestFactory;
  
  @RequestMapping("/")
  public String index(){
    return "index";
  }
  
  @RequestMapping("/error")
  public String error(HttpServletRequest request, Model model) {
    
    AuthenticationException authException = (AuthenticationException) request.getAttribute("authnException");
    
    if (authException ==  null){
      model.addAttribute("error", "Unexpected error");
    } else {
      model.addAttribute("error", authException.getMessage());
    }
        
    return "index";
  }
  
  
  @Override
  public String getErrorPath() {

    return "/error";
  }

}
