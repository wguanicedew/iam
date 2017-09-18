package it.infn.mw.iam.core.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IamErrorController implements ErrorController {

  private static final String IAM_ERROR_VIEW = "iam/error";
  private static final String PATH = "/error";

  @RequestMapping(PATH)
  public ModelAndView error(HttpServletRequest request) {

    ModelAndView errorPage = new ModelAndView(IAM_ERROR_VIEW);
    
    HttpStatus status = HttpStatus.valueOf(getErrorCode(request));
    
    errorPage.addObject("errorMessage", String.format("%d. %s", status.value(), 
        status.getReasonPhrase()));
      
    Exception exception = getRequestException(request);
    
    if (exception != null){
      errorPage.addObject("exceptionMessage", exception.getMessage());
      errorPage.addObject("exceptionStackTrace", ExceptionUtils.getStackTrace(exception).trim());
    }
    return errorPage;
  }
  
  @RequestMapping("/exception")
  public String exception(){
    throw new IllegalStateException("Illo camughe!");
  }

  private int getErrorCode(HttpServletRequest httpRequest) {
    return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
  }

  private Exception getRequestException(HttpServletRequest httpRequest) {
    return (Exception) httpRequest.getAttribute("javax.servlet.error.exception");
  }

  @Override
  public String getErrorPath() {
    return PATH;
  }

}
