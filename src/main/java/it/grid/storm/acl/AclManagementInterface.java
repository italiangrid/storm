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

public interface AclManagementInterface {

	/**
	 * Grants the provided permission on the provided file to the provided group
	 * 
	 * @param localFile
	 * @param localUser
	 *          a local user representing a group on the operating system
	 * @param permission
	 */
	void grantGroupPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission);

	/**
	 * Grants the provided permission on the provided file to the provided user
	 * 
	 * @param localFile
	 * @param localUser
	 *          a local user representing an user on the operating system
	 * @param permission
	 */
	void grantUserPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission);

	/**
	 * Removes all the permission eventually assigned to the provided group on the
	 * provided file
	 * 
	 * @param localFile
	 *          a local user representing a group on the operating system
	 * @param localUser
	 */
	void removeGroupPermission(LocalFile localFile, LocalUser localUser);

	/**
	 * Removes all the permission eventually assigned to the provided user on the
	 * provided file
	 * 
	 * @param localFile
	 * @param localUser
	 *          a local user representing an user on the operating system
	 */
	void removeUserPermission(LocalFile localFile, LocalUser localUser);

	/**
	 * Revokes the provided permission on the provided file to the provided group
	 * 
	 * @param localFile
	 * @param localUser
	 *          a local user representing a group on the operating system
	 * @param permission
	 */
	void revokeGroupPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission);

	/**
	 * Revokes the provided permission on the provided file to the provided user
	 * 
	 * @param localFile
	 * @param localUser
	 *          a local user representing an user on the operating system
	 * @param permission
	 */
	void revokeUserPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission);

	/**
	 * Sets the provided permission on the provided file to the provided group
	 * 
	 * @param localFile
	 * @param localUser
	 *          a local user representing a group on the operating system
	 * @param permission
	 */
	void setGroupPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission);

	/**
	 * Sets the provided permission on the provided file to the provided user
	 * 
	 * @param localFile
	 * @param localUser
	 *          a local user representing an user on the operating system
	 * @param permission
	 */
	void setUserPermission(LocalFile localFile, LocalUser localUser,
		FilesystemPermission permission);

	/**
	 * Removes all the permission from any user/group from the provided file
	 * 
	 * @param localFile
	 */
	void removeAllPermissions(LocalFile localFile);

	/**
	 * Moves all the permission from any user/group from the provided
	 * fromLocalFile to the new toLocalFile (NOTE: can be assumed that toLocalFile
	 * has no ACL)
	 * 
	 * @param fromLocalFile
	 * @param toLocalFile
	 */
	void moveAllPermissions(LocalFile fromLocalFile, LocalFile toLocalFile);

}
