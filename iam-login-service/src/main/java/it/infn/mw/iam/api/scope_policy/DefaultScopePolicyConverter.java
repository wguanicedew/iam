package it.infn.mw.iam.api.scope_policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import it.infn.mw.iam.api.scim.converter.ScimResourceLocationProvider;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamScope;
import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.model.IamScopePolicy.Rule;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;
import it.infn.mw.iam.persistence.repository.IamScopeRepository;

@Component
public class DefaultScopePolicyConverter implements IamScopePolicyConverter {

  private final ScimResourceLocationProvider resourceLocationProvider;
  private final IamAccountRepository accountRepo;
  private final IamScopeRepository scopeRepo;
  private final IamGroupRepository groupRepo;

  @Autowired
  public DefaultScopePolicyConverter(ScimResourceLocationProvider locationProvider,
      IamAccountRepository accountRepo, IamGroupRepository groupRepo,
      IamScopeRepository scopeRepo) {
    this.resourceLocationProvider = locationProvider;
    this.accountRepo = accountRepo;
    this.groupRepo = groupRepo;
    this.scopeRepo = scopeRepo;
  }

  public ScopePolicyDTO fromModel(IamScopePolicy sp) {
    ScopePolicyDTO dto = new ScopePolicyDTO();

    dto.setId(sp.getId());
    dto.setCreationTime(sp.getCreationTime());
    dto.setLastUpdateTime(sp.getLastUpdateTime());
    dto.setDescription(sp.getDescription());
    dto.setRule(sp.getRule().name());

    if (!sp.getScopes().isEmpty()) {
      dto.setScopes(Sets.newHashSet());
      sp.getScopes().forEach(s -> dto.getScopes().add(s.getScope()));
    }

    if (sp.getAccount() != null) {

      IamAccountRefDTO ar = new IamAccountRefDTO();
      ar.setUuid(sp.getAccount().getUuid());
      ar.setUsername(sp.getAccount().getUsername());
      ar.setLocation(resourceLocationProvider.userLocation(sp.getAccount().getUuid()));

      dto.setAccount(ar);

    }

    if (sp.getGroup() != null) {
      IamGroupRefDTO gr = new IamGroupRefDTO();

      gr.setLocation(resourceLocationProvider.groupLocation(sp.getGroup().getUuid()));
      gr.setName(sp.getGroup().getName());
      gr.setUuid(sp.getGroup().getUuid());

      dto.setGroup(gr);

    }
    return dto;
  }

  public IamScopePolicy toModel(ScopePolicyDTO sp) {

    IamScopePolicy scopePolicy = new IamScopePolicy();

    scopePolicy.setDescription(sp.getDescription());
    scopePolicy.setRule(Rule.valueOf(sp.getRule()));

    if (sp.getScopes() != null) {
      sp.getScopes().forEach(s -> {
        IamScope iamScope = scopeRepo.findByScope(s).orElse(new IamScope(s));
        scopePolicy.getScopes().add(iamScope);
      });
    }

    if (sp.getAccount() != null) {
      IamAccount account = accountRepo.findByUuid(sp.getAccount().getUuid())
        .orElseThrow(() -> new InvalidScopePolicyError(
            "No account found for UUID: " + sp.getAccount().getUuid()));
      scopePolicy.setAccount(account);
    }

    if (sp.getGroup() != null) {
      IamGroup group = groupRepo.findByUuid(sp.getGroup().getUuid()).orElseThrow(
          () -> new InvalidScopePolicyError("No group found for UUID: " + sp.getGroup().getUuid()));

      scopePolicy.setGroup(group);
    }

    return scopePolicy;
  }

}
