package it.infn.mw.iam.api.scim.converter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimMeta;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;

@Service
public class GroupConverter implements Converter<ScimGroup, IamGroup> {

  private final ScimResourceLocationProvider resourceLocationProvider;

  @Autowired
  public GroupConverter(ScimResourceLocationProvider rlp) {
    this.resourceLocationProvider = rlp;
  }
  
  @Override
  public IamGroup fromScim(ScimGroup scim) {

	throw new NotImplementedException();
  }

  @Override
  public ScimGroup toScim(IamGroup entity) {
	
	ScimGroup.Builder groupBuilder = new ScimGroup.Builder(entity.getName())
      .id(entity.getUuid().toString());
		
	Set<ScimMemberRef> members = new HashSet<>();
	
	for (IamAccount account : entity.getAccounts()) {
	  ScimMemberRef memberRef = new ScimMemberRef.Builder()
		.value(account.getUuid())
		.display(account.getUsername())
		.ref(resourceLocationProvider.groupLocation(entity.getUuid()))
		.build();
	  members.add(memberRef);
	}
	groupBuilder.setMembers(members);

	return groupBuilder.build();
  }

}
