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

import it.grid.storm.checksum.ChecksumManager;
import it.grid.storm.ea.StormEA;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Façade for operations on a filesystem entry (file or directory). All
 * operations on the filesystem should be performed by creating an instance of
 * the {@link it.grid.storm.filesystem.File} class, and using its methods to
 * create or modify a filesystem entry. From {@link java.io.File} the following
 * methods are inherited: {@link java.io.File#delete() delete},
 * {@link java.io.File#exists() exists}, {@link java.io.File#getAbsolutePath()
 * getAbsolutePath}, {@link java.io.File#getName() getName},
 * {@link java.io.File#isDirectory() isDirectory}, {@link java.io.File#isFile()
 * isFile}, {@link java.io.File#list() list}, {@link java.io.File#mkdir() mkdir}
 * , {@link java.io.File#mkdirs() mkdirs}; the methods
 * {@link java.io.File#getParentFile() getParentFile} and
 * {@link java.io.File#listFiles() listFiles} have been overridden to return
 * {@link it.grid.storm.filesystem.File} objects instead of {@link java.io.File}
 * ones. Additional methods are provided to manipulate this file's ACL in a
 * filesystem-indepenent way; all ACL operations take a LocalUser identity (see
 * {@link it.grid.storm.griduser.LocalUser}) and a {@link FilesystemPermission}
 * permission representation. A pathname need not exist for an associated
 * it.grid.storm.filesystem.File object to be created; indeed, you should create
 * an instance associated to a non existing pathname and then invoke
 * {@link #create()} or {@link #makeDirectory()} to create a file or a
 * directory, respectively. The constructor for this class requires a pathname
 * and a {@link Filesystem} object. The passed {@link Filesystem} object need to
 * match the filesystem-type of the filesystem the passed pathname resides on.
 * Therefore, {@link it.grid.storm.filesystem.File} objects can only be created
 * from a factory method that has all the required pieces of information;
 * {@link it.grid.storm.namespace.StoRI} looks like the right class for that.
 * 
 * @see java.io.File
 * @see it.grid.storm.namespace.StoRI
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @author EGRID - ICTP Trieste, for minor changes.
 * @version $Revision: 1.8 $
 **/
public class LocalFile {

	// ---- public factory methods ----

	// ---- class private variables ----

	/** Log4J logger. */
	private static final Logger log = LoggerFactory.getLogger(LocalFile.class);

	// ---- instance private variables ----

	/** The Filesystem interface to operate on the wrapped pathname. */
	private final Filesystem fs;
	private final java.io.File localFile; // Instance of java.io.File to the real
																				// local file!

	// ---- constructors ----

	/**
	 * Constructor, taking parent pathname (as a {@link File}), child name (as a
	 * <code>String</code> ) and the hosting filesystem object. From <i>parent</i>
	 * and <i>child</i> an absolute pathname must result, following the rules in
	 * {@link java.io.File#File(java.io.File,String)}.
	 * 
	 * @param parent
	 *          {@link File} instance of the parent directory
	 * @param child
	 *          pathname of the child
	 * @param fs
	 *          The {@link it.grid.storm.filesystem.Filesystem} object to use for
	 *          operations on this file.
	 * @see it.grid.storm.filesystem.Filesystem
	 * @see java.io.File#File(java.io.File,String)
	 */
	public LocalFile(final LocalFile parent, final String name,
		final Filesystem fs) throws NullPointerException {

		localFile = new java.io.File(parent.localFile, name);

		assert (localFile.isAbsolute()) : "Non-absolute path in constructor File(File,String,Filesystem)";
		assert (null != fs) : "Null filesystem in constructor File(File,String,Filesystem)";

		this.fs = fs;
	}

	/**
	 * Constructor, taking string pathname and the hosting filesystem object. The
	 * <code>path</code> parameter must be a non-empty absolute pathname.
	 * 
	 * @param pathname
	 *          The pathname wrapped by this PFN; <em>must</em> be absolute, or
	 *          code will fail in an assertion.
	 * @param fs
	 *          The {@link it.grid.storm.filesystem.Filesystem} object to use for
	 *          operations on this file.
	 * @see it.grid.storm.filesystem.Filesystem
	 */
	public LocalFile(final String pathname, final Filesystem fs)
		throws NullPointerException {

		localFile = new java.io.File(pathname);

		assert (localFile.isAbsolute()) : "Non-absolute path in constructor File(String,Filesystem)";
		assert (null != fs) : "Null Filesystem in constructor File(String,Filesystem)";

		this.fs = fs;
	}

	/**
	 * Constructor, taking parent pathname (as a <code>String</code>), child name
	 * (as a <code>String</code>) and the hosting filesystem object. From
	 * <i>parent</i> and <i>child</i> an absolute pathname must result, following
	 * the rules in {@link java.io.File#File(String,String)}.
	 * 
	 * @param parent
	 *          pathname of the parent directory
	 * @param child
	 *          pathname of the child
	 * @param fs
	 *          The {@link it.grid.storm.filesystem.Filesystem} object to use for
	 *          operations on this file.
	 * @see it.grid.storm.filesystem.Filesystem
	 * @see java.io.File#File(String,String)
	 */
	public LocalFile(final String parent, final String name, final Filesystem fs)
		throws NullPointerException {

		localFile = new java.io.File(parent, name);

		assert (localFile.isAbsolute()) : "Non-absolute path in constructor File(String,String,Filesystem)";
		assert (null != fs) : "Null Filesystem in constructor File(String,String,Filesystem)";

		this.fs = fs;
	}

	// ---- public accessor methods ----

	/**
	 * Return <code>true</code> if the local user (to which the specified grid
	 * user is mapped to) can operate on the specified
	 * <code>fileOrDirectory</code> in the mode given by <code>accessMode</code>,
	 * according to the permissions set on the filesystem.
	 */
	public boolean canAccess(final LocalUser u,
		final FilesystemPermission accessMode) throws CannotMapUserException {

		return fs.canAccess(u, localFile.getAbsolutePath(), accessMode);
	}

	/**
	 * Method that creates a new empty file, as per contract of java.io.File:
	 * refer there for further info.
	 */
	public boolean createNewFile() throws IOException, SecurityException {

		return localFile.createNewFile();
	}

	/**
	 * Method that deletes This file, as per contract of java.io.File: refer there
	 * for further info.
	 */
	public boolean delete() throws SecurityException {

		return localFile.delete();
	}

	/**
	 * Method that checks for the existence of This file, as per contract of
	 * java.io.File: refer there for further info.
	 */
	public boolean exists() throws SecurityException {

		return localFile.exists();
	}

	/**
	 * Returns the absolute pathname string, as per contract of
	 * {@link java.io.File}.
	 */
	public String getAbsolutePath() {

		return localFile.getAbsolutePath();
	}

	/**
	 * Retrieves the checksum of the file from the corresponding extended
	 * attribute. If no checksum is found it is computed (scheduled and computed
	 * by a separate thread) and stored in an extended attribute.
	 * 
	 * @return the checksum of the file.
	 */
	public String getDefaultChecksum() {

		try {
			return ChecksumManager.getInstance().getDefaultChecksum(
				localFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			log.warn("Unable to get local file checksum. FileNotFoundException: "
				+ e.getMessage());
			return null;
		}

	}

	public Map<String, String> getChecksums() {

		try {
			return ChecksumManager.getInstance().getChecksums(
				localFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			log.warn("Unable to get local file checksum. FileNotFoundException: "
				+ e.getMessage());
			return new HashMap<String, String>(0);
		}

	}

	/**
	 * Returns the algorithm used to compute checksums (as defined in the
	 * configuration file).
	 * 
	 * @return
	 */
	public String getChecksumAlgorithm() {

		return ChecksumManager.getInstance().getDefaultAlgorithm();
	}

	/**
	 * Return the <em>effective</em> permission a group has on this file. Loads
	 * the ACL for this file or directory, and return the permission associated
	 * with the local account primary group of the given {@link LocalUser}
	 * instance <i>u</i>. If no ACE for that group is found, return
	 * {@link Filesystem#NONE}.
	 * 
	 * @param u
	 *          the LocalUser whose local account primary GID's permissions are to
	 *          be retrieved.
	 * @return <em>effective</em> permission associated to the local account
	 *         primary GID of the given LocalUser <i>u</i> in the given file ACL,
	 *         or <code>null</code> if no ACL entry for that group was found.
	 */
	public FilesystemPermission getEffectiveGroupPermission(final LocalUser u) {

		return fs.getEffectiveGroupPermission(u, localFile.getAbsolutePath());
	}

	/**
	 * Return the <em>effective</em> permission a user has on this file. Loads the
	 * ACL for this file or directory, and return the permission associated with
	 * the local account UID of the given LocalUser <i>u</i>. If no ACE for that
	 * user is found, return {@link Filesystem#NONE}.
	 * 
	 * @param u
	 *          the LocalUser whose permissions are to be retrieved.
	 * @return <em>effective</em> permission associated to the local account UID
	 *         of the given LocalUser <i>u</i> in this file ACL, or
	 *         <code>null</code> if no ACL entry for that user was found.
	 */
	public FilesystemPermission getEffectiveUserPermission(final LocalUser u) {

		return fs.getEffectiveUserPermission(u, localFile.getAbsolutePath());
	}

	/**
	 * Return up-to-date file last modification time, as a UNIX epoch. Returned
	 * value may differ from the size returned by
	 * {@link java.io.File#lastModified()} on filesystems that do metadata caching
	 * (GPFS, for instance). Since it may force a metadata update on all cluster
	 * nodes, this method may be <em>slow</em>.
	 * 
	 * @return time (seconds since the epoch) this file was last modified.
	 * @see #lastModified()
	 * @see #getLastModifiedTime()
	 */
	public long getExactLastModifiedTime() {

		return fs.getExactLastModifiedTime(localFile.getAbsolutePath());
	}

	/**
	 * Return up-to-date file size in bytes. Returned value may differ from the
	 * size returned by {@link java.io.File#length()} on filesystems that do
	 * metadata caching (GPFS, for instance). Since it may force a metadata update
	 * on all cluster nodes, this method may be <em>slow</em>.
	 * 
	 * @return size (in bytes) of this file
	 * @see #length()
	 * @see #getExactSize()
	 */
	public long getExactSize() {

		return fs.getExactSize(localFile.getAbsolutePath());
	}

	/**
	 * Return the permission a group has on this file. Loads the ACL for this file
	 * or directory, and return the permission associated with the local account
	 * primary group of the given {@link LocalUser} instance <i>u</i>. If no ACE
	 * for that group is found, return {@link Filesystem#NONE}.
	 * 
	 * @param u
	 *          the LocalUser whose local account primary GID's permissions are to
	 *          be retrieved.
	 * @return permission associated to the local account primary GID of the given
	 *         LocalUser <i>u</i> in the given file ACL. or
	 *         {@link Filesystem#NONE} if no ACE for that group was found.
	 */
	public FilesystemPermission getGroupPermission(final LocalUser u) {

		return fs.getGroupPermission(u, localFile.getAbsolutePath());
	}

	/**
	 * Return (possibly cached) file last modification time, as a UNIX epoch. Same
	 * as {@link #lastModified()}, although this uses the host Filesystem native
	 * <code>stat()</code> -like calls. May return inaccurate results, as some
	 * filesystems (notably GPFS) provide the choice between filesystem-specific
	 * calls for accurate reporting (slower, as it may imply synchronizing the
	 * metadata cache) or standard libc calls that report possibly outdated
	 * information.
	 * 
	 * @return time (seconds since the epoch) this file was last modified.
	 * @see #lastModified()
	 * @see #getExactLastModifiedTime()
	 */
	public long getLastModifiedTime() {

		return fs.getLastModifiedTime(localFile.getAbsolutePath());
	}

	// overridden from java.io.File to change return value
	public LocalFile getParentFile() {

		return new LocalFile(localFile.getParentFile().getAbsolutePath(), this.fs);
	}

	/**
	 * Method that returns a String representing the path of This file, as per
	 * contract of java.io.File: refer there for further info.
	 */
	public String getPath() {

		return localFile.getPath();
	}

	/**
	 * Return the (possibly outdated) size in bytes of this file. Same as
	 * {@link #length()}, although this uses the host Filesystem native
	 * <code>stat()</code>-like calls. May return inaccurate results, as some
	 * filesystems (notably GPFS) provide the choice between filesystem-specific
	 * calls for accurate reporting (slower, as it may imply synchronizing the
	 * metadata cache) or standard libc calls that report possibly outdated
	 * information.
	 * 
	 * @return size (in bytes) of this file
	 * @see #length()
	 * @see #getExactSize()
	 */
	public long getSize() {

		long result = 0;
		String filename = localFile.getAbsolutePath();
		File file = new File(filename);
		if (file.exists()) {
			result = file.length();
		}
		return result;
		// return fs.getSize(localFile.getAbsolutePath());
	}

	/**
	 * Return the permission a user has on this file. Loads the ACL for this file
	 * or directory, and return the permission associated with the local account
	 * UID of the given LocalUser <i>u</i>. If no ACE for that user is found,
	 * return {@link Filesystem#NONE}.
	 * 
	 * @param u
	 *          the LocalUser whose permissions are to be retrieved.
	 * @return permission associated to the local account UID of the given
	 *         LocalUser <i>u</i> in this file ACL, or {@link Filesystem#NONE} if
	 *         no ACE for that user was found.
	 */
	public FilesystemPermission getUserPermission(final LocalUser u) {

		return fs.getUserPermission(u, localFile.getAbsolutePath());
	}

	/**
	 * Grant specified permission to a group, and return the former permission.
	 * <p>
	 * Adds the specified permission to the ones that the primary group of the
	 * given LocalUser <i>u</i> already holds on this file or directory: all
	 * permission bits that are set in <i>permission</i> will be set in the
	 * appropriate group ACE in the file ACL.
	 * <p>
	 * If no ACE is present for the specified group, then one is created and its
	 * permission value is set to <i>permission</i>.
	 * 
	 * @param u
	 *          the LocalUser whose local account primary GID's ACE is to be
	 *          altered.
	 * @param permission
	 *          Capabilities to grant.
	 * @return permission formerly associated to the local account primary GID of
	 *         the given LocalUser <i>u</i> in this file ACL, or
	 *         {@link Filesystem#NONE} if no ACE for that group was found.
	 */
	// TODO ACL manager
	// public FilesystemPermission grantGroupPermission(final LocalUser u, final
	// FilesystemPermission permission)
	// throws CannotMapUserException {
	public FilesystemPermission grantGroupPermission(final LocalUser u,
		final FilesystemPermission permission) {

		return fs.grantGroupPermission(u, localFile.getAbsolutePath(), permission);
	}

	/**
	 * Grant specified permission to a user, and return the former permission.
	 * <p>
	 * Adds the specified permissions to the ones that the local account UID of
	 * the given LocalUser <i>u</i> already holds on this file or directory: all
	 * permission bits that are set in <i>permission</i> will be set in the
	 * appropriate user ACE in the file ACL.
	 * <p>
	 * If no ACE is present for the specified user, then one is created and its
	 * permission value is set to <i>permission</i>.
	 * 
	 * @param u
	 *          the LocalUser whose local account UID's ACE is to be altered.
	 * @param permission
	 *          Capabilities to grant.
	 * @return permission formerly associated to the local account UID of the
	 *         given LocalUser <i>u</i> in this file ACL, or
	 *         {@link Filesystem#NONE} if no ACE for that user was found.
	 */
	// TODO ACL manager
	// public FilesystemPermission grantUserPermission(final LocalUser u, final
	// FilesystemPermission permission)
	// throws CannotMapUserException {
	public FilesystemPermission grantUserPermission(final LocalUser u,
		final FilesystemPermission permission) {

		return fs.grantUserPermission(u, localFile.getAbsolutePath(), permission);
	}

	/**
	 * Check if the file checksum is already set.
	 * 
	 * @return <code>true</code> if the checksum attribute is set,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasChecksum() {

		try {
			return ChecksumManager.getInstance().hasChecksum(
				localFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			log
				.warn("Unable to verify if local file has the checksum. FileNotFoundException: "
					+ e.getMessage());
			return false;
		}
	}

	/**
	 * Method used to find out if This File is a directory or not, as per contract
	 * of java.io.File: refer thre for further info.
	 */
	public boolean isDirectory() throws SecurityException {

		return localFile.isDirectory();
	}

	private boolean isGPFS() throws FSException {

		try {
			return FileSystemCheckerFactory.getInstance().createFileSystemChecker()
				.isGPFS(this.localFile);
		} catch (Exception e) {
			log.error("Unable to check if file " + this.localFile.getAbsolutePath()
				+ " is on GPFS. IllegalArgumentException " + e.getMessage());
			throw new FSException("Unable to check if file "
				+ this.localFile.getAbsolutePath()
				+ " is on GPFS. IllegalArgumentException " + e.getMessage());
		}
	}

	/**
	 * Returns <code>true</code> is the file is present on the disk,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> is the file is present on the disk,
	 *         <code>false</code> otherwise.
	 */
	public boolean isOnDisk() throws FSException {

		if (!isGPFS()) {
			return localFile.exists();
		} else {

			long fileSizeInBlocks = fs.getFileBlockSize(localFile.getAbsolutePath());

			if (fileSizeInBlocks >= localFile.length()) {

				log.debug("File is on disk: blockSize=" + fileSizeInBlocks
					+ " fileSize=" + localFile.length() + " file="
					+ localFile.getAbsolutePath());
				return true;

			} else {
				log.debug("File is NOT on disk: blockSize=" + fileSizeInBlocks
					+ " fileSize=" + localFile.length() + " file="
					+ localFile.getAbsolutePath());
				return false;
			}
		}
	}

	/**
	 * Returns <code>true</code> is the file is stored on the tape,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> is the file is stored on the tape,
	 *         <code>false</code> otherwise.
	 */
	public boolean isOnTape() {

		return StormEA.getMigrated(localFile.getAbsolutePath());
	}

	// --- public ACL manipulation methods --- //

	/**
	 * Method that returns the size in bytes of This file, as per contract of
	 * java.io.File: refer there for further info.
	 */
	public long length() throws SecurityException {

		return localFile.length();
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
	 * Method that creates a new directory, as per contract of java.io.File: refer
	 * there for further info.
	 */
	public boolean mkdir() throws SecurityException {

		return localFile.mkdir();
	}

	/**
	 * Method that creates a new directory, as per contract of java.io.File: refer
	 * there for further info.
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
	 * Removes the ACE (if any) of the primary group of the given LocalUser
	 * <i>u</i> from this file or directory ACL. Returns the permission formerly
	 * associated with that group.
	 * <p>
	 * If the given group is the file owning group, then its ACE is set to
	 * {@link Filesystem#NONE}, rather than removed.
	 * 
	 * @param u
	 *          the LocalUser whose local account primary GID's ACE is to be
	 *          altered.
	 * @return permission formerly associated to the local account primary GID of
	 *         the given LocalUser <i>u</i> in this file ACL, or
	 *         {@link Filesystem#NONE} if no ACE for that group was found.
	 */
	// TODO ACL manager
	// public FilesystemPermission removeGroupPermission(final LocalUser u) throws
	// CannotMapUserException {
	public FilesystemPermission removeGroupPermission(final LocalUser u) {

		return fs.removeGroupPermission(u, localFile.getAbsolutePath());
	}

	/**
	 * Remove a user's ACE, and return the (now deleted) permission.
	 * <p>
	 * Removes the ACE (if any) of the primary user of the given LocalUser
	 * <i>u</i> from this file or directory ACL. Returns the permission formerly
	 * associated with that user.
	 * <p>
	 * If the given user is the file owner, then its ACE is set to
	 * {@link Filesystem#NONE}, rather than removed.
	 * 
	 * @param u
	 *          the LocalUser whose local account UID's ACE is to be altered.
	 * @return permission formerly associated to the local account UID of the
	 *         given LocalUser <i>u</i> in this file ACL, or
	 *         {@link Filesystem#NONE} if no ACE for that user was found.
	 */
	// TODO ACL manager
	// public FilesystemPermission removeUserPermission(final LocalUser u) throws
	// CannotMapUserException {
	public FilesystemPermission removeUserPermission(final LocalUser u) {

		return fs.removeUserPermission(u, localFile.getAbsolutePath());
	}

	/**
	 * Method that renames This file, as per contract of java.io.File: refer there
	 * for further info. The only notable difference is that this method requires
	 * a String rather than java.io.File parameter.
	 */
	public boolean renameTo(String newName) throws SecurityException,
		NullPointerException {

		return localFile.renameTo(new File(newName));
	}

	/**
	 * Revoke specified permission from a group's ACE, and return the former
	 * permission.
	 * <p>
	 * Removes the specified permission from the ones that the primary group of
	 * the given LocalUser <i>u</i> local account already holds on this file or
	 * directory: all permission bits that are <em>set</em> in <i>permission</i>
	 * will be <em>cleared</em> in the appropriate group ACE in the file ACL.
	 * <p>
	 * If no ACE is present for the specified group, then one is created and its
	 * permission value is set to {@link Filesystem#NONE}.
	 * 
	 * @param u
	 *          the LocalUser whose local account primary GID's ACE is to be
	 *          altered.
	 * @param permission
	 *          Capabilities to revoke.
	 * @return permission formerly associated to the local account primary GID of
	 *         the given LocalUser <i>u</i> in this file ACL, or
	 *         {@link Filesystem#NONE} if no ACE for that group was found.
	 * @see fs_acl::revoke_group_perm()
	 */
	// TODO ACL manager
	// public FilesystemPermission revokeGroupPermission(final LocalUser u, final
	// FilesystemPermission permission)
	// throws CannotMapUserException {
	public FilesystemPermission revokeGroupPermission(final LocalUser u,
		final FilesystemPermission permission) {

		return fs.revokeGroupPermission(u, localFile.getAbsolutePath(), permission);
	}

	/**
	 * Revoke specified permission from a user's ACE, and return the former
	 * permission.
	 * <p>
	 * Removes the specified permission from the ones that the primary user of the
	 * given LocalUser <i>u</i> local account already holds on this file or
	 * directory: all permission bits that are <em>set</em> in <i>permission</i>
	 * will be <em>cleared</em> in the appropriate user ACE in the file ACL.
	 * <p>
	 * If no ACE is present for the specified user, then one is created and its
	 * permission value is set to {@link Filesystem#NONE}.
	 * 
	 * @param u
	 *          the LocalUser whose local account UID's ACE is to be altered.
	 * @param permission
	 *          Capabilities to revoke.
	 * @return permission formerly associated to the local account UID of the
	 *         given LocalUser <i>u</i> in this file ACL, or
	 *         {@link Filesystem#NONE} if no ACE for that user was found.
	 */
	// TODO ACL manager
	// public FilesystemPermission revokeUserPermission(final LocalUser u, final
	// FilesystemPermission permission)
	// throws CannotMapUserException {
	public FilesystemPermission revokeUserPermission(final LocalUser u,
		final FilesystemPermission permission) {

		return fs.revokeUserPermission(u, localFile.getAbsolutePath(), permission);
	}

	/**
	 * Change file group.
	 * 
	 * @param groupName
	 *          name of the group
	 * @return <code>true</code> if the group was correctly set,
	 *         <code>false</code> otherwise
	 */
	public void setGroupOwnership(String groupName) throws FSException {

		try {
		
			fs.changeFileGroupOwnership(localFile.getAbsolutePath(), groupName);

		} catch(FilesystemError filesystemError) {

			throw new FSException(filesystemError);
			
		}
	}

	/**
	 * Set the specified permission in a group's ACE, and return the former
	 * permission.
	 * <p>
	 * Sets the ACE of the primary group of the given LocalUser <i>u</i> to the
	 * given <i>permission</i>. Returns the permission formerly associated with
	 * that group.
	 * 
	 * @param u
	 *          the localUser whose local account primary GID's ACE is to be
	 *          altered.
	 * @param permission
	 *          Permission to set in the group ACE.
	 * @return permission formerly associated to the local account primary GID of
	 *         the given LocalUser <i>u</i> in this file ACL, or
	 *         {@link Filesystem#NONE} if no ACE for that group was found.
	 */
	// TODO ACL manager
	// public FilesystemPermission setGroupPermission(final LocalUser u, final
	// FilesystemPermission permission)
	// throws CannotMapUserException {
	public FilesystemPermission setGroupPermission(final LocalUser u,
		final FilesystemPermission permission) {

		return fs.setGroupPermission(u, localFile.getAbsolutePath(), permission);
	}

	/**
	 * Set the specified permission in a user's ACE on a file or directory, and
	 * return the former permission.
	 * <p>
	 * Sets the ACE of the primary user of the given LocalUser <i>u</i> to the
	 * given <i>permission</i>. Returns the permission formerly associated with
	 * that user.
	 * 
	 * @param u
	 *          the Grid user whose local account UID's ACE is to be altered.
	 * @param permission
	 *          Permission to set in the user ACE.
	 * @return permission formerly associated to the local account UID of the
	 *         given LocalUser <i>u</i> in this file ACL, or
	 *         {@link Filesystem#NONE} if no ACE for that user was found.
	 */
	// TODO ACL manager
	// public FilesystemPermission setUserPermission(final LocalUser u, final
	// FilesystemPermission permission)
	// throws CannotMapUserException {
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
