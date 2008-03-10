//
//	<StoRM: Disk Resource Management Middleware.>
//	Copyright (C) <2004> <Magnoni Luca>
//



/** Pure Virtual Class for common Wrap Definition.
 * This class define common interface of Wrap Module.
 * Each Module implement funtion to a particular File System
 *
*/ 

#ifndef _LUSTRE_WRAP
#define _LUSTRE_WRAP

#include <iostream>
//#include <sys/open.h>
//#include <sys/mkdir.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/statfs.h>

#include "../h/common.h" //Common Configuration Option (DEBUG)
#include "../h/StoRM_LustreWrapper.h"

LustreWrapper::LustreWrapper() {};

LustreWrapper::~LustreWrapper() {};

int LustreWrapper::open(string path, mode_t flags) {
		return ( open(path,flags) ); 
};

int LustreWrapper::mkdir(string path,mode_t flags) {
		return ( mkdir(path,flags) );
};

int LustreWrapper::rmdir(string path,mode_t mode ) {
		return ( rmdir(path,mode) );
};

//int PosixWrapper::stat(string path, struct stat *buf) {
//		return ( stat(path,buf) );
//};

int LustreWrapper::rm(string user, string path) {
};

int LustreWrapper::mv(string user, string old_path, string new_path) {
		//return( mv(oldpath,newpath));
};

int LustreWrapper::ls(string path) {
	//return()
};


int LustreWrapper::addAcl(string tempDir,string path, string user, string acl) {
	//acl = "user:"+user+":"+acl;
	if(DEBUG) cout<<"<::LustreWrapper> :Adding on file "<<path<<" for user "<<user<<" Acl= "<<acl<<"..."<<endl;
	if(DEBUG) cout<<"<::LustreWrapper> :ACL on Lustre file system is not yet implemented..."<<endl;
	return(1);
};

int LustreWrapper::removeAcl(string tempDir, string  path,string user) {
	
	if(DEBUG) cout<<"<::LustreWrapper> :Removing ACL on file "<<path<<" for user "<<user<<"  ."<<" TempDir: "<<tempDir<<endl;
	if(DEBUG) cout<<"<::LustreWrapper> :ACL on Lustre file system is not yet implemented..."<<endl;
	return(1);
			

};

int LustreWrapper::spaceAlloc(string filePath, string fileName, long size) {
	if(DEBUG) cout<<"<::LustreWrapper> : Space Reservation on Lustre file system is not yet implemented..."<<endl;
	return(1);
};

int LustreWrapper::compactSpaceInFile(string pathToFile) {
 	if(DEBUG) cout<<"<::LustreWrapper> : Compact Space on Lustre file system is not yet implemented..."<<endl;
	if(DEBUG) cout.flush();
	return(1);

};

int LustreWrapper::truncateFile(string pathToFile, long size) {	
	if(DEBUG) cout<<"<::LustreWrapper> :Truncate su file "<<pathToFile<<" di size:"<<size<<endl;
	truncate(pathToFile.c_str(),size);
	return(1);

};

string LustreWrapper::getACL(string tempDir, string file) {	
	if(DEBUG) cout<<"<::LustreWrapper> :GetACL su file "<<file<<" not implemented on Lustre file system..."<<endl;
		
	return(NULL);
};

unsigned long long LustreWrapper::statFS(string rootDir) {
	struct statfs fp ;
	unsigned long long res;

	// Check underlying File System Type
	if(statfs(rootDir.c_str(),&fp)==-1)	
	{
		//Path specified for file doesn't exist in file system structure
		
		if(DEBUG) cout<<"Error Opening path: "<<rootDir<<endl;
		return(0);
	} else {
		res = fp.f_bsize;
		res = res * fp.f_bfree;
		return(res);
	}	
		

};

long LustreWrapper::statFile(string file) {
	struct stat nfp ;
	
	// Check underlying File System Type
	if(stat(file.c_str(),&nfp)==-1)	
	{
		//Path specified for file doesn't exist in file system structure
		
		if(DEBUG) cout<<"Error Opening Path: "<<file<<endl;
		return(0);
	}
		
	return(nfp.st_size);
		

};

#endif
