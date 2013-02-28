/**
 * @file: string_to_number.hpp
 * @author: Riccardo Murri <riccardo.murri@ictp.it>
 *
 * C++ wrappers around the strtol family functions.
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

#ifndef __STRING_TO_NUMBER_H
#define __STRING_TO_NUMBER_H

#include <string>

/** Convert string to signed long integer. */
signed long xstrtol(const std::string& num, const int base = 0);

/** Convert string to unsigned long integer. */
unsigned long xstrtoul(const std::string& num, const int base = 0);

#ifdef HAVE_LONG_LONG
/** Convert string to signed long long (64-bit) integer. */
signed long long xstrtoll(const std::string& num, 
						  const int base = 0);

/** Convert string to unsigned long long (64-bit) integer. */
unsigned long long xstrtoull(const std::string& num, 
							 const int base = 0);
#endif // HAVE_LONG_LONG

#endif // __STRING_TO_NUMBER_H
