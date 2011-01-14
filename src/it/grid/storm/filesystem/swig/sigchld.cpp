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

#include <signal.h>

int __attribute__((constructor)) sigchld_handle(void)
{
  // Prevent the JVM to intercept the SIGCHLD signal.
  // Otherwise we are unable to know the exit status of the child
  // process implicity in the popen() call.
  // See http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4945203

  signal(SIGCHLD,SIG_DFL);
}
