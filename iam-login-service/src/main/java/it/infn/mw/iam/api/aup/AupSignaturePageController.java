package it.infn.mw.iam.api.aup;

import static it.infn.mw.iam.core.web.EnforceAupFilter.REQUESTING_SIGNATURE;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.core.time.TimeProvider;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAup;
import it.infn.mw.iam.persistence.repository.IamAupRepository;
import it.infn.mw.iam.persistence.repository.IamAupSignatureRepository;

@Controller
public class AupSignaturePageController {


  final IamAupRepository repo;
  final IamAupSignatureRepository signatureRepo;
  final AccountUtils accountUtils;
  final TimeProvider timeProvider;

  @Autowired
  public AupSignaturePageController(IamAupRepository aupRepo,
      IamAupSignatureRepository aupSignatureRepo, AccountUtils accountUtils,
      TimeProvider timeProvider) {
    this.repo = aupRepo;
    this.signatureRepo = aupSignatureRepo;
    this.accountUtils = accountUtils;
    this.timeProvider = timeProvider;
  }

  @PreAuthorize("hasRole('USER')")
  @RequestMapping("/iam/aup/sign")
  public ModelAndView signAupPage() {
    ModelAndView view;

    Optional<IamAup> aup = repo.findDefaultAup();

    if (aup.isPresent()) {
      view = new ModelAndView("iam/signAup");
      view.addObject("aup", aup.get());
    } else {
      view = new ModelAndView("iam/noAup");
    }

    return view;
  }

  private Optional<SavedRequest> checkForSavedSpringSecurityRequest(HttpSession session) {
    SavedRequest savedRequest =
        (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

    if (!isNull(savedRequest)) {
      session.removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
    }

    return ofNullable(savedRequest);

  }


  @PreAuthorize("hasRole('USER')")
  @RequestMapping(method = RequestMethod.POST, value = "/iam/aup/sign")
  public ModelAndView signAup(HttpServletRequest request, HttpServletResponse response,
      HttpSession session) {

    Optional<IamAup> aup = repo.findDefaultAup();


    if (!aup.isPresent()) {
      return new ModelAndView("iam/noAup");
    }

    if (aup.isPresent()) {
      Date now = new Date(timeProvider.currentTimeMillis());
      IamAccount account = accountUtils.getAuthenticatedUserAccount().orElseThrow(
          () -> new IllegalStateException("No iam account found for authenticated user"));

      signatureRepo.createSignatureForAccount(account, now);

      if (!isNull(session.getAttribute(REQUESTING_SIGNATURE))) {
        session.removeAttribute(REQUESTING_SIGNATURE);
        Optional<SavedRequest> savedRequest = checkForSavedSpringSecurityRequest(session);

        if (savedRequest.isPresent()) {
          return new ModelAndView(format("redirect:%s", savedRequest.get().getRedirectUrl()));
        }
      }
    }

    return new ModelAndView("redirect:/dashboard");
  }
}


