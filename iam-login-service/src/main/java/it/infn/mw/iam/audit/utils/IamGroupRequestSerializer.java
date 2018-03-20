package it.infn.mw.iam.audit.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamGroupRequest;

public class IamGroupRequestSerializer extends JsonSerializer<IamGroupRequest> {

  @Override
  public void serialize(IamGroupRequest value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException, JsonProcessingException {

    gen.writeStartObject();
    gen.writeStringField("uuid", value.getUuid());
    gen.writeStringField("username", value.getAccount().getUsername());
    gen.writeStringField("groupName", value.getGroup().getName());
    gen.writeStringField("status", value.getStatus().name());
    gen.writeEndObject();
  }

}
