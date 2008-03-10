/**
 * @file: string_to_number.cpp
 * @author: Riccardo Murri <riccardo.murri@ictp.it>
 *
 * C++ wrappers around the strtol/strtoul family functions.
 *
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * Copyright (c) 2005, 2006 Antonio Messina <antonio.messina@ictp.it>
 * for the ICTP project EGRID.
 *
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

/// CVS Revsion info
static const char* const RCSID = "$Id: string_to_number.cpp,v 1.6 2006/03/28 08:24:52 rmurri Exp $";


#include "string_to_number.hpp"
#include "fs_errors.hpp"

#include <cstdlib>
#include <errno.h>
#include <sstream>
#include <stdexcept>


/** Template code for wrapping the strto(u)l family functions. */
template<typename numeric_type>
static numeric_type 
__string_to_num (const std::string& num, const int base, 
                 numeric_type convert(const char*, char**, int))
{
  char *endchr = NULL;
  numeric_type result = convert (num.c_str(), &endchr, base);
  if (0 != errno)
    // unsuccessful conversion
    {
      const int err = errno;
      std::ostringstream msg;
      switch (errno) 
        {
        case ERANGE:
          msg <<  "Numerical result out of range: '" << num << "'";
          throw std::out_of_range(msg.str().c_str());
        case EINVAL:
          msg << "Numerical base " << base 
              << " not supported in string to number conversion", 
          throw std::invalid_argument(msg.str().c_str());
        default:
          {
            msg << __FILE__ ": conversion of string '"
                << num
                << "' to integer failed";
            throw fs::system_error(msg.str(), err);
          }
        }
    };
  if (num.c_str() == endchr)
    // unsuccessful conversion - no digits
    {
      std::ostringstream msg;
      msg << "No numerical digits in string '" << num << "'";
      throw std::domain_error(msg.str().c_str());
    }

  // else, the conversion is successful
  return result;
}

/** Convert string to signed long integer. 
 *
 * <p>Converts string argument @a num into a long integer,
 * interpreting characters in @a num as digits in base @a base.
 *
 * <p>This is a simple wrapper around the C library function @c
 * strtol(); see "man strtol" for details.
 *
 * @param  num  The string to be converted to an integer.
 * @param  base Conversion base; see man 3 strtol
 * 
 * @throw std::out_of_range when the converted number would overflow
 * or underflow the integer type; std::invalid_argument when
 * conversion from digits in the specified @a base is not supported;
 * std::domain_error if @a num does not contain any digits of base @a
 * base;  fs::system_error, if other system error occurred.
 * 
 * @return The numerical value of @a num.
 */
signed long xstrtol (const std::string& num, const int base)
{
  return __string_to_num<long>(num, base, strtol);
}

/** Convert string to unsigned long integer.
 *
 * <p>Converts string argument @a num into an unsigned long integer,
 * interpreting characters in @a num as digits in base @a base.
 *
 * <p>This is a simple wrapper around the C library function @c
 * strtoul(); see "man strtoul" for details.
 *
 * @param  num  The string to be converted to an integer.
 * @param  base Conversion base; see man 3 strtoul
 * 
 * @throw std::out_of_range when the converted number would overflow
 * or underflow the integer type; std::invalid_argument when
 * conversion from digits in the specified @a base is not supported;
 * std::domain_error if @a num does not contain any digits of base @a
 * base;  fs::system_error, if other system error occurred.
 * 
 * @return The numerical value of @a num.
 */
unsigned long xstrtoul (const std::string& num, const int base)
{
  return __string_to_num<unsigned long>(num, base, strtoul);
}


#ifdef HAVE_LONG_LONG

/** Convert string to signed long long (64-bit) integer.
 *
 * <p>Converts string argument @a num into a long long integer,
 * interpreting characters in @a num as digits in base @a base.
 *
 * <p>This is a simple wrapper around the C library function @c
 * strtoll(); see "man strtoll" for details.
 *
 * @param  num  The string to be converted to an integer.
 * @param  base Conversion base; see man 3 strtol
 * 
 * @throw std::out_of_range when the converted number would overflow
 * or underflow the integer type; std::invalid_argument when
 * conversion from digits in the specified @a base is not supported;
 * std::domain_error if @a num does not contain any digits of base @a
 * base;  fs::system_error, if other system error occurred.
 * 
 * @return The numerical value of @a num.
 */
signed long long xstrtoll (const std::string& num, 
                           const int base)
{
  return __string_to_num<long long>(num, base, strtoll);
}

/** Convert string to unsigned long long (64-bit) integer.
 *
 * <p>Converts string argument @a num into an unsigned long long
 * integer, interpreting characters in @a num as digits in base @a
 * base.
 *
 * <p>This is a simple wrapper around the C library function @c
 * strtoull(); see "man strtoull" for details.
 *
 * @param  num  The string to be converted to an integer.
 * @param  base Conversion base; see man 3 strtol
 * 
 * @throw std::out_of_range when the converted number would overflow
 * or underflow the integer type; std::invalid_argument when
 * conversion from digits in the specified @a base is not supported;
 * std::domain_error if @a num does not contain any digits of base @a
 * base;  fs::system_error, if other system error occurred.
 * 
 * @return The numerical value of @a num.
 */
unsigned long long xstrtoull (const std::string& num, 
                              const int base)
{
  return __string_to_num<unsigned long long>(num, base, strtoull);
}

#endif // HAVE_LONG_LONG
