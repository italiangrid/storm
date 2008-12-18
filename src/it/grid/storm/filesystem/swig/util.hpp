/**
 * @file    util.hpp
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 *
 * C++ wrappers around common POSIX functions.
 */
/*
 * $Id: util.hpp,v 1.2 2006/03/22 10:32:14 rmurri Exp $
 *
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms
 * as StoRM itself.
 */

#ifndef __UTIL_H
#define __UTIL_H


#include "fs_errors.hpp"

#include <string>
#include <sys/stat.h>
#include <sys/statvfs.h>
#include <sys/types.h>
#include <unistd.h>


namespace fs {

  /** Wrapper around POSIX @c stat; if @c statvfs fails, throws a
	  %system_error. */
  void xstat(const std::string& filename, struct stat64& output) throw(fs::system_error);

  /** Wrapper around POSIX @c statvfs; if @c statvfs fails, throws a
	  %system_error. */
  void xstatvfs(const std::string& filename, struct statvfs& output) throw(fs::system_error);

};

#endif // __UTIL_H
