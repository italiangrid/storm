/**
 * @file fs_errors.cpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Implementation of namespace fs excpetion classes.
 */
/*
 * Copyright (c) 2005 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms
 * as StoRM itself.
 */

static const char* const 
RCSID = "$Id: fs_errors.cpp,v 1.2 2006/04/04 15:19:22 rmurri Exp $";


// --- #include files --- //

#include "fs_errors.hpp"

#include <errno.h>
#include <sstream>
#include <string>
#include <stdexcept>

/* this is needed for strerror_r to have POSIX semantics */
#ifndef _POSIX_C_SOURCE
#define _POSIX_C_SOURCE=200112L
#endif
#include <string.h>


// --- implementation --- //

/** Returns error description, given the error code.  
 *
 * @param  errnum  The system error code (see <code>errno</code>)
 *
 * @return The error description from <code>strerror_r</code>.
 * 
 * @todo FIXME: should check ERANGE return from @c strerror_r
 */
std::string
fs::system_error::error_message (const int errnum)
{
  // FIXME: should look for ERANGE error code from strerror_r,
  // and realloc() buf and retry...  But this is so quickier ;-)
  char buf[512];
  
  strerror_r (errnum, buf, 512);

  return std::string(buf);
}


/** Constructor, taking error message as @c std::string parameter.  If
 * @a errnum is not zero, the resulting exception will have a @c
 * what() message constructed by appending the system error message
 * (as returned by @c strerror()) to the user-supplied message in @a
 * msg.
 *
 * @param msg descriptive text for the occuring error 
 *
 * @param errnum system error code (@c errno); if not zero, then
 * system error message (as returned by @c strerror()) will be
 * appended to user-supplied @a msg.
 */
fs::system_error::system_error(const std::string& msg, 
                               const int errnum)
  : errcode(errnum)
{
  std::ostringstream msg_sb;
  msg_sb << msg;
  if (0 != errnum)
    msg_sb << ": " << error_message(errnum);
  errmsg.assign(msg_sb.str());
}
