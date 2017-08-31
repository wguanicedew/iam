package it.infn.mw.iam.audit.utils;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamX509Certificate;

public class IamX509CertificateSerializer extends JsonSerializer<Collection<IamX509Certificate>> {

  @Override
  public void serialize(Collection<IamX509Certificate> value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException, JsonProcessingException {

    gen.writeStartArray();
    for (IamX509Certificate elem : value) {
      gen.writeStartObject();
      gen.writeStringField("label", elem.getLabel());
      gen.writeStringField("subjectDn", elem.getSubjectDn());
      gen.writeStringField("issuerDn", elem.getIssuerDn());
      gen.writeStringField("certificate", elem.getCertificate());
      gen.writeEndObject();
    }
    gen.writeEndArray();
  }

}
