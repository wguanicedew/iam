package it.infn.mw.iam.api.scim.updater;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.updater.group.GroupUpdater;
import it.infn.mw.iam.persistence.model.IamGroup;

@Component
public class ScimGroupUpdater {

  private GroupUpdater groupUpdater;

  @Autowired
  public ScimGroupUpdater(GroupUpdater groupUpdater) {

    this.groupUpdater = groupUpdater;
  }

  public void update(IamGroup group, List<ScimPatchOperation<List<ScimMemberRef>>> operations) {

    for (ScimPatchOperation<List<ScimMemberRef>> op : operations) {

      if (!op.getPath().equals("members")) {
        throw new ScimPatchOperationNotSupported("Not supported operation path " + op.getPath());
      }

      switch (op.getOp()) {
        case add:
          groupUpdater.add(group, op.getValue());
          break;
        case remove:
          groupUpdater.remove(group, op.getValue());
          break;
        case replace:
          groupUpdater.replace(group, op.getValue());
          break;
        default:
          throw new ScimPatchOperationNotSupported("Not supported operation type " + op.getOp());
      }
    }
  }
}
