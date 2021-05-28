/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package it.grid.storm.acl;

import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.LocalUser;

/**
 * @author Michele Dibenedetto
 * 
 */

public interface AclManager {

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *         a not existent file
   */
  FilesystemPermission grantGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException;

  /**
   * @param localFile an existent file if received null parameters or the LocalFile object refers to
   *        a not existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *         a not existent file
   */
  FilesystemPermission grantUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *         a not existent file
   */
  FilesystemPermission removeGroupPermission(LocalFile localFile, LocalUser localUser)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *         a not existent file
   */
  FilesystemPermission removeUserPermission(LocalFile localFile, LocalUser localUser)
      throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *         a not existent file
   */
  FilesystemPermission revokeGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *         a not existent file
   */
  FilesystemPermission revokeUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *         a not existent file
   */
  FilesystemPermission setGroupPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException;

  /**
   * @param localFile an existent file
   * @param localUser
   * @param permission
   * @return
   * @throws IllegalArgumentException if received null parameters or the LocalFile object refers to
   *         a not existent file
   */
  FilesystemPermission setUserPermission(LocalFile localFile, LocalUser localUser,
      FilesystemPermission permission) throws IllegalArgumentException;

}
