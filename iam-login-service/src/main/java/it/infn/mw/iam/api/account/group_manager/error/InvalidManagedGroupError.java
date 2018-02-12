package it.infn.mw.iam.api.account.group_manager.error;

public class InvalidManagedGroupError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private InvalidManagedGroupError(String message) {
    super(message);
  }


  public static InvalidManagedGroupError groupNotFoundException(String groupUuid) {
    return new InvalidManagedGroupError(String.format("Group '%s' not found", groupUuid));
  }

  public static InvalidManagedGroupError groupNotManagedException(String groupUuid,
      String username) {
    return new InvalidManagedGroupError(String.format(
        "Group '%s' is not in the list of managed groups for user '%s'", groupUuid, username));
  }

  public static InvalidManagedGroupError groupAlreadyManagedException(String groupUuid,
      String username) {
    return new InvalidManagedGroupError(String.format(
        "Group '%s' is already in the list of managed groups for user '%s'", groupUuid, username));
  }
}
