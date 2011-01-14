/**
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
 * @file    util.cpp
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 *
 * C++ wrappers around common POSIX functions.
 */



/** CVS Id. */
static const char* const
RCSID = "$Id: util.cpp,v 1.4 2006/03/28 08:24:52 rmurri Exp $";


// --- include files and defines --- //

#include "fs_errors.hpp"
#include "util.hpp"

#include <errno.h>
#include <stdlib.h>
#include <sstream>
#include <string>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>



// --- xstat --- //

/** Wrapper around POSIX stat(); if stat() fails, throws a
 * %system_error.
 *
 * @param filename pathname of the file to be stat()'ed @param output
 * a <code>struct stat</code> where to record file informations.
 *
 * @throw fs::system_error if the system call failed.
 */
void
fs::xstat(const std::string& filename, struct stat64& output)
  throw(fs::system_error)
{
  int rc = stat64(filename.c_str(), &output);
  if (-1 == rc)
    {
      int err = errno;
      std::ostringstream msg;
      msg <<"Errno:"<<errno<< "failed stat(" << filename << ")";

      throw fs::system_error(msg.str(), err);
    }
}


// --- xstatvfs --- //

/** Wrapper around POSIX @c statvfs; if @c statvfs fails, throws a
 * %system_error.
 *
 * @param filename pathname of the file to be @c stat'ed
 * @param output a @c{struct stat} where to record file informations.
 *
 * @throw fs::system_error if the system call failed.
 */
void
fs::xstatvfs(const std::string& filename, struct statvfs& output)
  throw(fs::system_error)

{
  int rc = statvfs(filename.c_str(), &output);
  if (-1 == rc)
    {
      int err = errno;
      std::ostringstream msg;
      msg << "failed stat(" << filename << ")";
      throw fs::system_error(msg.str(), err);
    }
}
