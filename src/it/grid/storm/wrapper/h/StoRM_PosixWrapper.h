
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
