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
 * @file   path.cpp
 *
 * Implementation of the path class.
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.2 $
 */


#include "path.hpp"
#include "split2.hpp"

#include <cassert>
#include <string>
#include <sys/types.h> // mode_t
#include <vector>
#include <unistd.h>


/** Constructor, taking PATH string formatted like the environment
    variable "PATH". */
fs::path::path(const char* const PATH)
{
  if (NULL == PATH)
    // default PATH, according to Linux execlp(3) manpage
    split2(*this, ".:/bin:/usr/bin", ":");
  else
    split2(*this, PATH, ":");
}


/** Search for the first occurence of @a x, in the directories listed
    in this path instance; if one is found, it is put in the @a output
    parameter. Optional argument @a mode is passed to the access(2)
    function; defaults to @c R_OK|X_OK which corresponds to searching
    for executable files. 
    
    @return @c true if an occurrence of @a x was found in the search
    path; @c false if none was found.
*/
bool
fs::path::search(const std::string& x, 
             std::string& output,
             const mode_t mode) 
  const
{
  for (std::vector<std::string>::const_iterator i = begin();
       i != end();
       ++i)
    {
      // build complete path 
      std::string candidate(*i);
      candidate += '/';
      candidate += x;

      // check if exists and is accessible with the right mode
      if (0 == access(candidate.c_str(), mode)) {
        output = candidate;
        return true;
      }
    }
  return false;
}
