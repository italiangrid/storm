/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.acl;

import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.LocalUser;

/** @author Michele Dibenedetto */
public interface AclManager {

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  FilesystemPermission grantGroupPermission(
      LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file if received null parameters or the LocalFile object refers to
   *     a not existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  FilesystemPermission grantUserPermission(
      LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  FilesystemPermission removeGroupPermission(LocalFile localFile, LocalUser localUser)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  FilesystemPermission removeUserPermission(LocalFile localFile, LocalUser localUser)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  FilesystemPermission revokeGroupPermission(
      LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  FilesystemPermission revokeUserPermission(
      LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  FilesystemPermission setGroupPermission(
      LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  FilesystemPermission setUserPermission(
      LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  void removeHttpsPermissions(LocalFile localFile) throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  void grantHttpsUserPermission(
      LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  void grantHttpsServiceGroupPermission(LocalFile localFile, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  void grantHttpsGroupPermission(
      LocalFile localFile, LocalUser localUser, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *     a not existent file
   */
  void grantHttpsServiceUserPermission(LocalFile localFile, FilesystemPermission permission)
      throws IllegalArgumentException;

  /**
   * @param oldLocalFile an existent source file
   * @param newLocalFile an existent destination file
   * @throws IllegalArgumentException if received null parameters or the LocalFile objects refers to
   *     not existent files
   */
  void moveHttpsPermissions(LocalFile oldLocalFile, LocalFile newLocalFile)
      throws IllegalArgumentException;
}
