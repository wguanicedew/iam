/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2021
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
package it.infn.mw.iam.test.actuator;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import it.infn.mw.iam.test.util.annotation.IamMockMvcIntegrationTest;

@RunWith(SpringRunner.class)
@IamMockMvcIntegrationTest
public class ActuatorDisabledEnpointsTests extends ActuatorTestSupport {
  
  @Value("${iam.superuser.username}")
  private String basicUsername;

  @Value("${iam.superuser.password}")
  private String basicPassword;

  @Autowired
  private MockMvc mvc;

  @After
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }
  
  @Test
  public void testSensitiveEndpointsAreDisabledByDefault() throws Exception {
    for (String endpoint : PRIVILEGED_ENDPOINTS) {
      // @formatter:off
      mvc.perform(get(endpoint).with(httpBasic(basicUsername, basicPassword)))
        .andExpect(status().isNotFound());
      // @formatter:on
    }
  }
}
