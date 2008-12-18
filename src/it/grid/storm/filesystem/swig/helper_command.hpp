/**
 * @file   helper_command.hpp
 *
 * Interface of the helper_command class.
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.4 $
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

#ifndef __HELPER_COMMAND_HPP
#define __HELPER_COMMAND_HPP


#include "fs_errors.hpp"
#include <errno.h>

#include <cstdlib> // wait()'s W*() macros
#include <iostream>
#include <string>
#include <stdexcept>

#ifdef HAVE_STDIO_FILEBUF
#  include <ext/stdio_filebuf.h>
#else
#  error FIXME: need a functional equivalent of __gnu_cxx::stdio_filebuf
#endif // HAVE_STDIO_FILEBUF


/** Invoke an external helper command, providing access to the
    called command's standard input or standard output as C++
    iostreams. */
class helper_command {
public:

  // --- helper_command exceptions --- //

  /** Base class for helper_command error conditions. */
  class error : public fs::error {
  public:
    /** Constructor, taking error message */
    error (const std::string& msg) : fs::error(msg) { }
    /** Destructor.  Does nothing, but declared here because the @c
        std::exception one has a no-throw clause. */
    ~error() throw() { }
  protected:
    /** Default constructor, for subclassing setting the error message
        otherwise. */
    error() : fs::error() { }
  };

  /** Thrown when the external command fails to run. */
  class cannot_run : public error {
  public:
    /** Constructor, taking error message and optional @c errno code
        and strerror() string. */
    cannot_run(const std::string& cmdline, 
               const int err = 0,
               const char* const prefix = "Cannot run command");
    /** Destructor.  Does nothing, but declared here because the @c
        std::exception one has a no-throw clause. */
    ~cannot_run() throw() { }
    /** Return the system error code passed to the constructor. */
    int get_errno() const { return errnum; }
  private:
    /** System error code. */
    int errnum;
  };

  /** Thrown when the external command fails to run. */
  class bad_termination : public error {
  public:
    /** Constructor, taking error message and optional @c errno code
        and strerror() string. */
    bad_termination(const std::string& cmdline, int status);
    bad_termination(const helper_command& cmd);
    /** Destructor.  Does nothing, but declared here because the @c
        std::exception one has a no-throw clause. */
    ~bad_termination() throw() { }
  private:
    /** Command termination status */
    const int termination_status;
    /** Command line. */
    const std::string cmdline;

    /** Set the error message by describing the termination status. */
    void set_message_from_termination_status();
  };


  // --- helper_command class interface --- //

  /** Constructor, taking pathname of @c mmgetacl / @c mmputacl command
      and pathname of file to invoke it on. */
  helper_command(const std::string& cmd, const std::string& path) 
    throw(std::length_error);

  /** Destructor.  Closes any input/output stream open by the @c
      run_*() methods. */
  ~helper_command();

  /** Run the helper command and return a std::istream for reading
      the command STDOUT. Call done() to wait for process termination. */
  std::istream& run_and_return_stdout() throw(helper_command::cannot_run);

  /** Run the helper command and return a std::ostream for writing
      to the command STDIN. Call done() to wait for process termination. */
  std::ostream& run_and_return_stdin() throw(helper_command::cannot_run);

  /** Wait for command termination and collect termination status. */
  int done();

  /** Return the command line actually invoked. */
  const std::string& get_command_line() const 
    { return cmdline; }

  /** Return command exit status.  The exit status is encoded as the
      @a status in the @c wait*() C library calls, and should only be
      inspected with the appropriate W*() macros. */
  int get_termination_status() const 
    { return termination_status; }

  /** Return @c true if command exited normally (i.e., was not
      terminated or stopped by a signal). Will return @c false until
      done() has been called. */
  bool terminated_normally() const 
    { return terminated && WIFEXITED(termination_status); }
    
  /** Return the command exit status (-128 .. 127); this value is
      meaningful only if the command terminated normally (i.e., was
      not terminated or stopped by a signal). */
  int get_exit_code() const 
    { return WEXITSTATUS(termination_status); }

  /** Return @c true if command exited successfully. */
  bool terminated_successfully() const 
    { return terminated_normally() && (0 == get_exit_code()); }


  /** Append @a input string to @a output string, prepending @c '\' to
      all shell metacharacters. */
  static std::string::size_type escape_shell_metacharacters(const std::string& input,
                                                            std::string& output);

  
protected:
  /** Becomes @c true when the command has completed its run. */
  bool terminated; 

  /** The complete command line to run. */
  std::string cmdline;

private:
  std::ostream* cmd_stdin;
  std::istream* cmd_stdout;

  /** Command exit status, as returned by @c pclose(). */
  int termination_status;

#ifdef HAVE_STDIO_FILEBUF
  /** Connected to the command's STDIN or STDOUT by popen(). */
  FILE *pipe_fp; 

  /** Adaptor to read/write to the pipe via standard C++ library streams. */
  __gnu_cxx::stdio_filebuf<char>* pipe_sb_ptr;
#else
#error FIXME: need a functional equivalent of __gnu_cxx::stdio_filebuf
#endif // HAVE_STDIO_FILEBUF
};


#endif // __HELPER_COMMAND_HPP
