package it.infn.mw.iam.core.oauth.profile.aarc;

import org.mitre.openid.connect.model.UserInfo;

import com.google.gson.JsonObject;

import it.infn.mw.iam.core.userinfo.DelegateUserInfoAdapter;

public class AarcUserInfoAdapter extends DelegateUserInfoAdapter {

  private static final long serialVersionUID = 1L;

  private final AarcUrnHelper aarcUrnHelper;

  public AarcUserInfoAdapter(UserInfo delegate, AarcUrnHelper aarcUrnHelper) {
    super(delegate);
    this.aarcUrnHelper = aarcUrnHelper;
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = super.toJson();

    json.remove("groups");

    return json;
  }

  public static AarcUserInfoAdapter forUserInfo(UserInfo delegate, AarcUrnHelper aarcUrnHelper) {
    return new AarcUserInfoAdapter(delegate, aarcUrnHelper);
  }
}
