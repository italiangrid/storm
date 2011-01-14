/*
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

//
//	<StoRM: Disk Resource Management Middleware.>
//	Copyright (C) <2004> <Magnoni Luca>
//
//	This file is a part of StoRM.
//	StoRM is free software; you can redistribute it and/or
//	modify it under the terms of the GNU Lesser General Public License
//	as published by the Free Software Foundation; either version 2.1
//	of the License, or (at your option) any later version.
//
//	StoRM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
//	See the GNU Lesser General Public License for more details.
//
//	You should have received a copy of the GNU Lesser General
//	Public License along with this library; if not,
//	write to the Free Software Foundation, Inc., 59 Temple Place, 
//	Suite 330, Boston, MA 02111-1307 USA 
//
//
//



/** Pure Virtual Class for common Wrap Definition.
 * This class define common interface of Wrap Module.
 * Each Module implement funtion to a particular File System
 *
*/ 

#ifndef _STORM_WRAP
#define _STORM_WRAP

#include"unistd.h"

#include"../h/StoRM_SRMspec.h"

class WrapperInterface {
	private:
		/**
		 * open function with Posix-like parameter.
		 */ 
		virtual int open(string path, int flags) = 0;
		
		/**
		 * mkdir functionality.
		 */
		virtual int mkdir(string path, mode_t mode) = 0;

		/**
		 * rmdir funcion.	
		 */
		virtual int rmdir(string path) = 0;
		
		/**
		 * Stat function. 
		 */
		virtual int stat(string path, struct stat *buf) = 0;

		/**
		 * rm function.
		 */
		virtual int rm(string user, string path) = 0;

		/**
		 * mv function.
		 */
		
		virtual int mv (string user,string old_path,string new_path) = 0;
		
		/**
		 * ls function.
		 */
		virtual int ls (string path) = 0;

		//Acl funztionality.
		
		/**
		 * Add ACL
		 */
		virtual int addAcl(string path, string user, string  acl) = 0;

		/**
		 * Remove all ACL.
		 */
		virtual int AllUserAcls(string path,string user) = 0;

		//Space Reservation Functionality.

		/**
		 * Space Reservation.
		 */
		virtual int spaceAlloc(string filePath, string fileName, long size) = 0;

		/**
		 * Compact Space.
		 */

		virtual int compactSpaceInFile(string filePath) = 0;
		
	public:
		 
	 	WrapperInterface() {}; 
		/**
		 * Destructor.
		*/
		virtual ~WrapperInterface() {};

		/**
		 * This Function provide space allocation.
		 * @param size Size of space to reserve.
		 * @
		 *
		*/
		
		

};

#endif
