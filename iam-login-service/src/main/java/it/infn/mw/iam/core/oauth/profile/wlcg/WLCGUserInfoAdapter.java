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
package it.infn.mw.iam.core.oauth.profile.wlcg;

import org.mitre.openid.connect.model.UserInfo;

import com.google.gson.JsonObject;

import it.infn.mw.iam.core.userinfo.DelegateUserInfoAdapter;

public class WLCGUserInfoAdapter extends DelegateUserInfoAdapter {

  private static final long serialVersionUID = 1L;

  private WLCGUserInfoAdapter(UserInfo delegate) {
    super(delegate);
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = super.toJson();

    json.remove("groups");

    return json;
  }

  public static WLCGUserInfoAdapter forUserInfo(UserInfo delegate) {
    return new WLCGUserInfoAdapter(delegate);
  }
}
