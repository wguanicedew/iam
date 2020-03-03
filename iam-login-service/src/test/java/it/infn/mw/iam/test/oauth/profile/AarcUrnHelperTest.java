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
package it.infn.mw.iam.test.oauth.profile;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.core.oauth.profile.aarc.AarcUrnHelper;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@TestPropertySource(properties = {
  // @formatter:off
  "iam.organisation.name=org",
  "iam.urn.namespace=geant:iam:test",
  // @formatter:on
})
public class AarcUrnHelperTest {


  @Autowired
  AarcUrnHelper helper;

  @Before
  public void setup() {

  }

  protected IamGroup buildGroup(String name) {

    return buildGroup(name, null);
  }

  protected IamGroup buildGroup(String name, IamGroup parentGroup) {

    IamGroup g = new IamGroup();

    g.setUuid(UUID.randomUUID().toString());
    g.setName(name);
    g.setParentGroup(parentGroup);

    return g;
  }

  @Test
  public void testGroupUrnEncode() {

    String s = "urn:geant:iam:test:group:test#org";

    IamGroup g = buildGroup("test");

    Set<String> urns = helper.resolveGroups(Sets.newHashSet(g));
    assertThat(urns, hasSize(1));
    assertThat(urns, hasItem(s));
  }

  @Test
  public void testGroupHierarchyUrnEncode() {

    String parentUrn = "urn:geant:iam:test:group:parent#org";
    String childUrn = "urn:geant:iam:test:group:parent:child#org";

    IamGroup parent = buildGroup("parent");
    IamGroup child = buildGroup("child", parent);

    Set<String> urns = helper.resolveGroups(Sets.newHashSet(parent, child));
    assertThat(urns, hasSize(2));
    assertThat(urns, hasItem(parentUrn));
    assertThat(urns, hasItem(childUrn));
  }

}
