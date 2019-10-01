/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2019
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
package it.infn.mw.iam.api.scope_policy.validation;

import static it.infn.mw.iam.persistence.model.IamScopePolicy.MatchingPolicy.PATH;
import static it.infn.mw.iam.persistence.model.IamScopePolicy.MatchingPolicy.REGEXP;
import static java.util.Objects.isNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import it.infn.mw.iam.api.scope_policy.ScopePolicyDTO;

public class ScopePolicyValidator implements ConstraintValidator<ScopePolicy, ScopePolicyDTO> {

  private static final String EMPTY_SCOPE_MSG =
      "{it.infn.mw.iam.api.scope_policy.validation.ScopePolicyValidator.emptyScope.message}";

  public ScopePolicyValidator() {
    // empty
  }

  @Override
  public void initialize(ScopePolicy constraintAnnotation) {
    // empty
  }

  private boolean isValidMatchingPolicy(ScopePolicyDTO value, ConstraintValidatorContext context) {
    if (value.getMatchingPolicy().equals(PATH.name())
        || value.getMatchingPolicy().equals(REGEXP.name())) {
      if (isNull(value.getScopes()) || value.getScopes().isEmpty()) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(EMPTY_SCOPE_MSG).addConstraintViolation();
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean isValid(ScopePolicyDTO value, ConstraintValidatorContext context) {
    boolean nullChecks =
        !(value == null || (value.getAccount() != null && value.getGroup() != null));

    return nullChecks && isValidMatchingPolicy(value, context);
  }

}
