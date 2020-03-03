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
