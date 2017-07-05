package it.infn.mw.iam.audit.utils;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamSamlId;

public class IamSamlSerializer extends JsonSerializer<Collection<IamSamlId>> {

  @Override
  public void serialize(Collection<IamSamlId> value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException, JsonProcessingException {

    gen.writeStartArray();
    for (IamSamlId elem : value) {
      gen.writeStartObject();
      gen.writeStringField("idpid", elem.getIdpId());
      gen.writeStringField("userid", elem.getUserId());
      gen.writeEndObject();
    }
    gen.writeEndArray();
  }

}
