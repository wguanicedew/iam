package it.infn.mw.iam.test.oauth.scope;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.audit.utils.IamScopePolicySerializer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.test.repository.ScopePolicyTestUtils;

@RunWith(JUnit4.class)
@Transactional
public class ScopePolicyJacksonSerializerTests extends ScopePolicyTestUtils {

  public static final String REPR1 =
      "{\"id\":null,\"description\":null,\"rule\":\"PERMIT\",\"account\":null,\"group\":null,\"scopes\":[]}";

  public static final String REPR2 = 
      "{\"id\":null,\"description\":null,\"rule\":\"PERMIT\",\"account\":{\"uuid\":\"f5f009ca-30b6-48d1-bb6a-b976b1a95525\",\"name\":\"test\"},\"group\":null,\"scopes\":[]}";

  @Test
  public void testJacksonSerializationHandlesNull() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    Writer jsonWriter = new StringWriter();
    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
    SerializerProvider serializerProvider = mapper.getSerializerProvider();

    IamScopePolicySerializer serializer = new IamScopePolicySerializer();
    IamScopePolicy p = initPermitScopePolicy();

    serializer.serialize(p, jsonGenerator, serializerProvider);
    jsonGenerator.flush();

    assertThat(jsonWriter.toString(), is(REPR1));
  }

  @Test
  public void testJacksonSerialization() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    Writer jsonWriter = new StringWriter();
    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
    SerializerProvider serializerProvider = mapper.getSerializerProvider();

    IamScopePolicySerializer serializer = new IamScopePolicySerializer();
    IamScopePolicy p = initPermitScopePolicy();

    IamAccount account = new IamAccount();
    account.setUuid("f5f009ca-30b6-48d1-bb6a-b976b1a95525");
    account.setUsername("test");

    p.setAccount(account);

    serializer.serialize(p, jsonGenerator, serializerProvider);
    jsonGenerator.flush();

    assertThat(jsonWriter.toString(), is(REPR2));
  }

}
