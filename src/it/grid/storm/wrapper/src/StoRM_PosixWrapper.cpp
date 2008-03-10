//
//	<StoRM: Disk Resource Management Middleware.>
//	Copyright (C) <2004> <Magnoni Luca>
//
//



/** Pure Virtual Class for common Wrap Definition.
 * This class define common interface of Wrap Module.
 * Each Module implement funtion to a particular File System
 *
*/ 

#ifndef _STORM_WRAP
#define _STORM_WRAP

#include <iostream>
//#include <sys/open.h>
//#include <sys/mkdir.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/statfs.h>

#include "../h/common.h" //Common configuration Option (DEBUG)
#include "../h/StoRM_PosixWrapper.h"

PosixWrapper::PosixWrapper() {};

PosixWrapper::~PosixWrapper() {};

int PosixWrapper::open(string path, mode_t flags) {
		return ( open(path,flags) ); 
};

int PosixWrapper::mkdir(string path,mode_t flags) {
		return ( mkdir(path,flags) );
};

int PosixWrapper::rmdir(string path,mode_t mode ) {
		return ( rmdir(path,mode) );
};

//int PosixWrapper::stat(string path, struct stat *buf) {
//		return ( stat(path,buf) );
//};

int PosixWrapper::rm(string user, string path) {
};

int PosixWrapper::mv(string user, string old_path, string new_path) {
		//return( mv(oldpath,newpath));
};

int PosixWrapper::ls(string path) {
	//return()
};


int PosixWrapper::addAcl(string tempDir,string path, string user, string acl) {
	//acl = "user:"+user+":"+acl;
	if(DEBUG) cout<<"<::PosixWrapper> :Adding on file "<<path<<" for user "<<user<<" Acl= "<<acl<<"..."<<endl;
	if(DEBUG) cout<<"<::PosixWrapper> :ACL on Ext2,3 or similar file system is not yet implemented... be patient.."<<endl;
	return(1);
};

int PosixWrapper::removeAcl(string tempDir, string  path,string user) {
	
	if(DEBUG) cout<<"<::PosixWrapper> :Removing ACL on file "<<path<<" for user "<<user<<"  ."<<" TempDir: "<<tempDir<<endl;
	if(DEBUG) cout<<"<::PosixWrapper> :ACL on Ext2,3 or similar file system is not yet implemented... be patient.."<<endl;
	return(1);
			

};

int PosixWrapper::spaceAlloc(string filePath, string fileName, long size) {
	if(DEBUG) cout<<"<::PosixWrapper> : Space Reservation on Ext2,3 or similar file system is not yet implemented... be patient.."<<endl;
	return(1);
};

int PosixWrapper::compactSpaceInFile(string pathToFile) {
 	if(DEBUG) cout<<"IMIN!!!!"<<endl;
	if(DEBUG) cout<<"<::PosixWrapper> : Compact Space on Ext2,3 or similar file system is not yet implemented... be patient.."<<endl;
	if(DEBUG) cout.flush();
	return(1);

};

int PosixWrapper::truncateFile(string pathToFile, long size) {	
	if(DEBUG) cout<<"<::PosixWrapper> :Truncate su file "<<pathToFile<<" di size:"<<size<<endl;
	if(DEBUG) cout<<"<::PosixWrapper>output:"<<endl;
	truncate(pathToFile.c_str(),size);
	return(1);

};

string PosixWrapper::getACL(string tempDir, string file) {	
	if(DEBUG) cout<<"<::PosixWrapper> :GetACL su file "<<file<<" NOT IMPLEMENTED on ext* FS be patient..."<<endl;
	if(DEBUG) cout<<"<::PosixWrapper>output:"<<endl;
	
	return(NULL);
};

long PosixWrapper::statFS(string rootDir) {
	struct statfs fp ;
	// Check underlying File System Type
	if(statfs(rootDir.c_str(),&fp)==-1)	
	{
		//Path specified for file doesn't exist in file system structure
		
		if(DEBUG) cout<<"Error Opening path: "<<rootDir<<endl;
		return(-1);
	}
	else
		return(fp.f_bsize*fp.f_bfree);
		

};

long PosixWrapper::statFile(string file) {
	struct stat nfp ;
	unsigned long long res;
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
