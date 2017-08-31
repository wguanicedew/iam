package it.infn.mw.iam.audit.utils;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamGroup;

public class IamGroupCollectionSerializer extends JsonSerializer<Collection<IamGroup>> {

  @Override
  public void serialize(Collection<IamGroup> value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException, JsonProcessingException {

    gen.writeStartArray();
    for (IamGroup elem : value) {
      gen.writeStartObject();
      gen.writeStringField("uuid", elem.getUuid());
      gen.writeStringField("name", elem.getName());
      gen.writeEndObject();
    }
    gen.writeEndArray();
  }

}
