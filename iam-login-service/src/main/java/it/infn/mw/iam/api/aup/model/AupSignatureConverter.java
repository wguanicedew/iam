package it.infn.mw.iam.api.aup.model;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.Converter;
import it.infn.mw.iam.persistence.model.IamAupSignature;


@Service
public class AupSignatureConverter implements Converter<AupSignatureDTO, IamAupSignature> {

  final AupConverter aupConverter;

  @Autowired
  public AupSignatureConverter(AupConverter aupConverter) {
    this.aupConverter = aupConverter;
  }

  @Override
  public IamAupSignature entityFromDto(AupSignatureDTO dto) {
    throw new NotImplementedException();
  }

  @Override
  public AupSignatureDTO dtoFromEntity(IamAupSignature signature) {

    AupSignatureDTO sigDto = new AupSignatureDTO();
    AupDTO aupDto = aupConverter.dtoFromEntity(signature.getAup());
    sigDto.setAup(aupDto);
    AccountDTO accountDto = new AccountDTO();

    accountDto.setName(signature.getAccount().getUserInfo().getName());
    accountDto.setUsername(signature.getAccount().getUsername());
    accountDto.setUuid(signature.getAccount().getUuid());
    sigDto.setAccount(accountDto);
    sigDto.setSignatureTime(signature.getSignatureTime());

    return sigDto;
  }

}
