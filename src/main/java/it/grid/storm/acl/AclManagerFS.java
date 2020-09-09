/**
 * 
 */
package it.grid.storm.acl;

import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.LocalUser;

/**
 * @author Michele Dibenedetto
 * 
 */
public class AclManagerFS implements AclManager {

  private static AclManagerFS instance = new AclManagerFS();

  private AclManagerFS() {

  }

  /**
   * @return
   */
  public static AclManager getInstance() {

    return instance;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.grid.storm.acl.AclManager#grantGroupPermission(it.grid.storm.griduser .LocalUser,
   * it.grid.storm.filesystem.FilesystemPermission)
   */
  @Override
  public FilesystemPermission grantGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException {

    if (localFile == null || localUser == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser + " permission=" + permission);
    }
    if (!localFile.exists()) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received file parameter points to a non existent file");
    }
    FilesystemPermission newPermission = localFile.grantGroupPermission(localUser, permission);
    return newPermission;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.grid.storm.acl.AclManager#grantUserPermission(it.grid.storm.filesystem .LocalFile,
   * it.grid.storm.griduser.LocalUser, it.grid.storm.filesystem.FilesystemPermission)
   */
  @Override
  public FilesystemPermission grantUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException {

    if (localFile == null || localUser == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser + " permission=" + permission);
    }
    if (!localFile.exists()) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received file parameter points to a non existent file");
    }
    FilesystemPermission newPermission = localFile.grantUserPermission(localUser, permission);
    return newPermission;
  }

  @Override
  public FilesystemPermission removeGroupPermission(LocalFile localFile, LocalUser localUser)
      throws IllegalArgumentException {

    if (localFile == null || localUser == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser);
    }
    if (!localFile.exists()) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received file parameter points to a non existent file");
    }
    FilesystemPermission newPermission = localFile.removeGroupPermission(localUser);
    return newPermission;
  }

  @Override
  public FilesystemPermission removeUserPermission(LocalFile localFile, LocalUser localUser)
      throws IllegalArgumentException {

    if (localFile == null || localUser == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser);
    }
    if (!localFile.exists()) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received file parameter points to a non existent file");
    }
    FilesystemPermission newPermission = localFile.removeUserPermission(localUser);
    return newPermission;
  }

  @Override
  public FilesystemPermission revokeGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException {

    if (localFile == null || localUser == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser + " permission=" + permission);
    }
    if (!localFile.exists()) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received file parameter points to a non existent file");
    }
    FilesystemPermission newPermission = localFile.revokeGroupPermission(localUser, permission);
    return newPermission;
  }

  @Override
  public FilesystemPermission revokeUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException {

    if (localFile == null || localUser == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser + " permission=" + permission);
    }
    if (!localFile.exists()) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received file parameter points to a non existent file");
    }
    FilesystemPermission newPermission = localFile.revokeUserPermission(localUser, permission);
    return newPermission;
  }

  @Override
  public FilesystemPermission setGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException {

    if (localFile == null || localUser == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser + " permission=" + permission);
    }
    if (!localFile.exists()) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received file parameter points to a non existent file");
    }
    FilesystemPermission newPermission = localFile.setGroupPermission(localUser, permission);
    return newPermission;
  }

  @Override
  public FilesystemPermission setUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException {

    if (localFile == null || localUser == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser + " permission=" + permission);
    }
    if (!localFile.exists()) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received file parameter points to a non existent file");
    }
    FilesystemPermission newPermission = localFile.setUserPermission(localUser, permission);
    return newPermission;
  }

  @Override
  public void removeHttpsPermissions(LocalFile localFile) throws IllegalArgumentException {

    if (localFile == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received file parameter is null");
    }
  }

  @Override
  public void grantHttpsUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException {

    if (localFile == null || localUser == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser + " permission=" + permission);
    }
  }

  @Override
  public void grantHttpsServiceUserPermission(LocalFile localFile, FilesystemPermission permission)
      throws IllegalArgumentException {

    if (localFile == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " permission=" + permission);
    }
  }

  @Override
  public void grantHttpsGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException {

    if (localFile == null || localUser == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " localUser=" + localUser + " permission=" + permission);
    }
  }

  @Override
  public void grantHttpsServiceGroupPermission(LocalFile localFile, FilesystemPermission permission)
      throws IllegalArgumentException {

    if (localFile == null || permission == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: localFile=" + localFile
              + " permission=" + permission);
    }
  }

  @Override
  public void moveHttpsPermissions(LocalFile fromLocalFile, LocalFile toLocalFile)
      throws IllegalArgumentException {

    if (fromLocalFile == null || toLocalFile == null) {
      throw new IllegalArgumentException(
          "Unable to perform the operation. The received null parameters: fromLocalFile="
              + fromLocalFile + " toLocalFile=" + toLocalFile);
    }
  }

}
