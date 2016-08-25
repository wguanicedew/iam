package it.infn.mw.iam.api.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;

@Controller
public class PasswordResetController {

  @Autowired
  private PasswordResetService service;


  @RequestMapping(value = "/iam/password-reset/{token}", method = RequestMethod.GET)
  public String resetPassword(Model model, @PathVariable("token") String token) {
    String message = null;
    try {
      if (!service.checkResetKey(token)) {
        message = "This account is not active. Cannot reset password!";
      }
    } catch (ScimResourceNotFoundException e) {
      message = "Invalid reset key: " + e.getMessage();
    }

    model.addAttribute("errorMessage", message);
    model.addAttribute("resetKey", token);

    return "iam/resetPassword";
  }

  @RequestMapping(value = "/iam/password-reset", method = RequestMethod.POST,
      produces = "text/plain")
  @ResponseBody
  public String changePassword(@RequestParam(required = true, name = "resetkey") String resetKey,
      @RequestParam(required = true, name = "password") String password) {
    String retval = null;
    try {
      service.changePassword(resetKey, password);
      retval = "ok";
    } catch (Exception e) {
      retval = "err";
    }
    return retval;
  }

  @RequestMapping(value = "/iam/password-forgot/{email}", method = RequestMethod.GET)
  public void forgotPassword(@PathVariable("email") String email) {
    service.forgotPassword(email);
  }

}
