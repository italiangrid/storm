/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.filesystem;

import it.grid.storm.griduser.LocalUser;

public interface FilesystemIF {

  long FS_BLOCK_SIZE = 512;

  /** Get file size in bytes. Same as calling {@link java.io.File#length()}. */
  long getSize(String file);

  /**
   * Get file last modification time, as a UNIX epoch. Same as calling {@link
   * java.io.File#lastModified()}.
   */
  long getLastModifiedTime(String fileOrDirectory);

  /**
   * Get up-to-date file size in bytes. Returned value may differ from the size returned by {@link
   * java.io.File#length()} on filesystems that do metadata caching (GPFS, for instance). Since it
   * may force a metadata update on all cluster nodes, this method may be <em>slow</em>.
   */
  long getExactSize(String file);

  /**
   * Get up-to-date file last modification time, as a UNIX epoch. Returned value may differ from the
   * size returned by {@link java.io.File#lastModified()} on filesystems that do metadata caching
   * (GPFS, for instance). Since it may force a metadata update on all cluster nodes, this method
   * may be <em>slow</em>.
   */
  long getExactLastModifiedTime(String fileOrDirectory);

  /**
   * Truncate the specified file to the desired size
   *
   * @param filename @param desired_size @return
   */
  int truncateFile(String filename, long desired_size);

  /**
   * Returns true if file is on disk, false otherwise
   *
   * @param filename @return
   */
  boolean isFileOnDisk(String filename);

  /**
   * Returns a file block size
   *
   * @param filename @return
   */
  long getFileBlockSize(String filename);

  /**
   * Changes the group ownership for a file
   *
   * @param filename @param groupName
   */
  void changeFileGroupOwnership(String filename, String groupName);

  /**
   * Return available space (in bytes) on filesystem. Please note that this value may be inaccurate
   * on cluster/networked filesystems, due to metadata caching.
   */
  long getFreeSpace();

  /**
   * Return <code>true</code> if the local user <i>u</i> can operate on the specified
   * <i>fileOrDirectory</i> in the mode given by <i>accessMode</i>, according to the permissions set
   * on the filesystem.
   *
   * <p>Suppose a local UNIX user (identified by UID and the list of primary and supplementary GIDs)
   * requests access to a certain file or directory. Roughly, the access control algorithm is:
   *
   * <p>- if the requestor's UID matches the UID in the @em owner entry, then the @em owner
   * permissions are used;
   *
   * <p>- else if the requestor's UID matches a UID in a <em>specific user</em> entry, then the
   * bitwise-AND of that entry's permissions and the @em mask entry permissions are used;
   *
   * <p>- else if any of the requestor's GIDs (primary or supplementary) matches the group owner
   * entry, the bitwise-AND of that entry's permissions and the @em mask entry permissions are used;
   *
   * <p>- else if any of the requestor's GIDs (primary or supplementary) matches the group owner
   * entry, the bitwise-AND of that entry's permissions and the @em mask entry permissions are used;
   *
   * <p>- else, (if no group entry was found to match) the @em other entry permissions are used.
   *
   * @see fs_acl#access() @sa Linux man page acl(5)
   */
  boolean canAccess(LocalUser u, String fileOrDirectory, FilesystemPermission accessMode);

  /**
   * Return the <em>effective</em> permission a group has on the given file or directory.
   *
   * <p>Loads the ACL for the given file or directory, and return the permission associated with the
   * primary group of the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i>. If no
   * entry for that group is found, return <code>null</code>.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose primary GID's
   *     permissions are to be retrieved.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     named group permissions.
   * @return permission associated to the primary GID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that group was found.
   */
  FilesystemPermission getEffectiveGroupPermission(LocalUser u, String fileOrDirectory);

  /**
   * Return the <em>effective</em> permission a user has on the given file or directory.
   *
   * <p>Loads the ACL for the given file or directory, and return the permission associated with the
   * UID of the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i>. If no entry for
   * that user is found, return <code>null</code>.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose permissions
   *     are to be retrieved.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     given user permissions.
   * @return permission associated to the UID of the given {@link LocalUser} instance <i>u</i> in
   *     the given file ACL, or <code>null</code> if no entry for that user was found.
   */
  FilesystemPermission getEffectiveUserPermission(LocalUser u, String fileOrDirectory);

  /**
   * Return the permission a group has on the given file or directory.
   *
   * <p>Loads the ACL for the given file or directory, and return the permission associated with the
   * primary group of the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i>. If no
   * entry for that group is found, return <code>null</code>.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose primary GID's
   *     permissions are to be retrieved.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     named group permissions.
   * @return permission associated to the primary GID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that group was found.
   */
  FilesystemPermission getGroupPermission(LocalUser u, String fileOrDirectory);

  /**
   * Return the permission a user has on the given file or directory.
   *
   * <p>Loads the ACL for the given file or directory, and return the permission associated with the
   * UID of the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i>. If no entry for
   * that user is found, return <code>null</code>.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose permissions
   *     are to be retrieved.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     given user permissions.
   * @return permission associated to the UID of the given {@link LocalUser} instance <i>u</i> in
   *     the given file ACL, or <code>null</code> if no entry for that user was found.
   */
  FilesystemPermission getUserPermission(LocalUser u, String fileOrDirectory);

  /**
   * Grant specified permission to a group on a file or directory, and return the former permission.
   *
   * <p>Adds the specified permission to the ones that the primary group of the given {@link
   * it.grid.storm.griduser.LocalUser} instance <i>u</i> already holds on the given file or
   * directory: all permission bits that are set in <i>permission</i> will be set in the appropriate
   * group entry in the file ACL.
   *
   * <p>If no entry is present for the specified group, then one is created and its permission value
   * is set to <i>permission</i>.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose primary GID's
   *     entry is to be altered.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     given group permissions
   * @param permission Capabilities to grant.
   * @return permission formerly associated to the primary GID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that group was found.
   * @see fs_acl::grant_group_perm()
   */
  FilesystemPermission grantGroupPermission(
      LocalUser u, String fileOrDirectory, FilesystemPermission permission);

  /**
   * Grant specified permission to a user on a file or directory, and return the former permission.
   *
   * <p>Adds the specified permissions to the ones that the UID of the given {@link
   * it.grid.storm.griduser.LocalUser} instance <i>u</i> already holds on the given file or
   * directory: all permission bits that are set in <i>permission</i> will be set in the appropriate
   * user entry in the file ACL.
   *
   * <p>If no entry is present for the specified user, then one is created and its permission value
   * is set to <i>permission</i>.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose UID's entry
   *     is to be altered.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     given user permissions
   * @param permission Capabilities to grant.
   * @return permission formerly associated to the UID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that user was found.
   * @see fs_acl::grant_user_perm()
   */
  FilesystemPermission grantUserPermission(
      LocalUser u, String fileOrDirectory, FilesystemPermission permission);

  /**
   * Remove a group's entry from a file or directory ACL, and return the (now deleted) permission.
   *
   * <p>Removes the entry (if any) of the primary group of the given {@link
   * it.grid.storm.griduser.LocalUser} instance <i>u</i> from the given file or directory ACL.
   * Returns the permission formerly associated with that group.
   *
   * @todo would this be the correct behaviuor: if the given group is the file owning group, then
   *     its entry is set to {@link #NONE}, rather than removed.
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose primary GID's
   *     entry is to be altered.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     given group permissions
   * @return permission formerly associated to the primary GID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that group was found.
   * @see fs_acl::remove_group_perm_not_owner()
   */
  FilesystemPermission removeGroupPermission(LocalUser u, String fileOrDirectory);

  /**
   * Remove a user's entry from a file or directory ACL, and return the (now deleted) permission.
   *
   * <p>Removes the entry (if any) associated with the UID of the given {@link
   * it.grid.storm.griduser.LocalUser} instance <i>u</i> from the given file or directory ACL.
   * Returns the permission formerly associated with that user.
   *
   * @todo would this be the correct behaviour: if the given user is the file owner, then its entry
   *     is set to {@link #NONE}, rather than removed.
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose UID's entry
   *     is to be altered.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     named user permissions
   * @return permission formerly associated to the UID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that user was found.
   * @see fs_acl::remove_user_perm_not_owner()
   */
  FilesystemPermission removeUserPermission(LocalUser u, String fileOrDirectory);

  /**
   * Revoke specified permission from a group's entry on a file or directory, and return the former
   * permission.
   *
   * <p>Removes the specified permission from the ones that the primary group of the given {@link
   * it.grid.storm.griduser.LocalUser} instance <i>u</i> already holds on the given file or
   * directory: all permission bits that are <em>set</em> in <i>permission</i> will be
   * <em>cleared</em> in the appropriate group entry in the file ACL.
   *
   * <p>If no entry is present for the specified group, then one is created and its permission value
   * is set to {@link #NONE}.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose primary GID's
   *     entry is to be altered.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     named group permissions
   * @param permission Capabilities to revoke.
   * @return permission formerly associated with the primary GID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that group was found.
   * @see fs_acl::revoke_group_perm()
   */
  FilesystemPermission revokeGroupPermission(
      LocalUser u, String fileOrDirectory, FilesystemPermission permission);

  /**
   * Revoke specified permission from a user's entry on a file or directory, and return the former
   * permission.
   *
   * <p>Removes the specified permission from the ones that the primary user of the given {@link
   * it.grid.storm.griduser.LocalUser} instance <i>u</i> already holds on the given file or
   * directory: all permission bits that are <em>set</em> in <i>permission</i> will be
   * <em>cleared</em> in the appropriate user entry in the file ACL.
   *
   * <p>If no entry is present for the specified user, then one is created and its permission value
   * is set to {@link #NONE}.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose UID's entry
   *     is to be altered.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     named user permissions
   * @param permission Capabilities to revoke.
   * @return permission formerly associated with the UID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that user was found.
   * @see fs_acl::revoke_user_perm()
   */
  FilesystemPermission revokeUserPermission(
      LocalUser u, String fileOrDirectory, FilesystemPermission permission);

  /**
   * Set the specified permission in a group's entry on a file or directory, and return the former
   * permission.
   *
   * <p>Sets the entry of the primary group of the given {@link LocalUser} instance <i>u</i> to the
   * given <i>permission</i>. Returns the permission formerly associated with that group.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose primary GID's
   *     entry is to be altered.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     named group permissions
   * @param permission Permission to set in the group entry.
   * @return permission formerly associated with the primary GID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that group was found.
   * @see fs_acl::set_group_perm()
   */
  FilesystemPermission setGroupPermission(
      LocalUser u, String fileOrDirectory, FilesystemPermission permission);

  /**
   * Set the specified permission in a user's entry on a file or directory, and return the former
   * permission.
   *
   * <p>Sets the entry of the primary user of the given {@link LocalUser} instance <i>u</i> to the
   * given <i>permission</i>. Returns the permission formerly associated with that user.
   *
   * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose UID's entry
   *     is to be altered.
   * @param fileOrDirectory pathname to the file or directory whose ACL is to be searched for the
   *     named user permissions
   * @param permission Permission to set in the user entry.
   * @return permission formerly associated with the UID of the given {@link
   *     it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or <code>null
   *     </code> if no entry for that user was found.
   * @see fs_acl::set_user_perm()
   */
  FilesystemPermission setUserPermission(
      LocalUser u, String fileOrDirectory, FilesystemPermission permission);
}
