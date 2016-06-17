package it.infn.mw.iam.api.scim.controller.utils;

import java.io.IOException;
import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonDateSerializer extends JsonSerializer<Date> {

  private final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();

  @Override
  public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException, JsonProcessingException {
    gen.writeString(dateTimeFormatter.print(value.getTime()));
  }


}
