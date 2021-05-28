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
 * @file FilesystemPermission.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * 
 *         The it.grid.storm.filesystem.FilesystemPermission class.
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

/**
 * Provides an abstraction of all operations that can be performed on a filesystem entry (file or
 * directory).
 * 
 * <p>
 * Note: this class is an interface to the fs_acl::permission_t type; if the low-level
 * fs_acl::permission_t type ever gets modified, then the {@link toFsAclPermission()} method should
 * be modified also.
 * 
 * <p>
 * To all effects, instances of this class are <em>immutable</em>. Permissions are read off or
 * enforced onto disk files; they should not be altered by StoRM code. Still, if there is need for
 * permission manipulation in StoRM, a MutableFilesystemPermission derived class can be provided,
 * that promotes to public visibility the deny*() and permit*() methods.
 * 
 * 
 * @see it.grid.storm.authorization.AuthorizationQueryInterface
 * @see fs_acl::permission_t
 * 
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @author EGRID - ICTP Trieste
 * @version $Revision: 1.19 $
 */
public class FilesystemPermission implements java.io.Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  // --- constants used in the bitfield constructor --- //

  /** Permission to execute the file. */
  final static int EXECUTE = fs_acl.permission_flags.PERM_EXECUTE;

  /** Permission to write file contents. */
  final static int WRITE_DATA = fs_acl.permission_flags.PERM_WRITE_DATA;

  /** Permission to read file contents. */
  final static int READ_DATA = fs_acl.permission_flags.PERM_READ_DATA;

  /**
   * Permission to change file extended ACL (that is, beyond normal UNIX permission bits).
   */
  final static int WRITE_ACL = fs_acl.permission_flags.PERM_WRITE_ACL;

  /**
   * Permission to read file extended ACL (that is, beyond normal UNIX permission bits).
   */
  final static int READ_ACL = fs_acl.permission_flags.PERM_READ_ACL;

  /** Permission to delete a filesystem entry (file or directory). */
  final static int DELETE = fs_acl.permission_flags.PERM_DELETE;

  /** Permission to descend to children directories of a directory. */
  final static int TRAVERSE_DIRECTORY = fs_acl.permission_flags.PERM_TRAVERSE_DIRECTORY;

  /** Permission to list directory contents. */
  final static int LIST_DIRECTORY = fs_acl.permission_flags.PERM_LIST_DIRECTORY;

  /** Permission to create a child subdirectory. */
  final static int CREATE_SUBDIRECTORY = fs_acl.permission_flags.PERM_CREATE_SUBDIRECTORY;

  /** Permission to create a new file. */
  final static int CREATE_FILE = fs_acl.permission_flags.PERM_CREATE_FILE;

  /** Permission to delete a file or directory within a directory. */
  final static int DELETE_CHILD = fs_acl.permission_flags.PERM_DELETE_CHILD;

  /** No permission at all. */
  final static int NONE = fs_acl.permission_flags.PERM_NONE;

  /** All permission bits set. */
  final static int ALL = fs_acl.permission_flags.PERM_ALL;

  // --- public constant instances --- //

  /** Permission to read a file. */
  public final static FilesystemPermission Read = new FilesystemPermission(READ_DATA);

  /** Permission to read and write to a file. */
  public final static FilesystemPermission ReadWrite =
      new FilesystemPermission(READ_DATA | WRITE_DATA);

  /** Permission to list directory contents. */
  public final static FilesystemPermission List = new FilesystemPermission(LIST_DIRECTORY);

  /**
   * Permission to traverse directory (descend path where directory is an intermediate step).
   */
  public final static FilesystemPermission Traverse = new FilesystemPermission(TRAVERSE_DIRECTORY);

  /** Permission to list and traverse directory. */
  public final static FilesystemPermission ListTraverse =
      new FilesystemPermission(LIST_DIRECTORY | TRAVERSE_DIRECTORY);

  /** Permission to list, traverse and write directory. */
  public final static FilesystemPermission ListTraverseWrite =
      new FilesystemPermission(LIST_DIRECTORY | TRAVERSE_DIRECTORY | WRITE_DATA);

  /** No permission at all. */
  public final static FilesystemPermission None =
      new FilesystemPermission(FilesystemPermission.NONE);

  /** Permission to write. */
  public final static FilesystemPermission Write =
      new FilesystemPermission(FilesystemPermission.WRITE_DATA);

  /**
   * Permission to create file
   */
  public final static FilesystemPermission Create =
      new FilesystemPermission(FilesystemPermission.CREATE_FILE);

  /**
   * Permission to create subdirectory
   */
  public final static FilesystemPermission CreateSubdirectory =
      new FilesystemPermission(FilesystemPermission.CREATE_SUBDIRECTORY);

  /**
   * Permission to delete file or directory
   */
  public final static FilesystemPermission Delete =
      new FilesystemPermission(FilesystemPermission.DELETE);

  /**
   *
   */
  public final static FilesystemPermission ListDirectory =
      new FilesystemPermission(FilesystemPermission.LIST_DIRECTORY);

  // --- constructors --- //

  /**
   * Copy constructor. Takes another instance of the
   * {@link it.grid.storm.filesyste.FilesystemPermission} interface and creates an instance of this
   * class granting <em>exactly</em> the same permissions.
   */
  public FilesystemPermission(final FilesystemPermission p) {

    this.permission = (p.permission & ALL);
  };

  /**
   * Constructor that takes a bitfield of permissions and creates an instance of this class granting
   * exactly <em>those</em> permissions. The bitfield argument has the same format of the
   * fs_acl::permission_t type, or could be constructed by bitwise-OR'ing the
   * <code>READ_DATA</code>, <code>WRITE_DATA</code>, ... constants defined elsewhere in this class.
   * For any bit that is set in the <code>bitfield</code> argument, the corresponding permission
   * will be granted from this object.
   * 
   * <p>
   * Example usage:
   * 
   * <pre>
   * p = new FilesystemPermission(READ_DATA | WRITE_DATA);
   * // p.canReadFile() == true
   * // p.canWriteFile() == true
   * // p.canCreateNewFile() == false
   * </pre>
   * 
   * @see fs_acl::permission_t
   */
  public FilesystemPermission(final int bitfield) {

    this.permission = (bitfield & ALL);
  };

  /**
   * Default constructor: creates an instance that denies permission on each and every operation.
   * 
   * <p>
   * This constructor's intended usage is in conjunction with the permission manipulation functions
   * (in derived classes):
   * 
   * <pre>
   * p = new FilesystemPermission().permitReadFile().permitWriteFile();
   * // p.canReadFile() == true
   * // p.canWriteFile() == true
   * // p.canCreateNewFile() == false
   * </pre>
   */
  protected FilesystemPermission() {

    denyAll();
  }

  // --- permission conversion functions --- //

  /**
   * Return an fs_acl::permission_t bitfield representing the same permissions that this object
   * encodes.
   * 
   * @see fs_acl::permission_t
   */
  public int toFsAclPermission() {

    return permission;
  }

  // --- permission test methods --- //

  /**
   * Return <code>true</code> if permission is granted to read file contents.
   */
  public boolean canReadFile() {

    return 0 != (permission & READ_DATA);
  }

  /**
   * Return <code>true</code> if permission is granted to write file contents. No distinction can be
   * enforced between overwriting contents and appending to the file, so no distinction is made
   * here.
   */
  public boolean canWriteFile() {

    return 0 != (permission & WRITE_DATA);
  }

  /**
   * Return <code>true</code> if permission is granted to list directory contents.
   */
  public boolean canListDirectory() {

    return 0 != (permission & LIST_DIRECTORY);
  }

  /**
   * Return <code>true</code> if permission is granted to descend to a subdirectory.
   */
  public boolean canTraverseDirectory() {

    return 0 != (permission & TRAVERSE_DIRECTORY);
  }

  /**
   * Return <code>true</code> if permission is granted to create a new subdirectory.
   */
  public boolean canMakeDirectory() {

    return 0 != (permission & CREATE_SUBDIRECTORY);
  }

  /**
   * Return <code>true</code> if permission is granted to create a new file.
   */
  public boolean canCreateNewFile() {

    return 0 != (permission & CREATE_FILE);
  }

  /**
   * Return <code>true</code> if permission is granted to change filesystem entry (file or
   * directory) ACL.
   */
  public boolean canChangeAcl() {

    return 0 != (permission & WRITE_ACL);
  }

  /**
   * Return <code>true</code> if permission is granted to delete entry (file or directory).
   */
  public boolean canDelete() {

    return 0 != (permission & DELETE);
  }

  /**
   * Return <code>true</code> if all permissions that are granted by <i>other</i>
   * FilesystemPermission instance are also granted by this instance. That is, test if <i>other</i>
   * is more restrictive than the this instance.
   */
  public boolean allows(final FilesystemPermission other) {

    return (other.permission == (this.permission & other.permission));
  }

  /**
   * Return <code>true</code> if all permission bits that are set in <i>bitfield</i> are also set in
   * this instance. That is, test if <i>bitfield</i> represents a more restrictive than the this
   * instance.
   */
  public boolean allows(final int bitfield) {

    return (bitfield == (this.permission & bitfield));
  }

  // --- permission manipulation methods --- //

  /**
   * Change instance status so that all subsequent <code>can...</code> calls will return
   * <code>false</code>.
   * 
   * <p>
   * Returns the instance itself, so that calls to the permission manipulation functions can be
   * chained:
   * 
   * <pre>
   * p = new FilesystemPermission();
   * p.denyAll().permitReadFile().permitWriteFile();
   * // p.canReadFile() == true
   * // p.canWriteFile() == true
   * // p.canCreateNewFile() == false
   * </pre>
   */
  protected FilesystemPermission denyAll() {

    this.permission = NONE;
    return this;
  }

  protected FilesystemPermission denyReadFile() {

    permission &= ~READ_DATA;
    return this;
  }

  protected FilesystemPermission denyWriteFile() {

    permission &= ~WRITE_DATA;
    return this;
  }

  protected FilesystemPermission denyChangeAcl() {

    permission &= ~WRITE_ACL;
    return this;
  }

  protected FilesystemPermission denyCreateNewFile() {

    permission &= ~CREATE_FILE;
    return this;
  }

  protected FilesystemPermission denyListDirectory() {

    permission &= ~LIST_DIRECTORY;
    return this;
  }

  protected FilesystemPermission denyTraverseDirectory() {

    permission &= ~TRAVERSE_DIRECTORY;
    return this;
  }

  protected FilesystemPermission denyMakeDirectory() {

    permission &= ~CREATE_SUBDIRECTORY;
    return this;
  }

  protected FilesystemPermission denyDelete() {

    permission &= ~DELETE;
    return this;
  }

  public FilesystemPermission deny(FilesystemPermission other) {

    return new FilesystemPermission(this.permission & ~other.permission);
  }

  /**
   * Change instance status so that all subsequent <code>can...</code> calls will return
   * <code>true</code>. <em>Dangerous</em>, use with caution.
   * 
   * <p>
   * Returns the instance itself, so that calls to the permission manipulation functions can be
   * chained:
   * 
   * <pre>
   * p = new FilesystemPermission();
   * p.permitAll().denyDelete().denyRename();
   * // p.canReadFile() == true
   * // p.canWriteFile() == true
   * // p.canDelete() == false
   * </pre>
   */
  protected FilesystemPermission permitAll() {

    permission = ALL;
    return this;
  }

  protected FilesystemPermission permitReadFile() {

    permission |= READ_DATA;
    return this;
  }

  protected FilesystemPermission permitWriteFile() {

    permission |= WRITE_DATA;
    return this;
  }

  protected FilesystemPermission permitChangeAcl() {

    permission |= WRITE_ACL;
    return this;
  }

  protected FilesystemPermission permitCreateNewFile() {

    permission |= CREATE_FILE;
    return this;
  }

  protected FilesystemPermission permitListDirectory() {

    permission |= LIST_DIRECTORY;
    return this;
  }

  protected FilesystemPermission permitTraverseDirectory() {

    permission |= TRAVERSE_DIRECTORY;
    return this;
  }

  protected FilesystemPermission permitMakeDirectory() {

    permission |= CREATE_SUBDIRECTORY;
    return this;
  }

  protected FilesystemPermission permitDelete() {

    permission |= DELETE;
    return this;
  }

  // --- internal status flags --- //

  /**
   * Method that returns an int representing This FilesystemPermission. It can be used as argument
   * to FilesystemPermission constructor to get back an equivalent filesystemPermission Object.
   */
  public int getInt() {

    return permission;
  }

  /**
   * The permission set bitfield. Must match the type and representation used in
   * fs_acl::permission_t: no conversion is done, the code just assumes that it can pass the value
   * back and forth from Java to C++.
   */
  protected int permission;

  public String toString() {

    return Integer.valueOf(permission).toString();
  }
}
