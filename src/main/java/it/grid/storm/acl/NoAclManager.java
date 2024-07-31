package it.grid.storm.acl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.LocalUser;

public class NoAclManager implements AclManager {

  @Override
  public FilesystemPermission grantGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to grant group permission on null local file");
    checkNotNull(localUser, "Unable to grant group permission on null local user");
    checkNotNull(permission, "Unable to grant group permission with null permission");
    checkArgument(localFile.exists(),
        "Unable to grant group permission on a non existent local file: "
            + localFile.getAbsolutePath());

    return localFile.getEffectiveGroupPermission(localUser);
  }

  @Override
  public FilesystemPermission grantUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to grant user permission on null local file");
    checkNotNull(localUser, "Unable to grant user permission on null local user");
    checkNotNull(permission, "Unable to grant user permission with null permission");
    checkArgument(localFile.exists(),
        "Unable to grant user permission on a non existent local file: "
            + localFile.getAbsolutePath());

    return localFile.getEffectiveUserPermission(localUser);
  }

  @Override
  public FilesystemPermission removeGroupPermission(LocalFile localFile, LocalUser localUser) {

    checkNotNull(localFile, "Unable to remove group permission on null local file");
    checkNotNull(localUser, "Unable to remove group permission on null local user");
    checkArgument(localFile.exists(),
        "Unable to remove group permission on a non existent local file: "
            + localFile.getAbsolutePath());

    return localFile.getEffectiveGroupPermission(localUser);
  }

  @Override
  public FilesystemPermission removeUserPermission(LocalFile localFile, LocalUser localUser) {

    checkNotNull(localFile, "Unable to remove user permission on null local file");
    checkNotNull(localUser, "Unable to remove user permission on null local user");
    checkArgument(localFile.exists(),
        "Unable to remove user permission on a non existent local file: "
            + localFile.getAbsolutePath());

    return localFile.getEffectiveUserPermission(localUser);
  }

  @Override
  public FilesystemPermission revokeGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to revoke group permission on null local file");
    checkNotNull(localUser, "Unable to revoke group permission on null local user");
    checkNotNull(permission, "Unable to revoke group permission with null permission");
    checkArgument(localFile.exists(),
        "Unable to revoke group permission on a non existent local file: "
            + localFile.getAbsolutePath());

    return localFile.getEffectiveGroupPermission(localUser);
  }

  @Override
  public FilesystemPermission revokeUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to revoke user permission on null local file");
    checkNotNull(localUser, "Unable to revoke user permission on null local user");
    checkNotNull(permission, "Unable to revoke user permission with null permission");
    checkArgument(localFile.exists(),
        "Unable to revoke user permission on a non existent local file: "
            + localFile.getAbsolutePath());

    return localFile.getEffectiveUserPermission(localUser);
  }

  @Override
  public FilesystemPermission setGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to set group permission on null local file");
    checkNotNull(localUser, "Unable to set group permission on null local user");
    checkNotNull(permission, "Unable to set group permission with null permission");
    checkArgument(localFile.exists(),
        "Unable to set group permission on a non existent local file: "
            + localFile.getAbsolutePath());

    return localFile.getEffectiveGroupPermission(localUser);
  }

  @Override
  public FilesystemPermission setUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to set user permission on null local file");
    checkNotNull(localUser, "Unable to set user permission on null local user");
    checkNotNull(permission, "Unable to set user permission with null permission");
    checkArgument(localFile.exists(), "Unable to set user permission on a non existent local file: "
        + localFile.getAbsolutePath());

    return localFile.getEffectiveUserPermission(localUser);
  }

  @Override
  public void removeHttpsPermissions(LocalFile localFile) {

    checkNotNull(localFile, "Unable to remove https permission on null local file");
    checkArgument(localFile.exists(),
        "Unable to remove httès permission on a non existent local file: "
            + localFile.getAbsolutePath());
  }

  @Override
  public void grantHttpsUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to grant https user permission on null local file");
    checkNotNull(localUser, "Unable to grant https user permission on null local user");
    checkNotNull(permission, "Unable to grant https user permission with null permission");
    checkArgument(localFile.exists(),
        "Unable to grant https user permission on a non existent local file: "
            + localFile.getAbsolutePath());
  }

  @Override
  public void grantHttpsServiceGroupPermission(LocalFile localFile,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to grant https service group permission on null local file");
    checkNotNull(permission, "Unable to grant https service group permission with null permission");
    checkArgument(localFile.exists(),
        "Unable to grant https service group permission on a non existent local file: "
            + localFile.getAbsolutePath());
  }

  @Override
  public void grantHttpsGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to grant https group permission on null local file");
    checkNotNull(localUser, "Unable to grant https group permission on null local user");
    checkNotNull(permission, "Unable to grant https group permission with null permission");
    checkArgument(localFile.exists(),
        "Unable to grant https group permission on a non existent local file: "
            + localFile.getAbsolutePath());
  }

  @Override
  public void grantHttpsServiceUserPermission(LocalFile localFile,
      FilesystemPermission permission) {

    checkNotNull(localFile, "Unable to grant https service user permission on null local file");
    checkNotNull(permission, "Unable to grant https service user permission with null permission");
    checkArgument(localFile.exists(),
        "Unable to grant https service user permission on a non existent local file: "
            + localFile.getAbsolutePath());
  }

  @Override
  public void moveHttpsPermissions(LocalFile oldLocalFile, LocalFile newLocalFile) {

    checkNotNull(oldLocalFile, "Unable to move https permission on null local source file");
    checkNotNull(newLocalFile, "Unable to move https permission on null local destination file");
    checkArgument(oldLocalFile.exists(),
        "Unable to move https permission on a non existent source local file: "
            + oldLocalFile.getAbsolutePath());
    checkArgument(newLocalFile.exists(),
        "Unable to move https permission on a non existent destination local file: "
            + newLocalFile.getAbsolutePath());
  }

}
