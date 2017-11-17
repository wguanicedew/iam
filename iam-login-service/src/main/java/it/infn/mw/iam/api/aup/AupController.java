package it.infn.mw.iam.api.aup;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.common.ErrorDTO;
import it.infn.mw.iam.persistence.model.IamAup;

@RestController
public class AupController {

  private final AupService service;
  private final AupConverter converter;

  @Autowired
  public AupController(AupService service, AupConverter converter) {
    this.service = service;
    this.converter = converter;

  }

  private RuntimeException buildValidationError(BindingResult validationResult) {
    String firstErrorMessage = validationResult.getAllErrors().get(0).getDefaultMessage();
    return new InvalidAupError(firstErrorMessage);
  }

  @RequestMapping(value = "/iam/aup", method = RequestMethod.GET)
  public AupDTO getAup() {
    IamAup aup = service.findAup().orElseThrow(AupNotFoundError::new);

    return converter.dtoFromEntity(aup);
  }

  @RequestMapping(value = "/iam/aup", method = RequestMethod.POST)
  @ResponseStatus(code = HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public void createAup(@Valid @RequestBody AupDTO aup, BindingResult validationResult) {
    if (service.findAup().isPresent()) {
      throw new AupAlreadyExistsError();
    }

    if (validationResult.hasErrors()) {
      throw buildValidationError(validationResult);
    }

    // Enforce local creation and last update time
    Date now = new Date();
    aup.setCreationTime(now);
    aup.setLastUpdateTime(now);

    service.saveAup(aup);
  }

  @RequestMapping(value = "/iam/aup", method = RequestMethod.DELETE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteAup() {
    service.deleteAup();
  }

  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(AupAlreadyExistsError.class)
  public ErrorDTO aupAlreadyExistsError(Exception ex) {
    return ErrorDTO.fromString(ex.getMessage());
  }

  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidAupError.class)
  public ErrorDTO invalidAupError(Exception ex) {
    return ErrorDTO.fromString(ex.getMessage());
  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(AupNotFoundError.class)
  public ErrorDTO notFoundError(Exception ex) {
    return ErrorDTO.fromString(ex.getMessage());
  }
}
