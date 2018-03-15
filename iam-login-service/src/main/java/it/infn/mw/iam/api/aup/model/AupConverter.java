package it.infn.mw.iam.api.aup.model;

import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.Converter;
import it.infn.mw.iam.persistence.model.IamAup;

@Service
public class AupConverter implements Converter<AupDTO, IamAup> {

  @Override
  public IamAup entityFromDto(AupDTO dto) {
    IamAup aup = new IamAup();
    aup.setCreationTime(dto.getCreationTime());
    aup.setDescription(dto.getDescription());
    aup.setLastUpdateTime(dto.getLastUpdateTime());
    aup.setSignatureValidityInDays(dto.getSignatureValidityInDays());
    aup.setText(dto.getText());
    return aup;
  }

  @Override
  public AupDTO dtoFromEntity(IamAup entity) {
    return new AupDTO(entity.getText(), entity.getDescription(),
        entity.getSignatureValidityInDays(), entity.getCreationTime(), entity.getLastUpdateTime());
  }

}
