/**
 * @file   fs_acl.hpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Interface of the fs_acl class.
 *
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * in the accompanying file LICENCE.txt
 *
 * Documentation for this class and its methods is in the fs_acl.cpp
 * file.
 */

#ifndef __FS_ACL_H
#define __FS_ACL_H


#include "fs_errors.hpp"

#include <string>
#include <sys/types.h>
#include <utility>
#include <vector>

#ifdef HAVE_HASH_MAP
# include <ext/hash_map>
#else
# include <map>
#endif // HAVE_HASH_MAP


// set member m_old to new value p_new and return old value of m_old
#define SET_AND_RETURN_OLD(m_old,p_new)  {permission_t old=m_old; m_old=p_new; return old;}
#define GRANT_AND_RETURN_OLD(m_old,p_new)  {permission_t old=m_old; m_old|=p_new; return old;}
#define REVOKE_AND_RETURN_OLD(m_old,p_new)  {permission_t old=m_old; m_old&=~p_new; return old;}


/** A class for storing and operating on a filesyetem-level ACL. */
class fs_acl {
 public:
  /** The bitfield type used to encode permissions on an
   *  object. Choice falls on @c int (instead of a class of its own)
   *  because we have to pass a lot of these objects back and forth
   *  from Java.  The same integer value may be passed as an
   *  argument to the
   *  it.grid.storm.filesystem.FilesystemPermission#FilesystemPermission(int)
   *  constructor.
   *
   * @see fs_acl::permission_flags
   * @see it.grid.storm.filesystem.FilesystemPermission
   */
  typedef int permission_t;
  
  /** Constants to be used in dealing with permission_t bitfields.
   *
   * <p> The meaning of the individual permission bits (but not the
   * numeric value!) is loosely patterned on the NFSv4 one (RFC
   * 3530, section 5.1); still, only those bits that can influence
   * an SRM operation have been retained (for instance, there is no
   * @c APPEND_DATA right distinct from the @c WRITE_DATA one, as we
   * cannot distinguish the two cases within the current SRM
   * interface).
   *
   * <p> Numeric compatibility with the POSIX ACL permission bits
   * has been retained where possible.
   *
   * @see fs_acl::permission_t
   */
  enum permission_flags {
    /** No permission at all. */
    PERM_NONE = 0,

    /** Permission to execute the file. */
    PERM_EXECUTE = (1<<0),

    /** Permission to write file contents. */
    PERM_WRITE_DATA = (1<<1),

    /** Permission to read file contents. */
    PERM_READ_DATA = (1<<2),

    /** Permission to change file extended ACL (that is, beyond normal
        UNIX permission bits). */
    PERM_WRITE_ACL = (1<<3),

    /** Permission to read file extended ACL (that is, beyond normal
        UNIX permission bits). */
    PERM_READ_ACL = (1<<4),

    /** Permission to delete a filesystem entry (file or directory). */
    PERM_DELETE = (1<<5),

    /** Permission to descend to children directories of a directory. */
    PERM_TRAVERSE_DIRECTORY = (1<<0),

    /** Permission to list directory contents. */
    PERM_LIST_DIRECTORY = (1<<2),

    /** Permission to create a child subdirectory. */
    PERM_CREATE_SUBDIRECTORY = (1<<6),

    /** Permission to create a new file. */
    PERM_CREATE_FILE = (1<<7),

    /** Permission to delete a file or directory within a directory. */
    PERM_DELETE_CHILD = (1<<8),

    /** All permission bits set. */
    PERM_ALL =
        PERM_EXECUTE
        |PERM_WRITE_DATA
        |PERM_READ_DATA
        |PERM_WRITE_ACL
        |PERM_READ_ACL
        |PERM_DELETE
        |PERM_TRAVERSE_DIRECTORY
        |PERM_LIST_DIRECTORY
        |PERM_CREATE_SUBDIRECTORY
        |PERM_CREATE_FILE
        |PERM_DELETE_CHILD,
  };

  /** Return <code>true</code> if the specified user would be granted the
      specified access, based on the ACL stored in this object. */
  virtual bool access(const permission_t mode, 
                      const uid_t uid, 
                      const std::vector<gid_t> gid) const;
  
  /** Reset object to newly-constructed state, clearing the stored
      ACL.*/
  virtual void clear();

  /** Load ACL from a given file or directory, and store it in current object. */
  virtual void load(const std::string& pathname, 
                    const bool delete_also = false) 
    throw(fs::error, std::exception) = 0;

  /** Store current ACL into the given file or directory. */
  virtual void enforce(const std::string& pathname) const
    throw(fs::error, std::exception) = 0;

  /** Default constructor. All fields are initialized to null values,
     which is a fairly restrictive default, resulting in denying any
     access, should any getter method be called when no sensible value
     has been stored in this object.
   */
  fs_acl() :
    loaded_from_directory(false),
    owner_perm(0,PERM_NONE),
    group_owner_perm(0,PERM_NONE),
    other_perm(PERM_NONE),
    mask_is_set(false),
    mask(PERM_ALL)
  { }
  
  /** Destructor.  */
  virtual ~fs_acl() { }
  
  /** Return the GID of the file group owner */
  gid_t get_group_owner_gid() const { return group_owner_perm.get_id(); }
  
  /** Return the permission bits valid for the file group owner */
  permission_t get_group_owner_perm() const { return group_owner_perm.get_perm(); }
  
  /** Return the mask that is applied to all permissions for
      non-owner users/groups in this ACL. */
  permission_t get_mask() const { return (mask_is_set)? mask : fs_acl::PERM_ALL; }
  
  /** Return the permission bits valid for users/groups not listed in this ACL */
  permission_t get_other_perm() const { return other_perm; }
  
  /** Return the permission bits valid for the file owner */
  permission_t get_owner_perm() const { return owner_perm.get_perm(); }
  
  /** Return the UID of the file owner */
  uid_t get_owner_uid() const { return owner_perm.get_id(); }
  
  /** Return true if there's a permission for the group (identified by
      GID) stored in this ACL.   */
  bool has_group_perm (const gid_t gid) const 
    { return (gid == group_owner_perm.get_id()) || (1 == group_acl.count(gid)); }
  
  /** Return true if there's a permission for the user (identified by
      UID) stored in this ACL. */
  bool has_user_perm (const uid_t uid) const 
    { return (uid == owner_perm.get_id()) || (1 == user_acl.count(uid)); }
  
  /** Return true if a mask entry has been stored in this ACL.  If no
   * mask entry has been explicitly set, then the group owner permission
   * bits act as the (implicit) mask.
   */
  bool has_explicit_mask() const { return mask_is_set; }
  
  /** Return true if the <em>effective</em> permissions is different
      from the stored one. */
  bool is_group_perm_masked(const gid_t gid) const
    { return is_perm_masked_template(gid, group_owner_perm, group_acl); }

  /** Return @c true if given @a gid is the group owner. */
  bool is_group_owner(const gid_t gid) const
    { return (group_owner_perm.get_id() == gid); }

  /** Return @c true if given @a uid is the owner. */
  bool is_owner(const uid_t uid) const 
    { return (owner_perm.get_id() == uid); }

  /** Return @c true if bitfield @a subset is a subset of @a superset; that is, all bits
      that are set in @a subset are also set in @a superset. */
  static bool is_permission_subset(const permission_t subset, const permission_t superset)
    { return ((subset & superset) == subset); }

  /** Return true if the <em>effective</em> permissions is different
      from the stored one. */
  bool is_user_perm_masked(const uid_t uid) const
    { return is_perm_masked_template(uid, owner_perm, user_acl); }

  /** Return the <em>effective</em> permission bits that apply to the
   * specified group (by GID).  If no permission is found for the
   * given GID, then return PERM_NONE.
   *
   * The permission bitfield returned by this method is the @em
   * effective one, that is, the result of bitwise-AND of the raw
   * permission and the mask.  The effective permission is used in
   * actual access checks.
   */
  permission_t get_group_effective_perm(const gid_t gid) const
    { return get_effective_perm_template(gid, group_owner_perm, group_acl); }

  /** Return the (raw unmasked) permission bits that apply to the
   * specified group (by GID).  If no permission is found for the
   * given GID, then return PERM_NONE.
   *
   * The permission bitfield returned by this method is the raw, @em
   * non-effective one; in actual access checks, the @em effective
   * permission is used.
   */
  permission_t get_group_perm(const gid_t gid) const
    { return get_perm_template(gid, group_owner_perm, group_acl); }

  /** Return the <em>effective</em> permission bits that apply to the
   * specified group (by UID).  If no permission is found for the
   * given UID, then return PERM_NONE.
   *
   * The permission bitfield returned by this method is the @em
   * effective one, that is, the result of bitwise-AND of the raw
   * permission and the mask.  The effective permission is used in
   * actual access checks.
   */
  permission_t get_user_effective_perm(const uid_t uid) const
    { return get_effective_perm_template(uid, owner_perm, user_acl); }

  /** Return the (raw unmasked) permission bits that apply to the
   * specified group (by UID).  If no permission is found for the
   * given UID, then return PERM_NONE.
   *
   * The permission bitfield returned by this method is the raw, @em
   * non-effective one; in actual access checks, the @em effective
   * permission is used.
   */
  permission_t get_user_perm(const uid_t uid) const
    { return get_perm_template(uid, owner_perm, user_acl); }

  /** Set all bits set in the @a perm parameter in the group owner ACL entry. */
  permission_t grant_group_owner_perm (const permission_t perm)
    { permission_t old = group_owner_perm.get_perm(); 
        group_owner_perm.set_perm(old|perm); return old;}

  /** Set all bits set in the @a perm parameter in the group ACL entry
   * associated to the given @a gid.  If the supplied GID is the owner
   * group GID, then update owner group permissions, otherwise update
   * the entry in the per-group list.
   * 
   * <p> If no match for @a gid is found in the group ACL, then add an
   * entry containing (@a gid, @a perm).
   */
  permission_t grant_group_perm (const gid_t gid, const permission_t perm)
    { return (gid == group_owner_perm.get_id())?
        grant_group_owner_perm(perm) : grant_group_perm_not_owner(gid,perm); }

  permission_t grant_group_perm_not_owner (const gid_t gid, const permission_t perm)
    { GRANT_AND_RETURN_OLD(group_acl[gid], perm); }

  /** Set all bits set in the @a perm parameter in the "other" ACL entry. */
  permission_t grant_other_perm (const permission_t perm)
  { permission_t old = other_perm; other_perm |= perm; return old; }

  /** Set all bits set in the @a perm parameter in the owner ACL entry. */
  permission_t grant_owner_perm (const permission_t perm)
    { permission_t old = owner_perm.get_perm(); 
        owner_perm.set_perm(old|perm); return old;}

  /** Set all bits set in the @c perm parameter in the user ACL entry
   * associated to the given <code>uid</code>.  If the supplied UID is
   * the owner's UID, then update owner's permissions, otherwise
   * update the entry in the per-user list.
   * 
   * <p> If no match for @c uid is found in the ACL, then add an entry
   * containing (@c uid, @c perm).
   */
  permission_t grant_user_perm (const uid_t uid, const permission_t perm)
    { return (uid == owner_perm.get_id())?
        grant_owner_perm(perm) : grant_user_perm_not_owner(uid,perm); }

  permission_t grant_user_perm_not_owner (const uid_t uid, const permission_t perm)
    { GRANT_AND_RETURN_OLD(user_acl[uid], perm); }

  /** Return @c true if there are ACEs for specific (non-owner) users or groups. */
  bool has_extended_acl() const
    { return (user_acl.size()>0 || group_acl.size()>0); }

  /** Remove a GID and its associated permission from the the per-group ACL. */
  permission_t remove_group_perm_not_owner (const gid_t gid) 
    { permission_t old = group_acl[gid]; group_acl.erase(gid); return old; }

  /** Remove a UID and its associated permission from the the per-user ACL. */
  permission_t remove_user_perm_not_owner (const uid_t uid)
    { permission_t old = user_acl[uid]; user_acl.erase(uid); return old; }

  /** Clear in the group owner ACL entry all bits that are cleared in
      the @a perm parameter. */
  permission_t revoke_group_owner_perm (const permission_t perm)
    { permission_t old = group_owner_perm.get_perm(); 
        group_owner_perm.set_perm(old & ~perm); return old;}

  /** Clear all bits that are cleared in the @c mask parameter in the
   * group ACL entry associated to the given <code>gid</code>.  If the
   * supplied GID is the owner group GID, then update owner group
   * permissions, otherwise update the entry in the per-group list.
   * 
   * <p> In other words, @c mask is used to mask out permission bits
   * from an ACL entry.
   * 
   * <p> If no match for @c gid is found in the ACL, then add an entry
   * for group @c gid, with the "other" permissions, masked with @c mask.
   */
  permission_t revoke_group_perm (const gid_t gid, const permission_t perm)
    { return (gid == group_owner_perm.get_id())?
        revoke_group_owner_perm(perm) : revoke_group_perm_not_owner(gid,perm); }

  permission_t revoke_group_perm_not_owner (const gid_t gid, const permission_t perm)
    { REVOKE_AND_RETURN_OLD(group_acl[gid], perm); }

  /** Clear in the "other" ACL entry all bits that are cleared in the @a
   *  perm parameter.
   *
   * <p> In other words, @c mask is used to mask out permission bits
   * from the "other" ACL entry.
   */
  permission_t revoke_other_perm (const permission_t mask)
    { permission_t old = other_perm; other_perm &= ~mask; return old; }

  /** Clear in the owner ACL entry all bits that are cleared in the @a
      perm parameter. */
  permission_t revoke_owner_perm (const permission_t perm)
    { permission_t old = owner_perm.get_perm(); 
        owner_perm.set_perm(old & ~perm); return old;}

  /** Clear all bits that are cleared in the @c mask parameter in the
   * user ACL entry associated to the given <code>uid</code>.  If the
   * supplied UID is the owner's UID, then update owner's permissions,
   * otherwise update the entry in the per-user list.
   * 
   * <p> In other words, @c mask is used to mask out permission bits
   * from an ACL entry.
   * 
   * <p> If no match for @c uid is found in the ACL, then add an entry
   * for user @c uid, with the "other" permissions, masked with @c mask.
   */
  permission_t revoke_user_perm (const uid_t uid, const permission_t perm)
    { return (uid == owner_perm.get_id())?
        revoke_owner_perm(perm) : revoke_user_perm_not_owner(uid,perm); }

  permission_t revoke_user_perm_not_owner (const uid_t uid, const permission_t perm)
    { REVOKE_AND_RETURN_OLD(user_acl[uid], perm); }

  /** Set the file group owner's permission bits */
  permission_t set_group_owner_perm (const permission_t perm)
    { permission_t old = group_owner_perm.get_perm(); 
        group_owner_perm.set_perm(perm ); return old; }

  /** Add a (GID, permission bits) pair to the ACL. If the supplied GID
   * is the owner group GID, then update owner group permissions, otherwise
   * update/add a specific entry in the per-group list.
   */
  permission_t set_group_perm (const gid_t gid, const permission_t perm)
    { return (get_group_owner_gid() == gid)?
        set_group_owner_perm(perm) : set_group_perm_not_owner(gid, perm); }
  
  /** Add a (GID, permission bits) pair to the per-group list. */
  permission_t set_group_perm_not_owner (const gid_t gid, const permission_t perm)
    { SET_AND_RETURN_OLD(group_acl[gid], perm); }

  /** Set the mask that applies to all non-owner users and groups.
   *
   * @return the previous value of the mask.
   */
  permission_t set_mask (const permission_t new_mask) 
    { mask_is_set = true; SET_AND_RETURN_OLD(mask, new_mask); }

  /** Set the permission bits that applies to any user not explicitly
      mentioned in this ACL. */
  permission_t set_other_perm (const permission_t new_other_perm) 
    { SET_AND_RETURN_OLD(other_perm, new_other_perm); }

  /** Set the owner user permission. */
  permission_t set_owner_perm (const permission_t perm)
    { permission_t old = owner_perm.get_perm(); owner_perm.set_perm(perm); return old; }

  /** Add a (UID, permission bits) pair to the ACL. If the supplied UID
   * is the owner's UID, then update owner's permissions, otherwise
   * update/add a specific entry in the per-user list. 
   */
  permission_t set_user_perm (const uid_t uid, const permission_t perm)
    { return (get_owner_uid() == uid)?
        set_owner_perm(perm) : set_user_perm_not_owner(uid, perm); }

  /** Add a (UID, permission bits) pair to the per-user list. */
  permission_t set_user_perm_not_owner (const uid_t uid, const permission_t perm)
    { SET_AND_RETURN_OLD(user_acl[uid], perm); }

  /** Return total number of user->permission and group->permission mappings stored. */
  size_t size() const { return 2 + user_acl.size() + group_acl.size(); }

  /** Return a vector containing all the UIDs for which there is an
   * ACE in the ACL.
   */
  std::vector<uid_t> get_uid_list()
    {
      std::vector<uid_t> uid_list;
      for(user_acl_t::const_iterator i = user_acl.begin();
        i != user_acl.end();
        ++i)
        uid_list.push_back(i->first);
      return uid_list;
    }

    size_t get_uid_list_size() { return user_acl.size(); }

  /** Return a vector containing all the UIDs for which there is an
   * ACE in the ACL.
   */
  std::vector<gid_t> get_gid_list()
    {
      std::vector<gid_t> gid_list;
      for(user_acl_t::const_iterator i = group_acl.begin();
        i != group_acl.end();
        ++i)
        gid_list.push_back(i->first);
      return gid_list;

    }

// the rest of this file is mostly gory internal details, and
// should not be seen by the SWIG interface generator...
#ifndef SWIG

  /** Base type for both the per-user ACL and the per-group ACL. */
#ifdef HAVE_HASH_MAP
  template<typename uid_or_gid_t>
  class extended_acl_t 
    : public __gnu_cxx::hash_map< const uid_or_gid_t, permission_t, __gnu_cxx::hash<uid_or_gid_t> > 
    { };
#else
  template<typename uid_or_gid_t>
  class extended_acl_t 
    : public std::map< const uid_or_gid_t, permission_t> 
    { };
#endif // HAVE_HASH_MAP
  
  /** Type definition for the per-user ACL. */
  typedef extended_acl_t<uid_t> user_acl_t;

  /** Type definition for the per-group ACL. */
  typedef extended_acl_t<gid_t> group_acl_t;

  // these need to be public for the load_delete() method...
  user_acl_t::const_iterator user_acl_begin() const { return user_acl.begin(); }
  user_acl_t::const_iterator user_acl_end() const { return user_acl.end(); }
  group_acl_t::const_iterator group_acl_begin() const { return group_acl.begin(); }
  group_acl_t::const_iterator group_acl_end() const { return group_acl.end(); }
  

 protected:

  /** Make a new instance of the same class of this object. */
  virtual fs_acl *new_same_class() const {return NULL;};

  /** Set the file owner's UID and permission bits.  If the permission
   *  bits are not specified via the @a perm parameter, then they are
   *  reset to @c PERM_NONE. 
   */
  void set_owner(const uid_t uid, const permission_t perm = PERM_NONE) 
    { owner_perm.set(uid, perm); }

  /** Set the file group owner's GID and permission bits.  If the
   *  permission bits are not specified via the @a perm parameter, then
   *  they are reset to @c PERM_NONE.
   */
  void set_group_owner(const gid_t gid, const permission_t perm = PERM_NONE) 
    { group_owner_perm.set(gid, perm); }

  /** True if @c load() was called with the first parameter pointing to a directory. */
  bool loaded_from_directory;


 private:
  /** Template class for holding a user/group owner permission. */
  template<typename uid_or_gid_t>
  class owner_perm_pair : protected std::pair<uid_or_gid_t, permission_t> {
  public:
    owner_perm_pair(uid_or_gid_t id, permission_t perm=PERM_NONE)
      : std::pair<uid_or_gid_t, permission_t>(id, perm) 
      { }
    permission_t get_perm() const
      { return this->second; }
    permission_t set_perm(permission_t new_perm) 
      { permission_t old = this->second; this->second = new_perm; return old; }
    uid_or_gid_t get_id() const
      { return this->first; }
    uid_or_gid_t set_id(permission_t new_owner) 
      { permission_t old = this->first; this->first = new_owner; return old; }
    void set(uid_or_gid_t id, permission_t perm) 
      { this->first = id; this->second = perm; }
  };

  /** Template method for checking if the <em>effective</em>
   * permission is different from the stored one.
   *
   * Return true if the <em>effective</em> permissions is different
   * from the stored one.  The owning user/group permission is @em
   * never masked.
   */
  template<typename uid_or_gid_t>
    bool
    is_perm_masked_template(const uid_or_gid_t id,
                            const owner_perm_pair<uid_or_gid_t>& owner,
                            extended_acl_t<uid_or_gid_t>& eacl) const
    {
      if (id == owner.get_id())
        return true;
      else {
        const fs_acl::permission_t perm = eacl[id];
        return (perm != (get_mask() & perm));
      }
    }


  /** Return the <em>effective</em> permission bits that apply to the
   * specified user/group. If the specified UID/GID matches the one of
   * the owner user/group, then the owner permissions are returned
   * (unmasked).
   *
   * @return the effective (i.e., masked) permission that applies to
   * the given UID/GID, or PERM_NONE if no permission is present for
   * that user/group.
   */
  template<typename uid_or_gid_t>
    fs_acl::permission_t
    get_effective_perm_template(const uid_or_gid_t id,
                                        const owner_perm_pair<uid_or_gid_t>& owner,
                                        extended_acl_t<uid_or_gid_t>& eacl) const
    {
      if (id == owner.get_id())
        return owner.get_perm();
      else 
        if (1 == eacl.count(id))
          return (get_mask() & eacl[id]);
        else
          return PERM_NONE;
    }


  /** Return the <em>non-effective</em> permission bits that apply to
   * the specified user/group. If the specified UID/GID matches the
   * one of the owner user/group, then the owner permissions are
   * returned.
   *
   * @return the raw (i.e., unmasked) permission that applies to the
   * given UID/GID, or PERM_NONE if no permission is present for that
   * user/group.
   */
  template<typename uid_or_gid_t>
    fs_acl::permission_t
    get_perm_template(const uid_or_gid_t id,
                              const owner_perm_pair<uid_or_gid_t>& owner,
                              extended_acl_t<uid_or_gid_t>& eacl) const
    {
      if (id == owner.get_id())
        return owner.get_perm();
      else 
        if (1 == eacl.count(id))
          return eacl[id];
        else
          return PERM_NONE;
    }


  // --- instance variables ---

  /** ACE applied to file owner */
  owner_perm_pair<uid_t> owner_perm;

  /** ACE applied to file group owner. */
  owner_perm_pair<gid_t> group_owner_perm;

  /** Permission for access to the "other" class. */
  permission_t  other_perm;

  /** Flag, set if an ACE of type "mask" is provided. */
  bool mask_is_set;

  /** ACE of type "mask"; contents are valid iff mask_is_set is flagged @c true. */
  permission_t  mask;


  // XXX: we need to declare these two as 'mutable', otherwise some
  // methods declared "const" below will not compile (" error: passing
  // `const group_acl_t' as `this' argument of `_Tp&
  // std::map<...>::operator[](const _Key&) ... discards qualifiers").
  // Declaring these as mutable looks like it's semantically
  // incorrect, but is the only workaround I could find by now. (RM)
  //
  // [update 2006-03-13] It looks like newer (3.4) versions of GNU libstdc++
  // do have a 'const_reference std::map<...>::operator[]' so that this
  // needs no longer 'mutable'.
  //

  /** List of ACEs applied to specific users. */
  mutable extended_acl_t<uid_t>  user_acl;

  /** List of ACEs applied to specific groups. */
  mutable extended_acl_t<gid_t> group_acl;

#endif // #ifndef SWIG
};


#endif // #ifndef __FS_ACL_H
