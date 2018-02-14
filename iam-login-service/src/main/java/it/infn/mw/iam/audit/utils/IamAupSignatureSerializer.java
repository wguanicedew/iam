package it.infn.mw.iam.audit.utils;

import java.io.IOException;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamAupSignature;

public class IamAupSignatureSerializer extends JsonSerializer<IamAupSignature> {

  private final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();

  @Override
  public void serialize(IamAupSignature value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {

    gen.writeStartObject();
    gen.writeNumberField("aupId", value.getAup().getId());
    gen.writeStringField("username", value.getAccount().getUsername());
    gen.writeStringField("signatureTime",
        dateTimeFormatter.print(value.getSignatureTime().getTime()));
    gen.writeEndObject();
  }
  
}
