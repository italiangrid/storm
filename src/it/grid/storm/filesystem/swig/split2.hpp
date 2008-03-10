/**
 * @file   split2.hpp
 *
 * The split2() function for breaking a string into tokens.
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

#ifndef SPLIT2_HPP
#define SPLIT2_HPP


#include <string>


/** Break a string into words at occurences of @a delimiter
 * characters, putting words into @a container; any occurrence of
 * characters in @a ignore at start of field are ignored.
 *
 * By default, both @a delimiters and @a ignore contain the whitespace
 * characters only.  Therefore, when called with default parameters,
 * split() will split a string into words, ignoring all whitespace at
 * the start of a word.
 *
 * <p> Please see
 * http://gcc.gnu.org/onlinedocs/libstdc++/21_strings/howto.html#3 
 * for extensive implementation comments, and
 * http://gcc.gnu.org/onlinedocs/libstdc++/21_strings/stringtok_std_h.txt
 * for the original source.
 *
 *
 * @author Chris King
 * @author Petr Prikryl
 */
template <typename Container>
static void
split2(Container &container, 
       std::string const &in,
       const char * const delimiters = " \t\n",
       const char* const ignore = " \t\n")
{
  const std::string::size_type len = in.length();
  std::string::size_type i = 0;

  while ( i < len )
    {
      // eat leading characters of class "ignore"
      if (NULL != ignore)
        i = in.find_first_not_of (ignore, i);
      if (i == std::string::npos)
        // nothing left but ignored characters
        return;   

      // find the end of the token
      std::string::size_type j = in.find_first_of (delimiters, i);

      // push token
      if (j == std::string::npos) {
        container.push_back (in.substr(i));
        return;
      } else
        container.push_back (in.substr(i, j-i));

      // set up for next loop
      i = j + 1;
    }
}



#endif // SPLIT2_HPP
