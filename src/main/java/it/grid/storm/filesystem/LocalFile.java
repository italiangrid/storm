/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * @file File.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @author EGRID - ICTP Trieste, for subsequent Modifications.
 * 
 *         The it.grid.storm.filesystem.File class
 */
/*
 * Copyright (c) 2006 Riccardo Murri <riccardo.murri@ictp.it> for the EGRID/INFN
 * joint project StoRM.
 * 
 * You may copy, modify and distribute this file under the same terms as StoRM
 * itself.
 */

package it.grid.storm.filesystem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.checksum.ChecksumAlgorithm;
import it.grid.storm.checksum.ChecksumManager;
import it.grid.storm.ea.StormEA;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;

public class LocalFile {

  private static final Logger log = LoggerFactory.getLogger(LocalFile.class);

  private final Filesystem fs;

  private final File localFile;

  public LocalFile(final LocalFile parent, final String name, final Filesystem fs)
      throws NullPointerException {

    this(new File(parent.localFile, name), fs);
  }

  public LocalFile(final String pathname, final Filesystem fs) throws NullPointerException {

    this(new File(pathname), fs);
  }

  public LocalFile(final String parent, final String name, final Filesystem fs)
      throws NullPointerException {

    this(new File(parent, name), fs);
  }

  private LocalFile(File localFile, Filesystem fs) {

    this.localFile = localFile;

    checkArgument(localFile.isAbsolute(), "Non-absolute path in LocalFile constructor");
    checkNotNull(fs, "Null filesystem in LocalFile constructor");

    this.fs = fs;
  }

  /**
   * Return <code>true</code> if the local user (to which the specified grid user is mapped to) can
   * operate on the specified <code>fileOrDirectory</code> in the mode given by
   * <code>accessMode</code>, according to the permissions set on the filesystem.
   */
  public boolean canAccess(final LocalUser u, final FilesystemPermission accessMode)
      throws CannotMapUserException {

    return fs.canAccess(u, localFile.getAbsolutePath(), accessMode);
  }

  /**
   * Method that creates a new empty file, as per contract of java.io.File: refer there for further
   * info.
   */
  public boolean createNewFile() throws IOException, SecurityException {

    return localFile.createNewFile();
  }

  /**
   * Method that deletes This file, as per contract of java.io.File: refer there for further info.
   */
  public boolean delete() throws SecurityException {

    return localFile.delete();
  }

  /**
   * Method that checks for the existence of This file, as per contract of java.io.File: refer there
   * for further info.
   */
  public boolean exists() throws SecurityException {

    return localFile.exists();
  }

  /**
   * Returns the absolute pathname string, as per contract of {@link java.io.File}.
   */
  public String getAbsolutePath() {

    return localFile.getAbsolutePath();
  }

  /**
   * Retrieves the checksum of the file from the corresponding extended attribute. If no checksum is
   * found it is computed (scheduled and computed by a separate thread) and stored in an extended
   * attribute.
   * 
   * @return the checksum of the file.
   */
  public String getDefaultChecksum() {

    if (isDirectory()) {
      log.warn("Cannot return checksum of a directory: {}", localFile.getAbsolutePath());
      return null;
    }
    try {
      return ChecksumManager.getInstance().getDefaultChecksum(localFile.getAbsolutePath());
    } catch (FileNotFoundException e) {
      log.error(e.getMessage(), e);
      return null;
    }

  }

  /**
   * Returns the algorithm used to compute checksums (as defined in the configuration file).
   * 
   * @return
   */
  public ChecksumAlgorithm getChecksumAlgorithm() {

    return ChecksumManager.getInstance().getDefaultAlgorithm();
  }

  /**
   * Return the <em>effective</em> permission a group has on this file. Loads the ACL for this file
   * or directory, and return the permission associated with the local account primary group of the
   * given {@link LocalUser} instance <i>u</i>. If no ACE for that group is found, return
   * {@link DefaultFilesystem#NONE}.
   * 
   * @param u the LocalUser whose local account primary GID's permissions are to be retrieved.
   * @return <em>effective</em> permission associated to the local account primary GID of the given
   *         LocalUser <i>u</i> in the given file ACL, or <code>null</code> if no ACL entry for that
   *         group was found.
   */
  public FilesystemPermission getEffectiveGroupPermission(final LocalUser u) {

    return fs.getEffectiveGroupPermission(u, localFile.getAbsolutePath());
  }

  /**
   * Return the <em>effective</em> permission a user has on this file. Loads the ACL for this file
   * or directory, and return the permission associated with the local account UID of the given
   * LocalUser <i>u</i>. If no ACE for that user is found, return {@link DefaultFilesystem#NONE}.
   * 
   * @param u the LocalUser whose permissions are to be retrieved.
   * @return <em>effective</em> permission associated to the local account UID of the given
   *         LocalUser <i>u</i> in this file ACL, or <code>null</code> if no ACL entry for that user
   *         was found.
   */
  public FilesystemPermission getEffectiveUserPermission(final LocalUser u) {

    return fs.getEffectiveUserPermission(u, localFile.getAbsolutePath());
  }

  /**
   * Return up-to-date file last modification time, as a UNIX epoch. Returned value may differ from
   * the size returned by {@link java.io.File#lastModified()} on filesystems that do metadata
   * caching (GPFS, for instance). Since it may force a metadata update on all cluster nodes, this
   * method may be <em>slow</em>.
   * 
   * @return time (seconds since the epoch) this file was last modified.
   * @see #lastModified()
   * @see #getLastModifiedTime()
   */
  public long getExactLastModifiedTime() {

    return fs.getExactLastModifiedTime(localFile.getAbsolutePath());
  }

  /**
   * Return up-to-date file size in bytes. Returned value may differ from the size returned by
   * {@link java.io.File#length()} on filesystems that do metadata caching (GPFS, for instance).
   * Since it may force a metadata update on all cluster nodes, this method may be <em>slow</em>.
   * 
   * @return size (in bytes) of this file
   * @see #length()
   * @see #getExactSize()
   */
  public long getExactSize() {

    return fs.getExactSize(localFile.getAbsolutePath());
  }

  /**
   * Return the permission a group has on this file. Loads the ACL for this file or directory, and
   * return the permission associated with the local account primary group of the given
   * {@link LocalUser} instance <i>u</i>. If no ACE for that group is found, return
   * {@link DefaultFilesystem#NONE}.
   * 
   * @param u the LocalUser whose local account primary GID's permissions are to be retrieved.
   * @return permission associated to the local account primary GID of the given LocalUser <i>u</i>
   *         in the given file ACL. or {@link DefaultFilesystem#NONE} if no ACE for that group was found.
   */
  public FilesystemPermission getGroupPermission(final LocalUser u) {

    return fs.getGroupPermission(u, localFile.getAbsolutePath());
  }

  public long getLastModifiedTime() {

    return fs.getLastModifiedTime(getAbsolutePath());
  }

  public LocalFile getParentFile() {

    File parent = localFile.getParentFile();
    if (parent == null) {
      return null;
    }
    return new LocalFile(parent.getAbsolutePath(), this.fs);
  }

  public String getPath() {

    return localFile.getPath();
  }

  public long getSize() {

    return localFile.length();
  }

  /**
   * Return the permission a user has on this file. Loads the ACL for this file or directory, and
   * return the permission associated with the local account UID of the given LocalUser <i>u</i>. If
   * no ACE for that user is found, return {@link DefaultFilesystem#NONE}.
   * 
   * @param u the LocalUser whose permissions are to be retrieved.
   * @return permission associated to the local account UID of the given LocalUser <i>u</i> in this
   *         file ACL, or {@link DefaultFilesystem#NONE} if no ACE for that user was found.
   */
  public FilesystemPermission getUserPermission(final LocalUser u) {

    return fs.getUserPermission(u, localFile.getAbsolutePath());
  }

  /**
   * Grant specified permission to a group, and return the former permission.
   * <p>
   * Adds the specified permission to the ones that the primary group of the given LocalUser
   * <i>u</i> already holds on this file or directory: all permission bits that are set in
   * <i>permission</i> will be set in the appropriate group ACE in the file ACL.
   * <p>
   * If no ACE is present for the specified group, then one is created and its permission value is
   * set to <i>permission</i>.
   * 
   * @param u the LocalUser whose local account primary GID's ACE is to be altered.
   * @param permission Capabilities to grant.
   * @return permission formerly associated to the local account primary GID of the given LocalUser
   *         <i>u</i> in this file ACL, or {@link DefaultFilesystem#NONE} if no ACE for that group was
   *         found.
   */
  public FilesystemPermission grantGroupPermission(final LocalUser u,
      final FilesystemPermission permission) {

    return fs.grantGroupPermission(u, localFile.getAbsolutePath(), permission);
  }

  /**
   * Grant specified permission to a user, and return the former permission.
   * <p>
   * Adds the specified permissions to the ones that the local account UID of the given LocalUser
   * <i>u</i> already holds on this file or directory: all permission bits that are set in
   * <i>permission</i> will be set in the appropriate user ACE in the file ACL.
   * <p>
   * If no ACE is present for the specified user, then one is created and its permission value is
   * set to <i>permission</i>.
   * 
   * @param u the LocalUser whose local account UID's ACE is to be altered.
   * @param permission Capabilities to grant.
   * @return permission formerly associated to the local account UID of the given LocalUser <i>u</i>
   *         in this file ACL, or {@link DefaultFilesystem#NONE} if no ACE for that user was found.
   */
  public FilesystemPermission grantUserPermission(final LocalUser u,
      final FilesystemPermission permission) {

    return fs.grantUserPermission(u, localFile.getAbsolutePath(), permission);
  }

  /**
   * Check if the file checksum is already set.
   * 
   * @return <code>true</code> if the checksum attribute is set, <code>false</code> otherwise.
   */
  public boolean hasDefaultChecksum() {
    try {
      return ChecksumManager.getInstance().hasDefaultChecksum(localFile.getAbsolutePath());
    } catch (FileNotFoundException e) {
      log.warn("File not found when checking checksum: {}", e.getMessage(), e);
      return false;
    }
  }

  public boolean isDirectory() throws SecurityException {
    return localFile.isDirectory();
  }

  /**
   * Returns <code>true</code> is the file is present on the disk, <code>false</code> otherwise.
   * 
   * @return <code>true</code> is the file is present on the disk, <code>false</code> otherwise.
   */
  public boolean isOnDisk() throws FSException {

    boolean isOnDisk = false;
    try {
      isOnDisk = fs.isFileOnDisk(localFile.getAbsolutePath());
    } catch (Throwable e) {
      log.error(e.getMessage());
      return false;
    }

    if (log.isDebugEnabled()) {
      log.debug("File {} is {} on disk.", localFile.getAbsolutePath(), (isOnDisk ? "" : "NOT"));
    }

    return isOnDisk;
  }

  /**
   * Returns <code>true</code> is the file is stored on the tape, <code>false</code> otherwise.
   * 
   * @return <code>true</code> is the file is stored on the tape, <code>false</code> otherwise.
   */
  public boolean isOnTape() {

    return StormEA.getMigrated(localFile.getAbsolutePath());
  }

  /**
   * Method that returns the size in bytes of This file, as per contract of java.io.File: refer
   * there for further info.
   */
  public long length() throws SecurityException {

    return getSize();
  }

  // overridden from java.io.File to change return value
  public LocalFile[] listFiles() {

    java.io.File[] _children = localFile.listFiles();
    LocalFile[] children = new LocalFile[_children.length];
    for (int i = 0; i < _children.length; ++i) {
      children[i] = new LocalFile(_children[i].getAbsolutePath(), this.fs);
    }
    return children;
  }

  /**
   * Method that creates a new directory, as per contract of java.io.File: refer there for further
   * info.
   */
  public boolean mkdir() throws SecurityException {

    return localFile.mkdir();
  }

  /**
   * Method that creates a new directory, as per contract of java.io.File: refer there for further
   * info.
   */
  public boolean mkdirs() throws SecurityException {

    return localFile.mkdirs();
  }

  /**
   * Return <code>true</code> if the parent directory of this pathname exists.
   */
  public boolean parentExists() {

    java.io.File parent = localFile.getParentFile();
    assert (null != parent) : "Null parent in " + this.toString();
    return parent.exists();
  }

  /**
   * Remove a group's ACE, and return the (now deleted) permission.
   * <p>
   * Removes the ACE (if any) of the primary group of the given LocalUser <i>u</i> from this file or
   * directory ACL. Returns the permission formerly associated with that group.
   * <p>
   * If the given group is the file owning group, then its ACE is set to {@link DefaultFilesystem#NONE},
   * rather than removed.
   * 
   * @param u the LocalUser whose local account primary GID's ACE is to be altered.
   * @return permission formerly associated to the local account primary GID of the given LocalUser
   *         <i>u</i> in this file ACL, or {@link DefaultFilesystem#NONE} if no ACE for that group was
   *         found.
   */
  public FilesystemPermission removeGroupPermission(final LocalUser u) {

    return fs.removeGroupPermission(u, localFile.getAbsolutePath());
  }

  /**
   * Remove a user's ACE, and return the (now deleted) permission.
   * <p>
   * Removes the ACE (if any) of the primary user of the given LocalUser <i>u</i> from this file or
   * directory ACL. Returns the permission formerly associated with that user.
   * <p>
   * If the given user is the file owner, then its ACE is set to {@link DefaultFilesystem#NONE}, rather
   * than removed.
   * 
   * @param u the LocalUser whose local account UID's ACE is to be altered.
   * @return permission formerly associated to the local account UID of the given LocalUser <i>u</i>
   *         in this file ACL, or {@link DefaultFilesystem#NONE} if no ACE for that user was found.
   */
  public FilesystemPermission removeUserPermission(final LocalUser u) {

    return fs.removeUserPermission(u, localFile.getAbsolutePath());
  }

  /**
   * Method that renames This file, as per contract of java.io.File: refer there for further info.
   * The only notable difference is that this method requires a String rather than java.io.File
   * parameter.
   */
  public boolean renameTo(String newName) throws SecurityException, NullPointerException {

    return localFile.renameTo(new File(newName));
  }

  /**
   * Revoke specified permission from a group's ACE, and return the former permission.
   * <p>
   * Removes the specified permission from the ones that the primary group of the given LocalUser
   * <i>u</i> local account already holds on this file or directory: all permission bits that are
   * <em>set</em> in <i>permission</i> will be <em>cleared</em> in the appropriate group ACE in the
   * file ACL.
   * <p>
   * If no ACE is present for the specified group, then one is created and its permission value is
   * set to {@link DefaultFilesystem#NONE}.
   * 
   * @param u the LocalUser whose local account primary GID's ACE is to be altered.
   * @param permission Capabilities to revoke.
   * @return permission formerly associated to the local account primary GID of the given LocalUser
   *         <i>u</i> in this file ACL, or {@link DefaultFilesystem#NONE} if no ACE for that group was
   *         found.
   * @see fs_acl::revoke_group_perm()
   */
  public FilesystemPermission revokeGroupPermission(final LocalUser u,
      final FilesystemPermission permission) {

    return fs.revokeGroupPermission(u, localFile.getAbsolutePath(), permission);
  }

  /**
   * Revoke specified permission from a user's ACE, and return the former permission.
   * <p>
   * Removes the specified permission from the ones that the primary user of the given LocalUser
   * <i>u</i> local account already holds on this file or directory: all permission bits that are
   * <em>set</em> in <i>permission</i> will be <em>cleared</em> in the appropriate user ACE in the
   * file ACL.
   * <p>
   * If no ACE is present for the specified user, then one is created and its permission value is
   * set to {@link DefaultFilesystem#NONE}.
   * 
   * @param u the LocalUser whose local account UID's ACE is to be altered.
   * @param permission Capabilities to revoke.
   * @return permission formerly associated to the local account UID of the given LocalUser <i>u</i>
   *         in this file ACL, or {@link DefaultFilesystem#NONE} if no ACE for that user was found.
   */
  public FilesystemPermission revokeUserPermission(final LocalUser u,
      final FilesystemPermission permission) {

    return fs.revokeUserPermission(u, localFile.getAbsolutePath(), permission);
  }

  /**
   * Change file group.
   * 
   * @param groupName name of the group
   * @return <code>true</code> if the group was correctly set, <code>false</code> otherwise
   */
  public void setGroupOwnership(String groupName) throws FSException {

    try {

      fs.changeFileGroupOwnership(localFile.getAbsolutePath(), groupName);

    } catch (FilesystemError filesystemError) {

      throw new FSException(filesystemError);

    }
  }

  /**
   * Set the specified permission in a group's ACE, and return the former permission.
   * <p>
   * Sets the ACE of the primary group of the given LocalUser <i>u</i> to the given
   * <i>permission</i>. Returns the permission formerly associated with that group.
   * 
   * @param u the localUser whose local account primary GID's ACE is to be altered.
   * @param permission Permission to set in the group ACE.
   * @return permission formerly associated to the local account primary GID of the given LocalUser
   *         <i>u</i> in this file ACL, or {@link DefaultFilesystem#NONE} if no ACE for that group was
   *         found.
   */
  public FilesystemPermission setGroupPermission(final LocalUser u,
      final FilesystemPermission permission) {

    return fs.setGroupPermission(u, localFile.getAbsolutePath(), permission);
  }

  /**
   * Set the specified permission in a user's ACE on a file or directory, and return the former
   * permission.
   * <p>
   * Sets the ACE of the primary user of the given LocalUser <i>u</i> to the given
   * <i>permission</i>. Returns the permission formerly associated with that user.
   * 
   * @param u the Grid user whose local account UID's ACE is to be altered.
   * @param permission Permission to set in the user ACE.
   * @return permission formerly associated to the local account UID of the given LocalUser <i>u</i>
   *         in this file ACL, or {@link DefaultFilesystem#NONE} if no ACE for that user was found.
   */
  public FilesystemPermission setUserPermission(final LocalUser u,
      final FilesystemPermission permission) {

    return fs.setUserPermission(u, localFile.getAbsolutePath(), permission);
  }

  /**
   * Return a string representation of this object.
   */
  @Override
  public String toString() {

    return File.class.toString() + ":" + localFile.toString();
  }

  /**
   * Truncate the file to the desired size
   * 
   * @param desired_size
   * @return
   */

  public int truncateFile(long desired_size) {

    return fs.truncateFile(localFile.getAbsolutePath(), desired_size);
  }

  /**
   * Return the unique absolute canonical path of the file
   */
  public String getCanonicalPath() throws IOException {

    return localFile.getCanonicalPath();
  }

}
