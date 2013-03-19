/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * @file   Filesystem.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * The it.grid.storm.filesystem.Filesystem class
 */
/*
 * Copyright (c) 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms
 * as StoRM itself.
 */

package it.grid.storm.filesystem;



import it.grid.storm.filesystem.swig.fs_acl;
import it.grid.storm.filesystem.swig.genericfs;
import it.grid.storm.griduser.LocalUser;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;


/**
   Façade and base class for filesystem manipulation.

   This class implements methods for manipulation of filesystem
   entries.  You should not use this class directly in StoRM code from
   outside this package, rather manipulate the filesystem in StoRM
   through the {@link File} and {@link Space} interfaces: this class
   is just a thin wrapper around low-level filesystem calls.

   This class should be instanciated by giving a fs::genericfs
   subclass instance that is shall be used for all filesystem
   operations.  Clearly, only the configuration mechanism knows which
   filesystem-type has been configured on a certain path, so this
   class should only be instanciated at stratup/configuration time.

   Not only, can an instance of this class be shared among different
   objects operating on the same filesystem (or portion of it), but it
   actually @em should, because of shared ACL locking (see
   "Implementation Notes").


   <b>Implementation notes</b>

   This class tries to consistently use:

   - <code>String</code> for path names.

   - <code>long</code> for sizes; sizes are always expressed in
     bytes.

   - {@link FilesystemPermission} for expressing permissions that a
     user has on a file;

   - {@link it.grid.storm.griduser.LocalUser} for representing user
     credentials.

   This class serializes files access in the ACL manipulation methods,
   because the POSIX ACL manipulation API only allows for getting or
   setting the <em>whole</em> list of permissions; no changes of
   individual permissions can be performed.  Therefore, no two threads
   can concurrently manipulate ACLs on the same file, or they may
   overwrite each other's changes.

   A per-filename lock is maintained, and no two threads can
   concurrently modify ACL on the same pathname.  Modification by
   other programs (that is, outside StoRM) cannot be prevented, so we
   still have a race condition.

   @author Riccardo Murri <riccardo.murri@ictp.it>
   @version $Revision: 1.20 $

 **/
public class Filesystem {

	
	public static final long FS_BLOCK_SIZE = 512;

    // --- private instance variables ---

    /** Low-level filesystem interface.*/
    private final genericfs fs;

    /** Cache of lock instances. */
    private static AclLockPool locks = new AclLockPool();

    private final Logger log;

    private GetGroupPermissionMethod getGroupPermissionMethod = new GetGroupPermissionMethod();
    private GetUserPermissionMethod getUserPermissionMethod = new GetUserPermissionMethod();

    private GetEffectiveGroupPermissionMethod getEffectiveGroupPermissionMethod = new GetEffectiveGroupPermissionMethod();
    private GetEffectiveUserPermissionMethod getEffectiveUserPermissionMethod = new GetEffectiveUserPermissionMethod();

    private GrantGroupPermissionMethod grantGroupPermissionMethod = new GrantGroupPermissionMethod();
    private GrantUserPermissionMethod grantUserPermissionMethod = new GrantUserPermissionMethod();

    private RemoveGroupPermissionMethod removeGroupPermissionMethod = new RemoveGroupPermissionMethod();
    private RemoveUserPermissionMethod removeUserPermissionMethod = new RemoveUserPermissionMethod();

    private RevokeGroupPermissionMethod revokeGroupPermissionMethod = new RevokeGroupPermissionMethod();
    private RevokeUserPermissionMethod revokeUserPermissionMethod = new RevokeUserPermissionMethod();

    private SetGroupPermissionMethod setGroupPermissionMethod = new SetGroupPermissionMethod();
    private SetUserPermissionMethod setUserPermissionMethod = new SetUserPermissionMethod();

    // --- constructor ---

    /** Constructor, taking native low-level filesystem interface object. */
    public Filesystem(final genericfs nativeFs) {
        assert (null != nativeFs) : "Null nativeFs in Filesystem(NativeFilesystemInterface) constructor";

        fs = nativeFs;
        log  = LoggerFactory.getLogger(Filesystem.class);

        getGroupPermissionMethod = new GetGroupPermissionMethod();
        getUserPermissionMethod = new GetUserPermissionMethod();

        grantGroupPermissionMethod = new GrantGroupPermissionMethod();
        grantUserPermissionMethod = new GrantUserPermissionMethod();

        revokeGroupPermissionMethod = new RevokeGroupPermissionMethod();
        revokeUserPermissionMethod = new RevokeUserPermissionMethod();

        setGroupPermissionMethod = new SetGroupPermissionMethod();
    }








    /**
       @defgroup fs_fileops Low-level File and Directory Operations

       Methods here come in two variants: the "exact" one which tries
       to return up-to-date information, and the "standard" one, which
       uses common POSIX system calls to get the required info.

       The "exact" variant may force a cluster-wide metadata update,
       so it may result in performance degradation if used
       frerquently.

       Motivation for this comes from the GPFS filesystem: GPFS nodes
       perform file metadata and attribute caching, so they may return
       out-of-date results to the standard system calls.  The GPFS API
       provides the corresponding "exact" calls.

       In the generic POSIX filesystem implementation, there's no
       difference between the "exact" and the "standard" call.

       @{
     **/


    /** Get file size in bytes.  Same as calling {@link
     * java.io.File#length()}.
     */
    public long getSize(final String file) {

        return fs.get_size(file);
    }
    
    /** Get file last modification time, as a UNIX epoch.  Same as
     *  calling {@link java.io.File#lastModified()}.
     */
    public long getLastModifiedTime(final String fileOrDirectory) {

        /**
         * Since the lastModificationTime can be retrieved by Java.io.File
         * we prefer to don't use the native driver for get this information.
         * @todo
         * This should be done in the JAVA side of each driver, since
         * this solution will not work in situation (as Amazon s3) for which
         * a native JVM support does not exists.
         * 
         */

        File fileOrDir = new File(fileOrDirectory);
        return fileOrDir.lastModified();

    }

    /** Get up-to-date file size in bytes.  Returned value may differ
     *  from the size returned by {@link java.io.File#length()} on
     *  filesystems that do metadata caching (GPFS, for instance).
     *  Since it may force a metadata update on all cluster nodes,
     *  this method may be <em>slow</em>.
     */
    public long getExactSize(final String file) {
        File fileOrDir = new File(file);
        return fileOrDir.length();
    }

    /** Get up-to-date file last modification time, as a UNIX epoch.
     *  Returned value may differ from the size returned by {@link
     *  java.io.File#lastModified()} on filesystems that do metadata
     *  caching (GPFS, for instance).  Since it may force a metadata
     *  update on all cluster nodes, this method may be <em>slow</em>.
     */
    public long getExactLastModifiedTime(final String fileOrDirectory) {
        return fs.get_exact_last_modification_time(fileOrDirectory);
    }

    /**
     * Truncate the specified file to the desired size
     * @param filename
     * @param desired_size
     * @return
     */
    public int truncateFile(final String filename, final long desired_size) {
        return fs.truncate_file(filename, desired_size);
    }

    /** @} **/



    public long getFileBlockSize(String filename){
    	return FS_BLOCK_SIZE * fs.get_number_of_blocks(filename);
    }

    public void changeFileGroupOwnership(String filename, String groupName){
    	fs.change_group_ownership(filename, groupName);
    }


    /**
        @defgroup fs_space  Low-level Space Reservation Functions

        <strong>This interface is a draft!</strong> Due to the
        unsettled state of the SRM spec regarding to reserved space
        semantics, and the differences between SRM space reservation
        and GPFS preallocation, it's better not use this interface
        directly; rather, use wrapper objects (like {@link
        it.grid.storm.filesystem.Space}) for operations.


        @{

     **/

    /** Return available space (in bytes) on filesystem.  Please note
     * that this value may be inaccurate on cluster/networked
     * filesystems, due to metadata caching.
     */
    public long getFreeSpace() {
        return fs.get_free_space();
    }

    

     /**
       @defgroup fs_access Access Control

       These functions should be used to test if a certain user is
       allowed to perform a filesystem operation on a file or
       directory.  The filesystem protection scheme controlling the
       possible operations is abstracted in the {@link
       it.grid.storm.filesystem.FilesystemPermission} class.

       @{

     **/

    /**
       Return <code>true</code> if the local user <i>u</i> can operate
       on the specified <i>fileOrDirectory</i> in the mode given by
       <i>accessMode</i>, according to the permissions set on the
       filesystem.

       Suppose a local UNIX user (identified by UID and the list of
       primary and supplementary GIDs) requests access to a certain file
       or directory.  Roughly, the access control algorithm is:

       - if the requestor's UID matches the UID in the @em owner entry,
       then the @em owner permissions are used;

       - else if the requestor's UID matches a UID in a <em>specific
       user</em> entry, then the bitwise-AND of that entry's
       permissions and the @em mask entry permissions are used;

       - else if any of the requestor's GIDs (primary or supplementary)
       matches the group owner entry, the bitwise-AND of that entry's
       permissions and the @em mask entry permissions are used;

       - else if any of the requestor's GIDs (primary or supplementary)
       matches the group owner entry, the bitwise-AND of that entry's
       permissions and the @em mask entry permissions are used;

       - else, (if no group entry was found to match) the @em other entry
       permissions are used.

        @see fs_acl#access()
        @sa  Linux man page acl(5)
     */
    public boolean canAccess(final LocalUser u, final String fileOrDirectory, final FilesystemPermission accessMode) {
        assert (null != u) : "Null LocalUser parameter passed to Filesystem.canAccess()";
        assert (null != fileOrDirectory) : "Null fileOrDirectory parameter passed to Filesystem.canAccess()";
        assert (null != accessMode) : "Null accessMode parameter passed to Filesystem.canAccess()";

        fs_acl acl = fs.new_acl();
        acl.load(fileOrDirectory,false);
        return acl.access(accessMode.toFsAclPermission(), u.getUid(), u.getGids());
    }

    /** @} **/


    /**

    @defgroup fs_acl ACL Manipulation

    The it.grid.storm.filesystem package supports manipulation of
    POSIX-like ACLs (GPFS supports POSIX-like ACLs), although it
    should be sufficiently general to be extended to NFSv4 and other
    cluster filesystems.

    <b>Quick POSIX ACL Glossary</b>

    @em ACL: an "Access Control List" is a list of ACEs; according to
    POSIX 1003.1e draft 17 (see
    http://www.suse.de/~agruen/acl/posix/posix.html ), ACLs come in
    two types:

      - @em access ACLs, that are used to determine whether a certain
        user/process has access to a file or directory; the ACEs in a
        access ACL are checked according to the algorithm described in
        the acl(5) manpage.

      - @em default ACLs, that control the initial permissions on a
        newly created file.  Default ACLs may be set on directories
        only.

    StoRM has no use for default ACLs, so any reference to an ACL
    hereon will actually mean an "access ACL".


    @em ACE: an "ACL Entry" binds an entity on the system (the file
    owner, a specific user, ...) and a set of permissions (access
    rights).  ACEs come in several kinds; the terms by which we denote
    the different kinds here are:
    <ul>

    <li><em>owner</em>: the owner ACE sets the permission to be applied to
        the UNIX file owner; this can be operated via the
        standard UNIX @c stat() and @c chmod() system call.

    <li><em>group owner</em>: the group owner ACE sets the permission to be
        applied to members of the file group, except the file owner;
        this can be operated via the usual @c stat() and @c chmod()
        system call, <em>when there is no mask ACE</em>.

    <li><em>specific user</em>: a specific user ACE sets the
        permissions to be applied to a specific UNIX User ID; the @em
        effective permission (i.e., the one that is actually used in
        access control checks) is the bitwise-AND of this permission
        and the @em mask entry.

    <li><em>spcific group</em>: a specific group ACE sets the
        permissions to be applied to a specific UNIX group ID; the @em
        effective permission (i.e., the one that is actually used in
        access control checks) is the bitwise-AND of this permission
        and the @em mask entry.

    <li><em>mask</em>: the mask ACE controls the maximum set of access
        rights that will be granted to users/processes matching the
        specific user, group or other ACE.  That is, any permission
        set in a "specific user", "specific group" or "other" ACE is
        bitwise-ANDed with the "mask" ACE permission set when it comes
        to checking access rights for a certain user/process.

        <p><em>Note:</em> presence of the
        @em mask ACE changes the behaviour of the @c stat() and @c
        chmod() system calls: please see:

          http://playground.sun.com/pub/nfsv4/webpage/nfsv4-wg-archive-dec-96-jan-03/3530.html

        for details and the rationale of the mask entry design.

    <li><em>other</em>: sets the permission to be applied to
        users/processes that do not fall in any of the above classes;
        this can be changed with the usual @c chmod() system call.

    </ul>

    The "owner", "group owner" and "other" ACE are @em mandatory in
    any ACL; they will always be present, since they correspond to the
    basic UNIX permissions.  The "mask" ACE must be present if any
    specific user or specific group ACE is present.


    <b>Access control algorithm</b>

    The access control algorithm implemented in StoRM <em>must</em>
    match the one implemented in the Linux kernel, which is detailed
    in the @a acl(5) man page.

    Suppose a local UNIX user (identified by UID and the list of
    primary and supplementary GIDs) requests access to a certain file
    or directory.  Roughly, the access control algorithm is:

    - if the requestor's UID matches the UID in the @em owner entry,
    then the @em owner permissions are used;

    - else if the requestor's UID matches a UID in a <em>specific
    user</em> entry, then the bitwise-AND of that entry's
    permissions and the @em mask entry permissions are used;

    - else if any of the requestor's GIDs (primary or supplementary)
    matches the group owner entry, the bitwise-AND of that entry's
    permissions and the @em mask entry permissions are used;

    - else if any of the requestor's GIDs (primary or supplementary)
    matches the group owner entry, the bitwise-AND of that entry's
    permissions and the @em mask entry permissions are used;

    - else, (if no group entry was found to match) the @em other entry
    permissions are used.


    <b>Access control and Just-in-Time ACL</b>

    A notable consequence of the access control algorithm is this: if
    a user has a specific user ACE matching his/her UID, then no other
    ACEs will be searched.  Therefore, when removing the added
    permissions in the Just-in-Time ACL policy, the StoRM code must be
    sure to restore the @em exact access rights.  This implies:
    <ol>
      <li> <em>resetting</em> the permissions in the specific user ACE
      to its former value, if a specific user ACE was present before
      the permissions were modified by the Just-in-Time StoRM code;

      <li> <em>removing</em> the added specific user entry, if no
      specific user ACE was present.
    </ol>

    The ACL manipulation methods will return @c null, if no specific
    user entry is present in the ACL being modified, or a {@link
    FilesystemPermission} instance encoding the former user-specific
    permission set.

    Thus, code implementing the above procedure may be sketched like
    this:
    <pre>
      // add Just-in-Time read+write permission
      old = file.grantUserPermission(user,
                                     FilesystemPermission.ReadWrite);
      ...
      // "reset" the ACL to its former state
      if (null == old)
        // no specific user permission, remove added one
        file.removeUserPermission(user);
      else
        // reinstate old permission set
        file.setUserPermission(user, old);
    </pre>


    <b>Effective and "unmasked" permissions in StoRM</b>

    The <em>effective permission</em> is the bitwise-AND of a
    permission (in a specific user or group ACL entry) and the value
    of the ACL "mask" entry.  We shall call the permission that is
    stored in a specific user or group ACL entry the "raw" or
    "unmasked" permission.  Only the effective permission is actually
    used for determining a users' access rights.

    So, if a the @em mask entry is set to a restrictive value, then no
    permission larger than the mask will be granted to requests that
    match a specific user or specific group entry, <em>regardless of
    the actual value of the permission set in the matched entry</em>.

    So, if a sysadmin restricts access to some files with a mask ACL
    entry, then StoRM may not be able to grant some permissions to a
    requesting user, even if the user should be given access according
    to the Authorization component (i.e., the Grid-level access
    control algorithms).

    Local policy wins over Grid policy, so StoRM should check whether
    the user has been granted the intended effective permissions, and
    fail the Grid user request in case.

    A sketch of the above procedure using actual methods in the {@link
    it.grid.storm.filesystem.File} class:
    <pre>
      // grant the Just-in-Time ACL
      oldPerm = file.grantUserPermission(user, newPerm);

      // check that intended permissions were actually granted
      actualPerm = file.getEffectiveUserPermission(user);
      if (actualPerm.allows(newPerm)) {
        // new permission is effective, proceed
        ...
      }
      else {
        // new permission is ineffective because of mask settings,
        // fail operation with SRM_UNAUTHORIZED_ACCESS and a message
        // stating that local ACL settings prevent access...
        throw new SRM_UNAUTHORIZED_ACCESS("ACL mask settings deny access.");
      }
    </pre>

    @{

     **/

    /** Return the <em>effective</em> permission a group has on the
     * given file or directory.
     * 
     * Loads the ACL for the given file or directory, and return the
     * permission associated with the primary group of the given
     * {@link it.grid.storm.griduser.LocalUser} instance <i>u</i>.  If
     * no entry for that group is found, return <code>null</code>.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose primary GID's
     * permissions are to be retrieved.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the named group permissions.
     *
     * @return permission associated to the primary GID of the given
     * {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in
     * the given file ACL, or <code>null</code> if no entry for that
     * group was found.
     */
    public FilesystemPermission getEffectiveGroupPermission (final LocalUser u, final String fileOrDirectory) {
        return getPermissionTemplate(u, fileOrDirectory, getEffectiveGroupPermissionMethod);
    }

    /** Return the <em>effective</em> permission a user has on the
     * given file or directory.
     * 
     * Loads the ACL for the given file or directory, and return the
     * permission associated with the UID of the given {@link
     * it.grid.storm.griduser.LocalUser} instance <i>u</i>.  If no
     * entry for that user is found, return <code>null</code>.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose permissions
     * are to be retrieved.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the given user permissions.
     *
     * @return permission associated to the UID of the given {@link
     * LocalUser} instance <i>u</i> in the given file ACL, or
     * <code>null</code> if no entry for that user was found.
     */
    public FilesystemPermission getEffectiveUserPermission (final LocalUser u, final String fileOrDirectory) {
        return getPermissionTemplate(u, fileOrDirectory, getEffectiveUserPermissionMethod);
    }

    /** Return the permission a group has on the given file or
     * directory.
     * 
     * Loads the ACL for the given file or directory, and
     * return the permission associated with the primary group of the
     * given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i>.  If no entry for that
     * group is found, return <code>null</code>.
     *
     * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose
     * primary GID's permissions are to be retrieved.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the named group permissions.
     *
     * @return permission associated to the primary GID of the given
     * {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in
     * the given file ACL, or <code>null</code> if no entry for that
     * group was found.
     */
    public FilesystemPermission getGroupPermission (final LocalUser u, final String fileOrDirectory) {
        return getPermissionTemplate(u, fileOrDirectory, getGroupPermissionMethod);
    }

    /** Return the permission a user has on the given file or
     * directory.
     * 
     * Loads the ACL for the given file or directory, and return the
     * permission associated with the UID of the given {@link
     * it.grid.storm.griduser.LocalUser} instance <i>u</i>.  If no
     * entry for that user is found, return <code>null</code>.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose permissions
     * are to be retrieved.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the given user permissions.
     *
     * @return permission associated to the UID of the given {@link
     * LocalUser} instance <i>u</i> in the given file ACL, or
     * <code>null</code> if no entry for that user was found.
     */
    public FilesystemPermission getUserPermission (final LocalUser u, final String fileOrDirectory) {
        return getPermissionTemplate(u, fileOrDirectory, getUserPermissionMethod);
    }

    /** Grant specified permission to a group on a file or directory,
     * and return the former permission.
     * 
     * <p>Adds the specified permission to the ones that the primary
     * group of the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> already
     * holds on the given file or directory: all permission
     * bits that are set in <i>permission</i> will be set in the
     * appropriate group entry in the file ACL.
     *
     * <p>If no entry is present for the specified group, then one is
     * created and its permission value is set to <i>permission</i>.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose primary GID's
     * entry is to be altered.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the given group permissions
     *
     * @param permission Capabilities to grant.
     * 
     * @return permission formerly associated to the primary GID of
     * the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file
     * ACL, or <code>null</code> if no entry for that group was found.
     *
     * @see fs_acl::grant_group_perm()
     */
    public FilesystemPermission grantGroupPermission (final LocalUser u, final String fileOrDirectory,
            final FilesystemPermission permission) {
        return setPermissionTemplate(u, fileOrDirectory, permission, grantGroupPermissionMethod);
    }

    /** Grant specified permission to a user on a file or directory,
     * and return the former permission.
     * 
     * <p>Adds the specified permissions to the ones that the
     * UID of the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> already
     * holds on the given file or directory: all permission
     * bits that are set in <i>permission</i> will be set in the
     * appropriate user entry in the file ACL.
     *
     * <p>If no entry is present for the specified user, then one is
     * created and its permission value is set to <i>permission</i>.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose UID's entry is
     * to be altered.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the given user permissions
     *
     * @param permission Capabilities to grant.
     * 
     * @return permission formerly associated to the UID of the given
     * {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file ACL, or
     * <code>null</code> if no entry for that user was found.
     *
     * @see fs_acl::grant_user_perm()
     */
    public FilesystemPermission grantUserPermission (final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {
        return setPermissionTemplate(u, fileOrDirectory, permission, grantUserPermissionMethod);
    }

    /** Remove a group's entry from a file or directory ACL, and return
     * the (now deleted) permission.
     * 
     * <p>Removes the entry (if any) of the primary group of the given
     * {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> from the given file or
     * directory ACL.  Returns the permission formerly associated with
     * that group.
     *
     * @todo would this be the correct behaviuor: if the given group
     * is the file owning group, then its entry is set to {@link #NONE},
     * rather than removed.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose primary GID's
     * entry is to be altered.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the given group permissions
     *
     * @return permission formerly associated to the primary GID of
     * the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file
     * ACL, or <code>null</code> if no entry for that group was found.
     *
     * @see fs_acl::remove_group_perm_not_owner()
     */
    public FilesystemPermission removeGroupPermission (final LocalUser u, final String fileOrDirectory) {
        return removePermissionTemplate(u, fileOrDirectory, removeGroupPermissionMethod);
    }

    /** Remove a user's entry from a file or directory ACL, and return
     * the (now deleted) permission.
     * 
     * <p>Removes the entry (if any) associated with the UID of the given
     * {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> from the given file or
     * directory ACL.  Returns the permission formerly associated with
     * that user.
     *
     * @todo would this be the correct behaviour: if the given user is
     * the file owner, then its entry is set to {@link #NONE}, rather
     * than removed.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose UID's
     * entry is to be altered.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the named user permissions
     *
     * @return permission formerly associated to the UID of
     * the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file
     * ACL, or <code>null</code> if no entry for that user was found.
     *
     * @see fs_acl::remove_user_perm_not_owner()
     */
    public FilesystemPermission removeUserPermission (final LocalUser u, final String fileOrDirectory) {
        return removePermissionTemplate(u, fileOrDirectory, removeUserPermissionMethod);
    }

    /** Revoke specified permission from a group's entry on a file or
     * directory, and return the former permission.
     * 
     * <p>Removes the specified permission from the ones that the
     * primary group of the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i>
     * already holds on the given file or directory: all permission
     * bits that are <em>set</em> in <i>permission</i> will be
     * <em>cleared</em> in the appropriate group entry in the file ACL.
     *
     * <p>If no entry is present for the specified group, then one is
     * created and its permission value is set to {@link #NONE}.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose primary GID's
     * entry is to be altered.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the named group permissions
     *
     * @param permission Capabilities to revoke.
     * 
     * @return permission formerly associated with the primary GID of
     * the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file
     * ACL, or <code>null</code> if no entry for that group was found.
     *
     * @see fs_acl::revoke_group_perm()
     */
    public FilesystemPermission revokeGroupPermission (final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {
        return setPermissionTemplate(u, fileOrDirectory, permission, revokeGroupPermissionMethod);
    }

    /** Revoke specified permission from a user's entry on a file or
     * directory, and return the former permission.
     * 
     * <p>Removes the specified permission from the ones that the
     * primary user of the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i>
     * already holds on the given file or directory: all permission
     * bits that are <em>set</em> in <i>permission</i> will be
     * <em>cleared</em> in the appropriate user entry in the file ACL.
     *
     * <p>If no entry is present for the specified user, then one is
     * created and its permission value is set to {@link #NONE}.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose UID's
     * entry is to be altered.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the named user permissions
     *
     * @param permission Capabilities to revoke.
     * 
     * @return permission formerly associated with the UID of
     * the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file
     * ACL, or <code>null</code> if no entry for that user was found.
     *
     * @see fs_acl::revoke_user_perm()
     */
    public FilesystemPermission revokeUserPermission (final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {
        return setPermissionTemplate(u, fileOrDirectory, permission, revokeUserPermissionMethod);
    }

    /** Set the specified permission in a group's entry on a file or
     * directory, and return the former permission.
     * 
     * <p>Sets the entry of the primary group of the given {@link
     * LocalUser} instance <i>u</i> to the given <i>permission</i>.
     * Returns the permission formerly associated with that group.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose primary GID's
     * entry is to be altered.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the named group permissions
     *
     * @param permission Permission to set in the group entry.
     * 
     * @return permission formerly associated with the primary GID of
     * the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file
     * ACL, or <code>null</code> if no entry for that group was found.
     *
     * @see fs_acl::set_group_perm()
     */
    public FilesystemPermission setGroupPermission (final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {
        return setPermissionTemplate(u, fileOrDirectory, permission, setGroupPermissionMethod);
    }

    /** Set the specified permission in a user's entry on a file or
     * directory, and return the former permission.
     * 
     * <p>Sets the entry of the primary user of the given {@link
     * LocalUser} instance <i>u</i> to the given <i>permission</i>.
     * Returns the permission formerly associated with that user.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose UID's
     * entry is to be altered.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for the named user permissions
     *
     * @param permission Permission to set in the user entry.
     * 
     * @return permission formerly associated with the UID of
     * the given {@link it.grid.storm.griduser.LocalUser} instance <i>u</i> in the given file
     * ACL, or <code>null</code> if no entry for that user was found.
     *
     * @see fs_acl::set_user_perm()
     */
    public FilesystemPermission setUserPermission (final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {
        return setPermissionTemplate(u, fileOrDirectory, permission, setUserPermissionMethod);
    }


    /** @} **/








    // --- ACL manipulation template methods ---

    /** Template method for getting user/group entry. */
    private interface GetPermissionMethod {
        /** Template method for getting user/group entry. */
        public FilesystemPermission get(final fs_acl a, final LocalUser u);
    }

    /** Implements the template method for getting a group entry from
     * an fs_acl instance.
     */
    private class GetGroupPermissionMethod implements GetPermissionMethod {
        /** Returns the permission associated with the
         * <em>primary</em> GID of the passed {@link
         * it.grid.storm.griduser.LocalUser} instance,
         * or <code>null</code> if no permission is set specifically
         * for that group.
         */
        public FilesystemPermission get(final fs_acl a, final LocalUser u) {
            if (a.has_group_perm(u.getPrimaryGid())) {
                return new FilesystemPermission(a.get_group_perm(u.getPrimaryGid()));
            } else {
                return null;
            }
        }
    }

    /** Implements the template method for getting a users' entry from
     * an fs_acl instance.
     */
    private class GetUserPermissionMethod implements GetPermissionMethod {
        /** Return the permission associated with the UID of the
         * passed {@link it.grid.storm.griduser.LocalUser} instance,
         * or <code>null</code> if no permission is set specifically
         * for that user.
         */
        public FilesystemPermission get(final fs_acl a, final LocalUser u) {
            if (a.has_user_perm(u.getUid())) {
                return new FilesystemPermission(a.get_user_perm(u.getUid()));
            } else {
                return null;
            }
        }
    }

    /** Implements the template method for getting a group entry from
     * an fs_acl instance.
     */
    private class GetEffectiveGroupPermissionMethod implements GetPermissionMethod {
        /** Returns the <em>effective</em> permission associated with
         * the <em>primary</em> GID of the passed {@link
         * it.grid.storm.griduser.LocalUser} instance, or
         * <code>null</code> if no permission is set specifically for
         * that group.
         */
        public FilesystemPermission get(final fs_acl a, final LocalUser u) {
            if (a.has_group_perm(u.getPrimaryGid())) {
                return new FilesystemPermission(a.get_group_effective_perm(u.getPrimaryGid()));
            } else {
                return null;
            }
        }
    }

    /** Implements the template method for getting a users' entry from
     * an fs_acl instance.
     */
    private class GetEffectiveUserPermissionMethod implements GetPermissionMethod {
        /** Return the <em>effective</em> permission associated with
         * the UID of the passed {@link
         * it.grid.storm.griduser.LocalUser} instance, or
         * <code>null</code> if no permission is set specifically for
         * that user.
         */
        public FilesystemPermission get(final fs_acl a, final LocalUser u) {
            if (a.has_user_perm(u.getUid())) {
                return new FilesystemPermission(a.get_user_effective_perm(u.getUid()));
            } else {
                return null;
            }
        }
    }

    /** Return the permission a user or group has on the named file,
     * or <code>null</code> if no permission is set specifically for
     * that user or group.
     *
     * Whether the returned permission applies to the user's UID or
     * primary GID depends on the passed @a permissionMethod
     * parameter.
     *
     * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose
     * permissions are to be retrieved.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for user/group permissions.
     *
     * @param permissionMethod a class implementing the {@link
     * GetPermissionTemplate} interface; its <code>get</code> method
     * should return the sought-for permission or <code>null</code>.
     *
     * @see GetPermissionTemplate
     */
    private FilesystemPermission getPermissionTemplate (final LocalUser u, final String fileOrDirectory,
            final GetPermissionMethod permissionMethod) {

        assert (null != u) : "Null LocalUser passed to Filesystem.getPermissionTemplate()";
        assert (null != fileOrDirectory) : "Null fileOrDirectory passed to Filesystem.getPermissionTemplate()";
        assert (null != permissionMethod) : "Null permissionMethod passed to Filesystem.getPermissionTemplate()";

        fs_acl acl = fs.new_acl();
        acl.load(fileOrDirectory,false);
        FilesystemPermission permission = permissionMethod.get(acl, u);
        return permission; 
    }





    /** Template method for removing a user/group entry from an ACL. */
    private interface RemovePermissionMethod {
        /** Template method for removing a user/group entry from an ACL. */
        public FilesystemPermission remove(final fs_acl a, final LocalUser u);
    }

    /** Implements the template method for removing a users' entry from
     * an fs_acl instance.
     */
    private class RemoveUserPermissionMethod implements RemovePermissionMethod {
        /** Removes the entry associated with the UID of the passed
         * {@link it.grid.storm.griduser.LocalUser} instance, and
         * returns its former permission value.  The UID <em>must
         * not</em> be the file owner's UID, or an assertion will fail
         * and execution will stop.
         * 
         * @todo FIXME: what should it do when no permission is found?
         * fail an assertion (current behaviour)? throw exception?
         * silently ignore?
         *
         * @todo FIXME: what should it do when requested to remove the
         * (group) owner permission? throw an excpetion? set permits
         * to NONE?
         */
        public FilesystemPermission remove(final fs_acl a, final LocalUser u) {
            assert (a.has_user_perm(u.getUid())) : "Filesystem: removing permission for user "+u.getUid()+"that has no permission associated!";
            return new FilesystemPermission(a.remove_user_perm_not_owner(u.getUid()));
        }
    }

    /** Implements the template method for removing a group entry from
     * an fs_acl instance.
     */
    private class RemoveGroupPermissionMethod implements RemovePermissionMethod {
        /** Removes the entry associated with the primary GID of the
         * passed {@link it.grid.storm.griduser.LocalUser} instance,
         * and returns its former permission value.
         * 
         * @todo FIXME: what should it do when no permission is found?
         * fail an assertion (current behaviour)? throw exception?
         * silently ignore?
         *
         * @todo FIXME: what should it do when requested to remove the
         * (group) owner permission? throw an excpetion? set permits
         * to NONE?
         */
        public FilesystemPermission remove(final fs_acl a, final LocalUser u) {
            assert (a.has_user_perm(u.getUid())) : "Filesystem: removing permission for group "+u.getUid()+"that has no permission associated!";
            return new FilesystemPermission(a.remove_group_perm_not_owner(u.getPrimaryGid()));
        }
    }

    /** Remove the entry a user or group permission has on the named
     * file, and return the former permission.  Whether the returned
     * permission applies to the user's UID or primary GID depends on
     * the passed @a permissionMethod parameter.
     *
     * <p> This method serializes filesystem access, because the POSIX ACL
     * manipulation API only allows for getting or setting the
     * <em>whole</em> list of permissions; no changes of individual
     * permissions can be performed.  Therefore, no two threads can
     * concurrently manipulate ACLs on the same file, or they may
     * overwrite each other's changes.
     *
     * @param u the local user ({@link it.grid.storm.griduser.LocalUser} instance) whose
     * permissions are to be removed.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be searched for users' permissions.
     *
     * @param permissionMethod a class implementing the {@link
     * RemovePermissionTemplate} interface; its <code>remove</code>
     * method should return the sought-for permission.
     *
     * @see RemovePermissionTemplate
     */
    private FilesystemPermission removePermissionTemplate (final LocalUser u, final String fileOrDirectory,
            final RemovePermissionMethod permissionMethod) {
        assert (null != u) : "Null LocalUser passed to Filesystem.removePermissionTemplate()";
        assert (null != fileOrDirectory) : "Null fileOrDirectory passed to Filesystem.removePermissionTemplate()";
        assert (null != permissionMethod) : "Null permissionMethod passed to Filesystem.removePermissionTemplate()";

        FilesystemPermission oldPermission;
        fs_acl acl = fs.new_acl();
        // do not allow concurrent operation on the same pathname
        Semaphore lock = locks.get(fileOrDirectory);
        try {
            lock.acquireUninterruptibly();
            acl.load(fileOrDirectory,false);
            oldPermission = permissionMethod.remove(acl, u);
            acl.enforce(fileOrDirectory);
        } finally {
            lock.release();
            locks.remove(fileOrDirectory);
        }
        return oldPermission;
    }



    /** Template method for setting user/group entry. */
    private interface SetPermissionMethod {
        public FilesystemPermission apply(final fs_acl a, final LocalUser u, final FilesystemPermission p);
    }

    /** Implements the template method for setting a user's entry on an
     * fs_acl instance.
     */
    private class SetUserPermissionMethod implements SetPermissionMethod {
        /** Sets the permission associated with the UID of the passed
         * {@link it.grid.storm.griduser.LocalUser} on a fs_acl instance.  If no
         * permission entry exists for that UID, a new one is created,
         * holding the UID and the given permission.
         * 
         * @return the old replaced permission, or <code>null</code>
         * if none was found.
         */
        public FilesystemPermission apply(final fs_acl a, final LocalUser u, final FilesystemPermission p) {
            if (a.has_user_perm(u.getUid())) {
                return new FilesystemPermission(a.set_user_perm(u.getUid(), p.toFsAclPermission()));
            } else{
                a.set_user_perm(u.getUid(), p.toFsAclPermission());
                return null;
            }
        }
    }

    /** Implements the template method for granting additional
     * permissions to a group on an fs_acl instance.
     */
    private class GrantUserPermissionMethod implements SetPermissionMethod {
        /** Grants additional permissions to the UID of the passed
         * {@link it.grid.storm.griduser.LocalUser} on a fs_acl instance.  If no permission
         * entry exists for that UID, a new one is created, holding the
         * UID and the given permission.
         * 
         * @return the old replaced permission, or <code>null</code>
         * if none was found.
         */
        public FilesystemPermission apply(final fs_acl a, final LocalUser u, final FilesystemPermission p) {
            if (a.has_user_perm(u.getUid())) {
                return new FilesystemPermission(a.grant_user_perm(u.getUid(), p.toFsAclPermission()));
            } else {
                a.grant_user_perm(u.getUid(), p.toFsAclPermission());
                return null;
            }
        }
    }

    /** Implements the template method for revoking selected
     * permissions to a user on an fs_acl instance.
     */
    private class RevokeUserPermissionMethod implements SetPermissionMethod {
        /** Revoke selected permissions to the UID of the passed
         * {@link it.grid.storm.griduser.LocalUser} on a fs_acl instance.  If no permission
         * entry exists for that UID, a new one is created, holding the
         * UID and the given permission.
         * 
         * @return the old replaced permission, or <code>null</code>
         * if none was found.
         */
        public FilesystemPermission apply(final fs_acl a, final LocalUser u, final FilesystemPermission p) {
            if (a.has_user_perm(u.getUid())) {
                return new FilesystemPermission(a.revoke_user_perm(u.getUid(), p.toFsAclPermission()));
            } else {
                return null;
            }
        }
    }

    /** Implements the template method for setting a group entry from an
     * fs_acl instance.
     */
    private class SetGroupPermissionMethod implements SetPermissionMethod {
        /** Sets the permission associated with the <em>primary</em>
         * GID of the passed {@link it.grid.storm.griduser.LocalUser} on a fs_acl instance.
         * If no permission entry exists for that GID, a new one is
         * created, holding the GID and the given permission.
         * 
         * @return the old replaced permission, or <code>null</code>
         * if none was found.
         */
        public FilesystemPermission apply(final fs_acl a, final LocalUser u, final FilesystemPermission p) {
            if (a.has_group_perm(u.getPrimaryGid())) {
                return new FilesystemPermission(a.set_group_perm(u.getPrimaryGid(), p.toFsAclPermission()));
            } else {
                a.set_group_perm(u.getPrimaryGid(), p.toFsAclPermission());
                return null;
            }
        }
    }

    /** Implements the template method for granting additional
     * permissions to a group on an fs_acl instance.
     */
    private class GrantGroupPermissionMethod implements SetPermissionMethod {
        /** Grants additional permissions to the <em>primary</em>
         * GID of the passed {@link it.grid.storm.griduser.LocalUser} on a fs_acl instance.
         * If no permission entry exists for that GID, a new one is
         * created, holding the GID and the given permission.
         * 
         * @return the old replaced permission, or <code>null</code>
         * if none was found.
         */
        public FilesystemPermission apply(final fs_acl a, final LocalUser u, final FilesystemPermission p) {
            if (a.has_group_perm(u.getPrimaryGid())) {
                return new FilesystemPermission(a.grant_group_perm(u.getPrimaryGid(), p.toFsAclPermission()));
            } else {
                a.grant_group_perm(u.getPrimaryGid(), p.toFsAclPermission());
                return null;
            }
        }
    }

    /** Implements the template method for revoking selected
     * permissions to a group on an fs_acl instance.
     */
    private class RevokeGroupPermissionMethod implements SetPermissionMethod {
        /** Revoke selected permissions to the <em>primary</em> GID of
         * the passed {@link it.grid.storm.griduser.LocalUser} on a fs_acl instance.  If no
         * permission entry exists for that GID, a new one is created,
         * holding the GID and the given permission.
         * 
         * @return the old replaced permission, or <code>null</code>
         * if none was found.
         */
        public FilesystemPermission apply(final fs_acl a, final LocalUser u, final FilesystemPermission p) {
            if (a.has_group_perm(u.getPrimaryGid())) {
                return new FilesystemPermission(a.revoke_group_perm(u.getPrimaryGid(), p.toFsAclPermission()));
            } else {
                return null;
            }
        }
    }

    /** Set the permission a user or group has on the given file.
     * Whether the returned permission applies to the user's UID or
     * primary GID depends on the passed @a permissionMethod
     * parameter.
     *
     * <p> This method serializes filesystem access, because the POSIX
     * ACL manipulation API only allows for getting or setting the
     * <em>whole</em> list of permissions; no changes of individual
     * permissions can be performed.  Therefore, no two threads can
     * concurrently manipulate ACLs on the same file, or they may
     * overwrite each other's changes.
     *
     * @param u the local user ({@link
     * it.grid.storm.griduser.LocalUser} instance) whose permissions
     * are to be retrieved.
     *
     * @param fileOrDirectory pathname to the file or directory whose
     * ACL is to be scanned for users' permissions
     *
     * @param permissionMethod a class implementing the {@link
     * SetPermissionTemplate} interface; its <code>apply</code> method
     * should modify the entry return the replaced permission.
     *
     * @return A {@link FilesystemPermission} instance encoding the
     * old permission that has been overwritten.
     *
     * @see GetPermissionTemplate
     */
    private FilesystemPermission setPermissionTemplate (final LocalUser u, final String fileOrDirectory,
            final FilesystemPermission p, final SetPermissionMethod setPermissionMethod) {
        assert (null != u) : "Null LocalUser passed to Filesystem.setPermissionTemplate()";
        assert (null != fileOrDirectory) : "Null fileOrDirectory passed to Filesystem.setPermissionTemplate()";
        assert (null != p) : "Null FilesystemPermission passed to Filesystem.setPermissionTemplate()";
        assert (null != setPermissionMethod) : "Null permissionMethod passed to Filesystem.setPermissionTemplate()";

        FilesystemPermission oldPermission;
        fs_acl acl = fs.new_acl();
        // do not allow concurrent operation on the same pathname
        AclLockPoolElement lock = locks.get(fileOrDirectory);
        try {
            lock.acquireUninterruptibly();
            acl.load(fileOrDirectory,false);
            oldPermission = setPermissionMethod.apply(acl, u, p);
            acl.enforce(fileOrDirectory);
        } finally {
            lock.release();
            locks.remove(fileOrDirectory);
        }
        return oldPermission;
    }
    
}
