package it.infn.mw.iam.test.api.tokens;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.google.common.collect.Lists;

public class MultiValueMapBuilder {

  private MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

  public static MultiValueMapBuilder builder() {

    return new MultiValueMapBuilder();
  }

  MultiValueMapBuilder count(long count) {

    params.put("count", Lists.newArrayList(String.valueOf(count)));
    return this;
  }

  MultiValueMapBuilder startIndex(long startIndex) {

    params.put("startIndex", Lists.newArrayList(String.valueOf(startIndex)));
    return this;
  }

  MultiValueMapBuilder userId(String userId) {

    params.put("userId", Lists.newArrayList(userId));
    return this;
  }

  MultiValueMapBuilder clientId(String clientId) {

    params.put("clientId", Lists.newArrayList(clientId));
    return this;
  }

  MultiValueMapBuilder attributes(String value) {

    params.put("attributes", Lists.newArrayList(value));
    return this;
  }

  MultiValueMap<String, String> build() {

    return params;
  }
}
