package it.infn.mw.iam.api.scope_policy;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(value = ScopePolicyController.BASE_RESOURCE)
public class ScopePolicyController {

  public static final String BASE_RESOURCE = "/iam";


}
