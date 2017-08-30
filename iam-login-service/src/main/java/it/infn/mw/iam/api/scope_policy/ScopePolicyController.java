package it.infn.mw.iam.api.scope_policy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.common.ErrorDTO;
import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.repository.IamScopePolicyRepository;


@RestController
@PreAuthorize("hasRole('ADMIN')")
public class ScopePolicyController {

  private final IamScopePolicyRepository policyRepo;
  private final IamScopePolicyConverter converter;

  @Autowired
  public ScopePolicyController(IamScopePolicyRepository repo, IamScopePolicyConverter converter) {
    this.policyRepo = repo;
    this.converter = converter;
  }

  @RequestMapping(value = "/iam/scope_policies", method = RequestMethod.GET)
  public List<ScopePolicyDTO> listScopePolicies() {

    Iterable<IamScopePolicy> policies = policyRepo.findAll();
    List<ScopePolicyDTO> dtos = new ArrayList<>();

    policies.forEach(p -> dtos.add(converter.fromModel(p)));

    return dtos;
  }



  @RequestMapping(value = "/iam/scope_policies", method = RequestMethod.POST)
  @ResponseStatus(code = HttpStatus.CREATED)
  public void addScopePolicy(@Valid @RequestBody ScopePolicyDTO policy,
      BindingResult validationResult) {

    if (validationResult.hasErrors()) {
      throw buildValidationError(validationResult);
    }

    Date now = new Date();

    IamScopePolicy p = converter.toModel(policy);

    p.setCreationTime(now);
    p.setLastUpdateTime(now);

    policyRepo.save(p);
  }

  
  @RequestMapping(value = "/iam/scope_policies/{id}", method = RequestMethod.GET)
  public ScopePolicyDTO getScopePolicy(@PathVariable Long id) {

    IamScopePolicy p = policyRepo.findById(id)
      .orElseThrow(() -> new ScopePolicyNotFoundError("No scope policy found for id: " + id));

    return converter.fromModel(p);

  }
  
  @RequestMapping(value = "/iam/scope_policies/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteScopePolicy(@PathVariable Long id) {

    IamScopePolicy p = policyRepo.findById(id)
      .orElseThrow(() -> new ScopePolicyNotFoundError("No scope policy found for id: " + id));

    policyRepo.delete(p);

  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(ScopePolicyNotFoundError.class)
  public ErrorDTO notFoundError(Exception ex) {
    return ErrorDTO.fromString(ex.getMessage());
  }
  
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidScopePolicyError.class)
  public ErrorDTO validationError(Exception ex) {
    return ErrorDTO.fromString(ex.getMessage());
  }

  protected InvalidScopePolicyError buildValidationError(BindingResult result) {
    String firstErrorMessage = result.getAllErrors().get(0).getDefaultMessage();
    return new InvalidScopePolicyError(firstErrorMessage);
  }
}
