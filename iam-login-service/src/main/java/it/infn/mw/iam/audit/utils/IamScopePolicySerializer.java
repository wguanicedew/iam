package it.infn.mw.iam.audit.utils;

import static java.util.Objects.isNull;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamScopePolicy;

public class IamScopePolicySerializer extends JsonSerializer<IamScopePolicy>{

  @Override
  public void serialize(IamScopePolicy value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    
    gen.writeStartObject();
    if (isNull(value.getId())){
      gen.writeNullField("id");
    }else {
      gen.writeNumberField("id", value.getId());
    }
    
    gen.writeStringField("description", value.getDescription());
    gen.writeStringField("rule", value.getRule().name());
    gen.writeObjectField("account", value.getAccount());
    gen.writeObjectField("group", value.getGroup());
    
    gen.writeArrayFieldStart("scopes");
    for (String s: value.getScopes()){
      gen.writeString(s);
    }
    gen.writeEndArray();
    
    gen.writeEndObject();
  }

  

}
