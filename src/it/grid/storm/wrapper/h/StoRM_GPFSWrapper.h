
//	<StoRM: Disk Resource Management Middleware.>
//	Copyright (C) <2004> <Magnoni Luca>
//



/**
 * GPFS Wrapping Module
 * 
 */

#ifndef _STORM_GPFSWRAP
#define _STORM_GPFSWRAP

#include <gpfs.h>

#include <sys/stat.h>

#include "../h/StoRM_SRMspec.h"

#include "../h/StoRM_WrapperInterface.h"


class GPFSWrapper {//: public  WrapInterface {
   	public:
		/**
		 * open function with Posix-like parameter.
		 */ 
		int gpfs_open(string path, int flags);
		
		/**
		 * mkdir functionality.
		 */
		int mkdir(string path, int mode);

		/**
		 * rmdir funcion.	
		 */
		int rmdir(string path);
		
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
		int addAcl(string tempDir, string path, string user, string  acl);

		/**
		 * Remove all ACL.
		 */
		int removeAcl(string tempDir, string path,string user);

		//Space Reservation Functionality.
	
		/**
		 * Space Reservation.
		 */
		int spaceAlloc(string filePath, string fileName, unsigned long size);

		/**
		 * Compact Space.
		 */

		int compactSpaceInFile(string filePath);
		

		/**
		 * Truncate Functionality.
		 */
		int truncateFile(string pathToFile, long size);
	
		/**
		 * GetACL Functionality.
		 */
		string getACL(string tempDir, string file);
	 	
		/**
		 * StafFS
		*/
		unsigned long long statFS(string rootDir);

		/**
		 * Stat
		 */
		unsigned long statFile(string File);

	 
		
  	//	public:
			GPFSWrapper();
			~GPFSWrapper();
   			//virtual int spaceAlloc(off_t size,string path, string fileName, uid_t user, gid_t group);
			//virtual void spaceRelease();
};

#endif
