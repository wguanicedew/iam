package it.infn.mw.iam.api.scim.new_updater;

import java.util.List;

import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;


public interface Parser {

  List<Updater> getUpdatersForRequest(IamAccount account, ScimPatchOperation<ScimUser> u);
}
