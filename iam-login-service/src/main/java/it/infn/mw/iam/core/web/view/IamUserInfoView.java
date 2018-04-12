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
package it.infn.mw.iam.core.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mitre.openid.connect.view.UserInfoView;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

@Primary
@Component(IamUserInfoView.VIEWNAME)
public class IamUserInfoView extends UserInfoView {

  public static final String VIEWNAME = "iamUserInfo";

  public static final String EXTN_AUTHN_INFO_KEY = "external_authn";

  protected void addExternalAuthenticationInfo(JsonObject json, Map<String, Object> model) {

    @SuppressWarnings("unchecked")
    Map<String, String> externalAuthnInfo = (Map<String, String>) model.get(EXTN_AUTHN_INFO_KEY);

    JsonObject extAuthn = new JsonObject();
    for (Map.Entry<String, String> e : externalAuthnInfo.entrySet()) {
      extAuthn.addProperty(e.getKey(), e.getValue());
    }

    json.add(EXTN_AUTHN_INFO_KEY, extAuthn);
  }

  @Override
  protected void writeOut(JsonObject json, Map<String, Object> model, HttpServletRequest request,
      HttpServletResponse response) {

    if (model.containsKey(EXTN_AUTHN_INFO_KEY)) {
      addExternalAuthenticationInfo(json, model);
    }

    super.writeOut(json, model, request, response);
  }

}
