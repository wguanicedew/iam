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
package it.infn.mw.iam.api.registration.cern.error;

import org.springframework.security.access.AccessDeniedException;

public class InsufficientAuthenticationError extends AccessDeniedException {

  private static final long serialVersionUID = 1L;

  public InsufficientAuthenticationError(String msg) {
    super(msg);
  }

  public InsufficientAuthenticationError(String msg, Throwable t) {
    super(msg, t);
  }

}
