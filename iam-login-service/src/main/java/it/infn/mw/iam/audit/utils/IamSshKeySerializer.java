package it.infn.mw.iam.audit.utils;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamSshKey;

public class IamSshKeySerializer extends JsonSerializer<Collection<IamSshKey>> {

  @Override
  public void serialize(Collection<IamSshKey> value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException, JsonProcessingException {

    gen.writeStartArray();
    for (IamSshKey elem : value) {
      gen.writeStartObject();
      gen.writeStringField("label", elem.getLabel());
      gen.writeStringField("fingerprint", elem.getFingerprint());
      gen.writeEndObject();
    }
    gen.writeEndArray();
  }

}
