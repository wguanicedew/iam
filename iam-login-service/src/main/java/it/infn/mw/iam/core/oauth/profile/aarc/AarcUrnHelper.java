package it.infn.mw.iam.core.oauth.profile.aarc;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.persistence.model.IamGroup;

@Component
public class AarcUrnHelper {

  @Value("${iam.organisation.name}")
  String organisationName;

  @Value("${iam.urn.namespace")
  String namespace;

  public Set<String> resolveGroups(Set<IamGroup> iamGroups) {

    Set<String> encodedGroups = Collections.emptySet();
    iamGroups.forEach(g -> encodedGroups.add(encodeGroup(g)));
    return encodedGroups;
  }

  public String encodeGroup(IamGroup group) {

    StringBuilder urn = new StringBuilder();
    urn.append("urn:" + namespace + ":group:");
    urn.append(group.getName());
    urn.append(":");

    StringBuilder groupHierarchy = new StringBuilder(group.getName());
    Optional<IamGroup> parent = Optional.ofNullable(group.getParentGroup());
    while (parent.isPresent()) {
      groupHierarchy.insert(0, parent.get().getName() + ":");
      parent = Optional.ofNullable(parent.get().getParentGroup());
    }

    urn.append(groupHierarchy.toString());
    urn.append("#" + organisationName);
    return urn.toString();
  }
}
