/**
 * @file    posixfs_acl.hpp
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Interface of the @c posixfs_acl class.
 */



#ifndef __POSIXFS_ACL_H
#define __POSIXFS_ACL_H


#include "fs_acl.hpp"
#include "fs_errors.hpp"

#include <string>
#include <sys/acl.h>


/** Get and set ACLs on a generic POSIX filesystem entries.  Uses the
 * POSIX.1e @c libacl functions to do the actual getting and setting;
 * this is implemented under Linux using the @c setxattr()/getxattr()
 * kernel interface.
 * 
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.4 $
 */
class posixfs_acl : public fs_acl {
public:
  /** Default constructor. */
  posixfs_acl() : fs_acl() { }

  /** Load ACL from the specified filesystem entry (file or directory). */
  virtual void load(const std::string& path, 
                    const bool delete_also = false)
    throw(fs::error, fs::invalid_path, 
          fs::permission_denied, fs::acl_not_supported, 
          std::exception);

  /** Set ACL on the specified filesystem entry (file or directory). */
  virtual void enforce(const std::string& path) 
    const
    throw(fs::error, fs::invalid_path, 
          fs::permission_denied, fs::acl_not_supported, 
          std::exception);

  /** Convert a permission bitfield from POSIX
      (S_IROTH|S_IWOTH|S_IXOTH) to fs_acl::permission_t format. */
  static fs_acl::permission_t to_permission_t(const unsigned short perm);
  
  /** Convert a permission bitfield from fs_acl::permission_t to POSIX
      libacl format. */
  static acl_perm_t from_permission_t(const fs_acl::permission_t perm);


#ifndef SWIG // SWIG interface generator need not see this part
  
protected:
  /** Load the ACL resulting from the UNIX permission bits. */
  void load_permission_bits(const std::string& pathname);

  /** Load the ACL resulting from the UNIX permission bits on GPFS. */
  void load_permission_bits_on_gpfs(const std::string& pathname);

  /** Load the @c DELETE permissions, inspecting the parent directory
      ACL. */
  virtual void load_delete_permission(const std::string& pathname);

  /** Make a new instance of the same class of this object. */
  virtual fs_acl *new_same_class() const;

  /** Throw an exception based on the @c errno system error code. */
  virtual void  throw_error(const int err, 
                            const std::string& pathname, 
                            const char* const funcname = "") const;
#endif // #ifndef SWIG
};


#endif // #ifndef __POSIXFS_ACL_H
