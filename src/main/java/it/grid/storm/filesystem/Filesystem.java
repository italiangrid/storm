/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * @file Filesystem.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *     <p>The it.grid.storm.filesystem.Filesystem class
 */
/*
 * Copyright (c) 2006 Riccardo Murri <riccardo.murri@ictp.it> for the EGRID/INFN
 * joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms as StoRM
 * itself.
 */

package it.grid.storm.filesystem;

import it.grid.storm.filesystem.swig.fs_acl;
import it.grid.storm.filesystem.swig.genericfs;
import it.grid.storm.griduser.LocalUser;
import java.io.File;
import java.util.concurrent.Semaphore;

/**
 * Façade and base class for filesystem manipulation.
 *
 * <p>This class implements methods for manipulation of filesystem entries. You should not use this
 * class directly in StoRM code from outside this package, rather manipulate the filesystem in StoRM
 * through the {@link File} and {@link Space} interfaces: this class is just a thin wrapper around
 * low-level filesystem calls.
 *
 * <p>This class should be instanciated by giving a fs::genericfs subclass instance that is shall be
 * used for all filesystem operations. Clearly, only the configuration mechanism knows which
 * filesystem-type has been configured on a certain path, so this class should only be instanciated
 * at stratup/configuration time.
 *
 * <p>Not only, can an instance of this class be shared among different objects operating on the
 * same filesystem (or portion of it), but it actually @em should, because of shared ACL locking
 * (see "Implementation Notes").
 *
 * <p><b>Implementation notes</b>
 *
 * <p>This class tries to consistently use:
 *
 * <p>- <code>String</code> for path names.
 *
 * <p>- <code>long</code> for sizes; sizes are always expressed in bytes.
 *
 * <p>- {@link FilesystemPermission} for expressing permissions that a user has on a file;
 *
 * <p>- {@link it.grid.storm.griduser.LocalUser} for representing user credentials.
 *
 * <p>This class serializes files access in the ACL manipulation methods, because the POSIX ACL
 * manipulation API only allows for getting or setting the <em>whole</em> list of permissions; no
 * changes of individual permissions can be performed. Therefore, no two threads can concurrently
 * manipulate ACLs on the same file, or they may overwrite each other's changes.
 *
 * <p>A per-filename lock is maintained, and no two threads can concurrently modify ACL on the same
 * pathname. Modification by other programs (that is, outside StoRM) cannot be prevented, so we
 * still have a race condition.
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it> @version $Revision: 1.20 $
 */
public class Filesystem implements FilesystemIF {

  // --- private instance variables ---

  /** Low-level filesystem interface. */
  private final genericfs fs;

  /** Cache of lock instances. */
  private static AclLockPool locks = new AclLockPool();

  private GetGroupPermissionMethod getGroupPermissionMethod = new GetGroupPermissionMethod();
  private GetUserPermissionMethod getUserPermissionMethod = new GetUserPermissionMethod();

  private GetEffectiveGroupPermissionMethod getEffectiveGroupPermissionMethod =
      new GetEffectiveGroupPermissionMethod();
  private GetEffectiveUserPermissionMethod getEffectiveUserPermissionMethod =
      new GetEffectiveUserPermissionMethod();

  private GrantGroupPermissionMethod grantGroupPermissionMethod = new GrantGroupPermissionMethod();
  private GrantUserPermissionMethod grantUserPermissionMethod = new GrantUserPermissionMethod();

  private RemoveGroupPermissionMethod removeGroupPermissionMethod =
      new RemoveGroupPermissionMethod();
  private RemoveUserPermissionMethod removeUserPermissionMethod = new RemoveUserPermissionMethod();

  private RevokeGroupPermissionMethod revokeGroupPermissionMethod =
      new RevokeGroupPermissionMethod();
  private RevokeUserPermissionMethod revokeUserPermissionMethod = new RevokeUserPermissionMethod();

  private SetGroupPermissionMethod setGroupPermissionMethod = new SetGroupPermissionMethod();
  private SetUserPermissionMethod setUserPermissionMethod = new SetUserPermissionMethod();

  // --- constructor ---

  /** Constructor, taking native low-level filesystem interface object. */
  public Filesystem(final genericfs nativeFs) {

    assert (null != nativeFs)
        : "Null nativeFs in Filesystem(NativeFilesystemInterface) constructor";

    fs = nativeFs;

    getGroupPermissionMethod = new GetGroupPermissionMethod();
    getUserPermissionMethod = new GetUserPermissionMethod();

    grantGroupPermissionMethod = new GrantGroupPermissionMethod();
    grantUserPermissionMethod = new GrantUserPermissionMethod();

    revokeGroupPermissionMethod = new RevokeGroupPermissionMethod();
    revokeUserPermissionMethod = new RevokeUserPermissionMethod();

    setGroupPermissionMethod = new SetGroupPermissionMethod();
  }

  /**
   * @defgroup fs_fileops Low-level File and Directory Operations
   *     <p>Methods here come in two variants: the "exact" one which tries to return up-to-date
   *     information, and the "standard" one, which uses common POSIX system calls to get the
   *     required info.
   *     <p>The "exact" variant may force a cluster-wide metadata update, so it may result in
   *     performance degradation if used frerquently.
   *     <p>Motivation for this comes from the GPFS filesystem: GPFS nodes perform file metadata and
   *     attribute caching, so they may return out-of-date results to the standard system calls. The
   *     GPFS API provides the corresponding "exact" calls.
   *     <p>In the generic POSIX filesystem implementation, there's no difference between the
   *     "exact" and the "standard" call. @{
   */

  /** Get file size in bytes. Same as calling {@link java.io.File#length()}. */
  @Override
  public long getSize(final String file) {

    return fs.get_size(file);
  }

  /**
   * Get file last modification time, as a UNIX epoch. Same as calling {@link
   * java.io.File#lastModified()}.
   */
  @Override
  public long getLastModifiedTime(final String fileOrDirectory) {

    /**
     * Since the lastModificationTime can be retrieved by Java.io.File we prefer to don't use the
     * native driver for get this information.
     *
     * @todo This should be done in the JAVA side of each driver, since this solution will not work
     *     in situation (as Amazon s3) for which a native JVM support does not exists.
     */
    File fileOrDir = new File(fileOrDirectory);
    return fileOrDir.lastModified();
  }

  /**
   * Get up-to-date file size in bytes. Returned value may differ from the size returned by {@link
   * java.io.File#length()} on filesystems that do metadata caching (GPFS, for instance). Since it
   * may force a metadata update on all cluster nodes, this method may be <em>slow</em>.
   */
  @Override
  public long getExactSize(final String file) {

    File fileOrDir = new File(file);
    return fileOrDir.length();
  }

  /**
   * Get up-to-date file last modification time, as a UNIX epoch. Returned value may differ from the
   * size returned by {@link java.io.File#lastModified()} on filesystems that do metadata caching
   * (GPFS, for instance). Since it may force a metadata update on all cluster nodes, this method
   * may be <em>slow</em>.
   */
  @Override
  public long getExactLastModifiedTime(final String fileOrDirectory) {

    return fs.get_exact_last_modification_time(fileOrDirectory);
  }

  /**
   * Truncate the specified file to the desired size
   *
   * @param filename @param desired_size @return
   */
  @Override
  public int truncateFile(final String filename, final long desired_size) {

    return fs.truncate_file(filename, desired_size);
  }

  @Override
  public boolean isFileOnDisk(String filename) {

    return fs.is_file_on_disk(filename);
  }

  @Override
  public long getFileBlockSize(String filename) {

    return FS_BLOCK_SIZE * fs.get_number_of_blocks(filename);
  }

  @Override
  public void changeFileGroupOwnership(String filename, String groupName) {

    fs.change_group_ownership(filename, groupName);
  }

  /**
   * @defgroup fs_space Low-level Space Reservation Functions
   *     <p><strong>This interface is a draft!</strong> Due to the unsettled state of the SRM spec
   *     regarding to reserved space semantics, and the differences between SRM space reservation
   *     and GPFS preallocation, it's better not use this interface directly; rather, use wrapper
   *     objects (like {@link it.grid.storm.filesystem.Space}) for operations. @{
   */

  /**
   * Return available space (in bytes) on filesystem. Please note that this value may be inaccurate
   * on cluster/networked filesystems, due to metadata caching.
   */
  @Override
  public long getFreeSpace() {

    return fs.get_free_space();
  }

  @Override
  public boolean canAccess(
      final LocalUser u, final String fileOrDirectory, final FilesystemPermission accessMode) {

    assert (null != u) : "Null LocalUser parameter passed to Filesystem.canAccess()";
    assert (null != fileOrDirectory)
        : "Null fileOrDirectory parameter passed to Filesystem.canAccess()";
    assert (null != accessMode) : "Null accessMode parameter passed to Filesystem.canAccess()";

    fs_acl acl = fs.new_acl();
    acl.load(fileOrDirectory, false);
    return acl.access(accessMode.toFsAclPermission(), u.getUid(), u.getGids());
  }

  @Override
  public FilesystemPermission getEffectiveGroupPermission(
      final LocalUser u, final String fileOrDirectory) {

    return getPermissionTemplate(u, fileOrDirectory, getEffectiveGroupPermissionMethod);
  }

  @Override
  public FilesystemPermission getEffectiveUserPermission(
      final LocalUser u, final String fileOrDirectory) {

    return getPermissionTemplate(u, fileOrDirectory, getEffectiveUserPermissionMethod);
  }

  @Override
  public FilesystemPermission getGroupPermission(final LocalUser u, final String fileOrDirectory) {

    return getPermissionTemplate(u, fileOrDirectory, getGroupPermissionMethod);
  }

  @Override
  public FilesystemPermission getUserPermission(final LocalUser u, final String fileOrDirectory) {

    return getPermissionTemplate(u, fileOrDirectory, getUserPermissionMethod);
  }

  @Override
  public FilesystemPermission grantGroupPermission(
      final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {

    return setPermissionTemplate(u, fileOrDirectory, permission, grantGroupPermissionMethod);
  }

  @Override
  public FilesystemPermission grantUserPermission(
      final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {

    return setPermissionTemplate(u, fileOrDirectory, permission, grantUserPermissionMethod);
  }

  @Override
  public FilesystemPermission removeGroupPermission(
      final LocalUser u, final String fileOrDirectory) {

    return removePermissionTemplate(u, fileOrDirectory, removeGroupPermissionMethod);
  }

  @Override
  public FilesystemPermission removeUserPermission(
      final LocalUser u, final String fileOrDirectory) {

    return removePermissionTemplate(u, fileOrDirectory, removeUserPermissionMethod);
  }

  @Override
  public FilesystemPermission revokeGroupPermission(
      final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {

    return setPermissionTemplate(u, fileOrDirectory, permission, revokeGroupPermissionMethod);
  }

  @Override
  public FilesystemPermission revokeUserPermission(
      final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {

    return setPermissionTemplate(u, fileOrDirectory, permission, revokeUserPermissionMethod);
  }

  @Override
  public FilesystemPermission setGroupPermission(
      final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {

    return setPermissionTemplate(u, fileOrDirectory, permission, setGroupPermissionMethod);
  }

  @Override
  public FilesystemPermission setUserPermission(
      final LocalUser u, final String fileOrDirectory, final FilesystemPermission permission) {

    return setPermissionTemplate(u, fileOrDirectory, permission, setUserPermissionMethod);
  }

  private interface GetPermissionMethod {

    public FilesystemPermission get(final fs_acl a, final LocalUser u);
  }

  private class GetGroupPermissionMethod implements GetPermissionMethod {

    public FilesystemPermission get(final fs_acl a, final LocalUser u) {

      if (a.has_group_perm(u.getPrimaryGid())) {
        return new FilesystemPermission(a.get_group_perm(u.getPrimaryGid()));
      } else {
        return null;
      }
    }
  }

  private class GetUserPermissionMethod implements GetPermissionMethod {

    public FilesystemPermission get(final fs_acl a, final LocalUser u) {

      if (a.has_user_perm(u.getUid())) {
        return new FilesystemPermission(a.get_user_perm(u.getUid()));
      } else {
        return null;
      }
    }
  }

  private class GetEffectiveGroupPermissionMethod implements GetPermissionMethod {

    public FilesystemPermission get(final fs_acl a, final LocalUser u) {

      if (a.has_group_perm(u.getPrimaryGid())) {
        return new FilesystemPermission(a.get_group_effective_perm(u.getPrimaryGid()));
      } else {
        return null;
      }
    }
  }

  private class GetEffectiveUserPermissionMethod implements GetPermissionMethod {

    public FilesystemPermission get(final fs_acl a, final LocalUser u) {

      if (a.has_user_perm(u.getUid())) {
        return new FilesystemPermission(a.get_user_effective_perm(u.getUid()));
      } else {
        return null;
      }
    }
  }

  private FilesystemPermission getPermissionTemplate(
      final LocalUser u, final String fileOrDirectory, final GetPermissionMethod permissionMethod) {

    assert (null != u) : "Null LocalUser passed to Filesystem.getPermissionTemplate()";
    assert (null != fileOrDirectory)
        : "Null fileOrDirectory passed to Filesystem.getPermissionTemplate()";
    assert (null != permissionMethod)
        : "Null permissionMethod passed to Filesystem.getPermissionTemplate()";

    fs_acl acl = fs.new_acl();
    acl.load(fileOrDirectory, false);
    FilesystemPermission permission = permissionMethod.get(acl, u);
    return permission;
  }

  /** Template method for removing a user/group entry from an ACL. */
  private interface RemovePermissionMethod {

    /** Template method for removing a user/group entry from an ACL. */
    public FilesystemPermission remove(final fs_acl a, final LocalUser u);
  }

  private class RemoveUserPermissionMethod implements RemovePermissionMethod {

    public FilesystemPermission remove(final fs_acl a, final LocalUser u) {

      assert (a.has_user_perm(u.getUid()))
          : "Filesystem: removing permission for user "
              + u.getUid()
              + "that has no permission associated!";
      return new FilesystemPermission(a.remove_user_perm_not_owner(u.getUid()));
    }
  }

  private class RemoveGroupPermissionMethod implements RemovePermissionMethod {

    public FilesystemPermission remove(final fs_acl a, final LocalUser u) {

      assert (a.has_user_perm(u.getUid()))
          : "Filesystem: removing permission for group "
              + u.getUid()
              + "that has no permission associated!";
      return new FilesystemPermission(a.remove_group_perm_not_owner(u.getPrimaryGid()));
    }
  }

  private FilesystemPermission removePermissionTemplate(
      final LocalUser u,
      final String fileOrDirectory,
      final RemovePermissionMethod permissionMethod) {

    assert (null != u) : "Null LocalUser passed to Filesystem.removePermissionTemplate()";
    assert (null != fileOrDirectory)
        : "Null fileOrDirectory passed to Filesystem.removePermissionTemplate()";
    assert (null != permissionMethod)
        : "Null permissionMethod passed to Filesystem.removePermissionTemplate()";

    FilesystemPermission oldPermission;
    fs_acl acl = fs.new_acl();
    // do not allow concurrent operation on the same pathname
    Semaphore lock = locks.get(fileOrDirectory);
    try {
      lock.acquireUninterruptibly();
      acl.load(fileOrDirectory, false);
      oldPermission = permissionMethod.remove(acl, u);
      acl.enforce(fileOrDirectory);
    } finally {
      lock.release();
      locks.remove(fileOrDirectory);
    }
    return oldPermission;
  }

  private interface SetPermissionMethod {

    public FilesystemPermission apply(
        final fs_acl a, final LocalUser u, final FilesystemPermission p);
  }

  private class SetUserPermissionMethod implements SetPermissionMethod {

    public FilesystemPermission apply(
        final fs_acl a, final LocalUser u, final FilesystemPermission p) {

      if (a.has_user_perm(u.getUid())) {
        return new FilesystemPermission(a.set_user_perm(u.getUid(), p.toFsAclPermission()));
      } else {
        a.set_user_perm(u.getUid(), p.toFsAclPermission());
        return null;
      }
    }
  }

  private class GrantUserPermissionMethod implements SetPermissionMethod {

    public FilesystemPermission apply(
        final fs_acl a, final LocalUser u, final FilesystemPermission p) {

      if (a.has_user_perm(u.getUid())) {
        return new FilesystemPermission(a.grant_user_perm(u.getUid(), p.toFsAclPermission()));
      } else {
        a.grant_user_perm(u.getUid(), p.toFsAclPermission());
        return null;
      }
    }
  }

  private class RevokeUserPermissionMethod implements SetPermissionMethod {

    public FilesystemPermission apply(
        final fs_acl a, final LocalUser u, final FilesystemPermission p) {

      if (a.has_user_perm(u.getUid())) {
        return new FilesystemPermission(a.revoke_user_perm(u.getUid(), p.toFsAclPermission()));
      } else {
        return null;
      }
    }
  }

  private class SetGroupPermissionMethod implements SetPermissionMethod {

    public FilesystemPermission apply(
        final fs_acl a, final LocalUser u, final FilesystemPermission p) {

      if (a.has_group_perm(u.getPrimaryGid())) {
        return new FilesystemPermission(a.set_group_perm(u.getPrimaryGid(), p.toFsAclPermission()));
      } else {
        a.set_group_perm(u.getPrimaryGid(), p.toFsAclPermission());
        return null;
      }
    }
  }

  private class GrantGroupPermissionMethod implements SetPermissionMethod {

    public FilesystemPermission apply(
        final fs_acl a, final LocalUser u, final FilesystemPermission p) {

      if (a.has_group_perm(u.getPrimaryGid())) {
        return new FilesystemPermission(
            a.grant_group_perm(u.getPrimaryGid(), p.toFsAclPermission()));
      } else {
        a.grant_group_perm(u.getPrimaryGid(), p.toFsAclPermission());
        return null;
      }
    }
  }

  private class RevokeGroupPermissionMethod implements SetPermissionMethod {

    public FilesystemPermission apply(
        final fs_acl a, final LocalUser u, final FilesystemPermission p) {

      if (a.has_group_perm(u.getPrimaryGid())) {
        return new FilesystemPermission(
            a.revoke_group_perm(u.getPrimaryGid(), p.toFsAclPermission()));
      } else {
        return null;
      }
    }
  }

  private FilesystemPermission setPermissionTemplate(
      final LocalUser u,
      final String fileOrDirectory,
      final FilesystemPermission p,
      final SetPermissionMethod setPermissionMethod) {

    assert (null != u) : "Null LocalUser passed to Filesystem.setPermissionTemplate()";
    assert (null != fileOrDirectory)
        : "Null fileOrDirectory passed to Filesystem.setPermissionTemplate()";
    assert (null != p) : "Null FilesystemPermission passed to Filesystem.setPermissionTemplate()";
    assert (null != setPermissionMethod)
        : "Null permissionMethod passed to Filesystem.setPermissionTemplate()";

    FilesystemPermission oldPermission;
    fs_acl acl = fs.new_acl();
    // do not allow concurrent operation on the same pathname
    AclLockPoolElement lock = locks.get(fileOrDirectory);
    try {
      lock.acquireUninterruptibly();
      acl.load(fileOrDirectory, false);
      oldPermission = setPermissionMethod.apply(acl, u, p);
      acl.enforce(fileOrDirectory);
    } finally {
      lock.release();
      locks.remove(fileOrDirectory);
    }
    return oldPermission;
  }
}
