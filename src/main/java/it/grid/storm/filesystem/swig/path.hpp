/**
 * @file   path.hpp
 *
 * Interface of the path class.
 *
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * in the accompanying file LICENCE.txt
 */

#ifndef __PATH_HPP
#define __PATH_HPP

#include <string>
#include <sys/types.h> // mode_t
#include <vector>
#include <unistd.h>
#include <stdlib.h>


namespace fs {


  class path : public std::vector<std::string> {
  public:
    /** Constructor, taking PATH string formatted like the environment
        variable "PATH". */
    path(const char* const PATH = getenv("PATH"));

    /** Search for the first occurence of @a x, in the directories
        listed in this path instance; if one is found, it is put in the @a
        output parameter. */
    bool search(const std::string& x, 
                std::string& output,
                const mode_t mode = R_OK|X_OK) const;
  };


};


#endif // __PATH_HPP
