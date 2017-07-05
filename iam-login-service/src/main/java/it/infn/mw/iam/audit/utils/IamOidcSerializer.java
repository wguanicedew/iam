package it.infn.mw.iam.audit.utils;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamOidcId;

public class IamOidcSerializer extends JsonSerializer<Collection<IamOidcId>> {

  @Override
  public void serialize(Collection<IamOidcId> value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException, JsonProcessingException {
    gen.writeStartArray();
    for (IamOidcId elem : value) {
      gen.writeStartObject();
      gen.writeStringField("issuer", elem.getIssuer());
      gen.writeStringField("subject", elem.getSubject());
      gen.writeEndObject();
    }
    gen.writeEndArray();
  }

}
