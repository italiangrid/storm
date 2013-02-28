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


//	<StoRM: Disk Resource Management Middleware.>
//	Copyright (C) <2004> <Magnoni Luca>
//

//
//



/**
 * GPFS Wrapping Module
 * 
 */

#ifndef _STORM_POSIXWRAP
#define _STORM_POSIXWRAP


#include "../h/StoRM_SRMspec.h"

#include "../h/StoRM_WrapperInterface.h"

class PosixWrapper {//public  WrapperInterface {
   	public:
		PosixWrapper();
		~PosixWrapper();
   	
		/**
		 * open function with Posix-like parameter.
		 */ 
		int open(string path, mode_t flags);
		
		/**
		 * mkdir functionality.
		 */
		int mkdir(string path, mode_t mode);

		/**
		 * rmdir funcion.	
		 */
		int rmdir(string path,mode_t mode);
		
		/**
		 * Stat function. 
		 */
		//int stat(string path, struct stat *buf);

		/**
		 * rm function.
		 */
		int rm(string user, string path);

		/**
		 * mv function.
		 */
		
		int mv (string user,string old_path,string new_path);
		
		/**
		 * ls function.
		 */
		int ls (string path);

		//Acl funztionality.
		
		/**
		 * Add ACL
		 */
		int addAcl(string threadName, string path, string user, string  acl);

		/**
		 * Remove all ACL.
		 */
		int removeAcl(string threadName, string path,string user);

		//Space Reservation Functionality.

		/**
		 * Space Reservation.
		 */
		int spaceAlloc(string filePath, string fileName, long size);

		/**
		 * Compact Space.
		 */

		int compactSpaceInFile(string filePath);
	
		/**
		 * Truncate Functionality.
		 */
		int truncateFile(string pathToFile,long size);
  		
		/**
		 * GetACL Functionality.
		 */
		string getACL(string threadName, string file);
 
		/**
		 * StatFS 
		 */
		long statFS(string rootDir);

		/**
		 * Stat
		 */
		long statFile(string File);

		//virtual int spaceAlloc(off_t size,string path, string fileName, uid_t user, gid_t group);
			//virtual void spaceRelease();
};

#endif
