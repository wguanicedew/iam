/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
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
package it.infn.mw.iam.test.rcauth;

import java.io.IOException;

import org.bouncycastle.operator.OperatorCreationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import it.infn.mw.iam.rcauth.x509.CertifcateRequestUtil;
import it.infn.mw.iam.rcauth.x509.CertificateRequestHolder;

@RunWith(JUnit4.class)
public class X509UtilsTest {

  
  @Test
  public void testCertGen() throws OperatorCreationException, IOException {
    String dn = "CN=example";
    int keySize = 2048;
    
    CertificateRequestHolder holder = CertifcateRequestUtil.buildCertificateRequest(dn, keySize);
    System.out.println(CertifcateRequestUtil.certRequestToPemString(holder.getRequest()));
    System.out.println(CertifcateRequestUtil.certRequestToBase64String(holder.getRequest()));
    
    
  }
}
