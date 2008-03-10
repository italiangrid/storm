/* Copyright (C) 2006 by Antonio Messina <antonio.messina@ictp.it> for the ICTP project EGRID. */
/*
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation; either version 2 of the License, or (at your
 *  option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

#include <signal.h>

int __attribute__((constructor)) sigchld_handle(void)
{
  // Prevent the JVM to intercept the SIGCHLD signal.
  // Otherwise we are unable to know the exit status of the child
  // process implicity in the popen() call.
  // See http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4945203

  signal(SIGCHLD,SIG_DFL);
}
