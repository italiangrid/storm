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
 * @file   helper_command.cpp
 *
 * Implementation of the helper_command class.
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.2 $
 */



#include "fs_errors.hpp"
#include "helper_command.hpp"

#include <iostream>
#include <sstream>
#include <string>
#include <stdexcept>
#include <string.h>

#ifdef HAVE_STDIO_FILEBUF
#  include <ext/stdio_filebuf.h>
#else
#  error FIXME: need a functional equivalent of __gnu_cxx::stdio_filebuf
#endif // HAVE_STDIO_FILEBUF



// --- helper_command class implementation --- //

/** Constructor, taking pathname of @c mmgetacl / @c mmputacl command
 * and pathname of file to invoke it on.  Escapes any shell
 * metacharacters that are present in @a path.
 *
 * @exception std::length_error if the combined length of @a cmd and
 * the sanitized @a path exceeds std::string maximum size.
 */
helper_command::helper_command(const std::string& cmd, 
                               const std::string& path) 
  throw (std::length_error) 
  : 
      terminated(false),
      cmd_stdin(NULL), 
      cmd_stdout(NULL),
      termination_status(0),
      pipe_fp(NULL), 
      pipe_sb_ptr(NULL)
{ 
  // attempt to minimize string (re)allocation
  cmdline.reserve(cmd.length() + path.length() + 16);

  cmdline += cmd;
  cmdline += " ";

  // prevent shell metacharacters exploitation in the popen() call in run()
  escape_shell_metacharacters(path, cmdline);
}


/** Destructor.  Closes any input/output stream open by the @c
    run_*() methods. */
helper_command::~helper_command()
{
  if (NULL != pipe_sb_ptr)
    delete pipe_sb_ptr;
  if (NULL != pipe_fp)
    pclose(pipe_fp);
  if (NULL != cmd_stdin)
    delete cmd_stdin;
  if (NULL != cmd_stdout)
    delete cmd_stdout;
}


/** Run the helper command and return a std::istream for reading the command STDOUT. */
std::istream&
helper_command::run_and_return_stdout()
  throw(helper_command::cannot_run)
{
#ifdef HAVE_STDIO_FILEBUF
  // see http://www.codecomments.com/archive272-2005-5-508094.html
  pipe_fp = popen(cmdline.c_str(), "r");
  if (NULL == pipe_fp)
    throw helper_command::cannot_run(cmdline, errno);
  pipe_sb_ptr = new __gnu_cxx::stdio_filebuf<char>(pipe_fp, std::ios::in);
  cmd_stdout = new std::istream(pipe_sb_ptr);
#else
#error FIXME: need a functional equivalent of __gnu_cxx::stdio_filebuf
#endif // HAVE_STDIO_FILEBUF
  return *cmd_stdout;
}


/** Run the helper command and return a std::ostream for writing to the command STDIN. */
std::ostream&
helper_command::run_and_return_stdin()
  throw(helper_command::cannot_run)
{
#ifdef HAVE_STDIO_FILEBUF
  // see http://www.codecomments.com/archive272-2005-5-508094.html
  pipe_fp = popen(cmdline.c_str(), "w");
  if (NULL == pipe_fp)
    throw helper_command::cannot_run(cmdline, errno);
  pipe_sb_ptr = new __gnu_cxx::stdio_filebuf<char>(pipe_fp, std::ios::out);
  cmd_stdin = new std::ostream(pipe_sb_ptr);
#else
#error FIXME: need a functional equivalent of __gnu_cxx::stdio_filebuf
#endif // HAVE_STDIO_FILEBUF
  return *cmd_stdin;
}


/** Wait for command termination and collect termination status. */
int
helper_command::done()
{
  // flush output, if any
  if (NULL != cmd_stdin)
    cmd_stdin->flush();

  // close STDIN/STDOUT streambuf -- need to do this before the
  // pclose()
  if (NULL != pipe_sb_ptr)
    delete pipe_sb_ptr;
  pipe_sb_ptr = NULL;

  // wait for termination and get command exit status
  if (NULL != pipe_fp)
    termination_status = pclose(pipe_fp);
  pipe_fp = NULL;

  terminated=true;

  return termination_status;
}


/** Append @a input string to @a output string, prepending @c '\' to
 * all shell metacharacters.
 * 
 * @return The number of escaped characters.
 */
std::string::size_type
helper_command::escape_shell_metacharacters (const std::string& input, 
                                             std::string& output)
{
  std::string::size_type count;

  for(std::string::const_iterator i = input.begin();
      i < input.end();
      ++i)
    {
      switch(*i) 
        {
        case ' ':
        case '!':
        case '"':
        case '#':
        case '$':
        case '%':
        case '&':
        case '(':
        case ')':
        case '*':
        case ';':
        case '<':
        case '>':
        case '?':
        case '\'':
        case '\\':
        case '\n':
        case '`':
        case '|':
        case '~':
          ++count;
          output += '\\';
        default:
          output += *i;
        }
    }
  return count;
}



// --- helper_command::cannot_run --- //

/** Thrown when some error with the external helper commands (notably,
 *  @c mmgetacl and @c mmputacl in gpfs) happens.
 *
 * By default, prints the string @a prefix, followed by the command
 * name @a cmd, then the error message from @c strerror()
 * corresponding to @a err.
 *
 * @param cmdline  the name of the command that failed
 * @param err      system error code (@c errno)
 * @param prefix   text of the message; default:"Cannot run command"
 */
helper_command::cannot_run::cannot_run(const std::string& cmdline,
                                       const int err,
                                       const char* const prefix)
  : 
  error(),
  errnum(err)
{
  if (0 == err)
    errnum = errno;
  std::ostringstream msg;
  msg << prefix << " '" << cmdline << "': " << strerror(errnum);
  set_message(msg.str());
}



// --- helper_command::bad_termination --- //

/** Constructor, taking failed command line and termination status. */
helper_command::bad_termination::bad_termination(const std::string& _cmdline, int _status)
  : 
  error(), 
  termination_status(_status),
  cmdline(_cmdline)
{
  set_message_from_termination_status();
}


/** Constructor, taking failed helper_command instance. */
helper_command::bad_termination::bad_termination(const helper_command& cmd)
  : 
  error(),
  termination_status(cmd.get_termination_status()),
  cmdline(cmd.get_command_line())
{
  set_message_from_termination_status();
}


/** Set the error message by describing the termination status. */
void
helper_command::bad_termination::set_message_from_termination_status()
{
  std::ostringstream msg;

  msg << "Command '" << cmdline << "' ";
  if (WIFEXITED(termination_status)) {
    // normal exit()
    if (0 == WEXITSTATUS(termination_status))
      msg << "terminated successfully.";
    else
      msg << "exited with code " << WEXITSTATUS(termination_status);
  }
  else if (WIFSIGNALED(termination_status))
    // killed by a signal
    msg << "killed by signal " << WTERMSIG(termination_status);
  else if (WIFSTOPPED(termination_status))
    // child stopped
    msg << "stopped by signal " << WSTOPSIG(termination_status);
  else 
    // should not happen
    msg << "BUG: unhandled termination status in helper_command::bad_termination::set_message_from_termination_status()";

  set_message(msg.str());
}
