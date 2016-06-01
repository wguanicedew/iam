package it.infn.mw.iam.api.scim.converter;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimMeta;
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
  public IamGroup fromScim(ScimGroup scimGroup) {

	IamGroup group = new IamGroup();

    group.setUuid(scimGroup.getId());
    group.setCreationTime(scimGroup.getMeta().getCreated());
    group.setLastUpdateTime(scimGroup.getMeta().getLastModified());
    group.setName(scimGroup.getDisplayName());
    
    Set<IamAccount> accounts = new HashSet<IamAccount>();
    for (ScimMemberRef member: scimGroup.getMembers()) {
      IamAccount account = new IamAccount();
      account.setUuid(member.getValue());
      account.setUsername(member.getDisplay());
      accounts.add(account);
    }
    group.setAccounts(accounts);

    return group;
  }

  @Override
  public ScimGroup toScim(IamGroup entity) {
	
	ScimMeta.Builder metaBuilder = new ScimMeta.Builder(
      entity.getCreationTime(), entity.getLastUpdateTime())
        .location(resourceLocationProvider.groupLocation(entity.getUuid()))
        .resourceType(ScimGroup.RESOURCE_TYPE);
	
	ScimGroup.Builder groupBuilder = new ScimGroup.Builder(entity.getName())
      .id(entity.getUuid().toString()).meta(metaBuilder.build());
		
	Set<ScimMemberRef> members = new HashSet<>();
	
	for (IamAccount account : entity.getAccounts()) {
	  ScimMemberRef memberRef = new ScimMemberRef.Builder()
		.value(account.getUuid())
		.display(account.getUsername())
		.ref(resourceLocationProvider.userLocation(account.getUuid()))
		.build();
	  members.add(memberRef);
	}
	groupBuilder.setMembers(members);

	return groupBuilder.build();
  }

}
