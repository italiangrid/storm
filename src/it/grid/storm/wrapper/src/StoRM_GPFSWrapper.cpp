//
//	<StoRM: Disk Resource Management Middleware.>
//	Copyright (C) <2004> <Magnoni Luca>
//



/** Pure Virtual Class for common Wrap Definition.
 * This class define common interface of Wrap Module.
 * Each Module implement funtion to a particular File System
 *
*/ 

#ifndef _STORM_WRAP
#define _STORM_WRAP

//For File Access
#include <iostream>
#include <fstream>
//#include <sys/open.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/statfs.h>
#include <sys/wait.h>
#include <pwd.h>
#include <fcntl.h>
#include <stdio.h>
#include <string> 
#include <math.h>
//for execl function
#include <unistd.h>

#include "../h/StoRM_SRMspec.h"
#include "../h/common.h" //Common Configuration Option (DEBUGUG)
#include "../h/StoRM_GPFSWrapper.h"

#define ACL_NUM_CHAR 4
 
using namespace std;

GPFSWrapper::GPFSWrapper() {};

GPFSWrapper::~GPFSWrapper() {};

int GPFSWrapper::gpfs_open(string path, int flags) {
	//	return ( open(path,flags) ); 
};

int GPFSWrapper::mkdir( string path,int flags) {
		return ( mkdir(path,flags) );
};

int GPFSWrapper::rmdir( string path) {
		//return ( rmdir(path,mode) );
};

//int GPFSWrapper::stat( string path, struct stat *buf) {
	//	return ( gpfs_stat(const( path.c_str()),buf) );
//};

int GPFSWrapper::rm(string user,  string path) {
};

int GPFSWrapper::mv(string user, string old_path,  string new_path) {
		//return( mv(oldpath,newpath));
};

int GPFSWrapper::ls(string path) {
//	return();
;
};

/**
 * In this versione we assume that "acl" to insert is realtive to user
 * and it's written in posix permission form, (ex:r---)
 */
int GPFSWrapper::addAcl( string tempDir, string path, string user, string acl) {
	
	pid_t pid;
	string filename = path;
	const string initGetCom = "/usr/lpp/mmfs/bin/mmgetacl";
	const string initPutCom = "/usr/lpp/mmfs/bin/mmputacl";
	const string getACL = "mmgetacl";
	const string getOption = "-o";
	const string putACL = "mmputacl";
	const string putOption = "-i";
	//Get FILENAME from path specified.
	const string fileName =   path.substr(path.rfind("/",path.size())+1, path.size() - path.rfind("/",path.size()));
	string complete_path = path;	
	const string oldACLFile = tempDir+"/"+"acl_"+fileName;
	const string newACLFile = tempDir+"/"+"nacl_"+fileName;
	string lockFile   = tempDir+"/"+"lock_"+fileName;
	//Important ACL mask!!!
	const string mask = "mask::rwx- \n";
	//File access
	ifstream fp_in;
	ifstream fp2_in;
	ofstream fp_out;

	string myFile;
	string tmpFile;	
	string currentString;
	string tmpString;
	string newAcl;
	bool maskFound = false;	
	bool userACLType = false;
	bool groupACLType = false;
	bool ACLDone = false;		
	bool USERFOUND =false;
	
	// GET USERNAME FROM UID
	// Check if the user is specified by UID or by user name (obsolete case, to be removed!)
	
	//Create uid_t from string	
	uid_t user_uid;	
	struct passwd * passwd_p;
	int uid_int;

	//Convert Lock File Name !
//lockFile = lockFile +"_"+rand_r(1212);

	int pos = 0;
	pos = complete_path.find('/',pos);

	while(pos != string::npos) {
		complete_path[pos]='_';
		pos = complete_path.find('/',pos);
	}
	
	lockFile = lockFile + complete_path;
	cout<<"completePath:"<<complete_path<<endl;

	//Convert String into INT rapresentation
	uid_int = atoi(user.c_str());
	if(uid_int!=0) {
		//'User' string contains a valid UID!
		user_uid = uid_int;
		if(DEBUG) cout<<"USER:"<<user<<"  UID:"<<uid_int<<endl;
		//Mapping UID  with local user name 
		passwd_p = getpwuid(user_uid);
		if(passwd_p!=NULL) {
			user = passwd_p->pw_name;
			if(DEBUG) cout<<"UID: "<<uid_int<<" mapped into:"<<user<<endl;
		} else {
			if(DEBUG) cout<<"Mapping not Found for user "<<user<<"!"<<endl;
		}

	} else {
		//Not a valid UID, using user name indicate as a string in 'user' specified
		if (DEBUG) cout<<"User '"<<user<<"' does not contains a valid UID!";
	}


 
	//Create ACL entry in form : user:username:r--
	//Modify acl type for GPFS
	//if(strstr(acl.c_str(),"c")==NULL)
	//		acl =acl+"-";
	
	//acl= "user:"+user+":"+acl; 
	
	if(DEBUG) cout<<"<::GPFSWrapper> :Adding on file "<<filename<<" for user "<<user<<" Acl= "<<acl<<"..."<<endl;
	
	//OPEN FILE FOR LOCK PURPOSES
	
 	int fd;
	//fd = open(lockFile.c_str(), O_WR | O_CREAT | O_EXCL);
 	do{
		fd = open(lockFile.c_str(), O_RDWR| O_CREAT | O_EXCL);
 		// the file already exist; another process is 
		// holding the lock
		//cout<<"the file is currently locked; try again"<<endl;
		///***********************TOREMOVE , FILE MUST NOT BE ERASED IF LOCK IS STILL PRESENT!!!!!
		sleep(1);
		fd = open(lockFile.c_str(), O_RDWR| O_CREAT | O_TRUNC);
 		
	 } while(fd<0);

	

	pid = fork( );

	if (pid == 0) {

		if(DEBUG) cout<<"CHILD process"<<endl;
		//close(fd);
	//if(execl("/usr/lpp/mmfs/bin/mmgetacl", "mmgetacl","-o","oldACL","/mnt/gpfs/testACL",NULL) == -1) {
		if(execl(initGetCom.c_str(), getACL.c_str(), getOption.c_str(), oldACLFile.c_str(), filename.c_str(), NULL) == -1) {
	//	if( execl("mmgetacl -o oldACL /mnt/gpfs/testACL",NULL)==-1) {
			if(DEBUG) cout<<"<GPFS::ACLWRAPPER>error in exec"<<endl;
			if(DEBUG) perror("GPFS_Wrapper::Error: ");
			return(-2);
		}

		exit(1);

	} else {

		if(DEBUG) cout<<"In the PARENT process"<<endl;
		//wait for child termination
		//NULL becouse don't care about exit child status
		
		wait(NULL);
		
		//Identify ACL To ADD Type. Ex: user:pippo:r--- or group:pippo_group:r---
		if(strstr(acl.c_str(),"user")!=NULL) {
			if(DEBUG) cout<<"User ACL Type"<<endl;
			userACLType=true;
		}
		if(strstr(acl.c_str(),"group")!=NULL) {
			if(DEBUG) cout<<"Group ACL Type"<<endl;
			groupACLType=true;
		}	

		//Read OLDACL file, modify it adding new ACL specified by request ang call mmputacl command
		
		//Read OLDACL FILE
		fp_in.open(oldACLFile.c_str(),ios::in);

		//cout<<"STREEEEEM OLD oldFile Opened"<<endl;

		//Check if ACL for USER Is not yet present, otherwise insert a new ACL that is the OR of prevous ACLS
		while(!fp_in.eof()) {
			fp_in>>tmpString;
			if(!fp_in.eof()) {
				//OLD VERSION
				//if((strstr(tmpString.c_str(),user.c_str())!=NULL))  {
				// The check for "#" char identify the #owner/#grouo string
				// that should not be modified by StoRM!
				if( (strstr(tmpString.c_str(),user.c_str())!=NULL)&&(strstr(tmpString.c_str(),"#")==NULL))  {
					if(DEBUG) cout<<"<GPFS_WRAPPER: DOUBLE acl FOUND!!!!>"<<endl;
					
					USERFOUND=true;
					newAcl = acl;
					// Create new ACL adding new permission on old one : 
					// r-- + -w- = rw-...
					// NB: compare "-" char with [r,w,x]... 
					for(int i=0;i<ACL_NUM_CHAR;i++) {
					
						if(tmpString[tmpString.size()-i-1] == acl[acl.size()-i-1])
							newAcl[acl.size()-i-1] = acl[acl.size()-i-1];
						else {
							if(tmpString[tmpString.size()-i-1]>acl[acl.size()-i-1])
								newAcl[acl.size()-i-1] = tmpString[tmpString.size()-i-1];	
							else	
								newAcl[acl.size()-i-1] = acl[acl.size()-i-1];		
						}
					}
					
					tmpFile = tmpFile + newAcl + "\n";
			
				} else
					tmpFile = tmpFile + tmpString+"\n";
			}
		}
			
		fp_in.close();
	
		if(!USERFOUND) {
			//RE-Read OLDACL FILE
			fp2_in.open(oldACLFile.c_str(),ios::in);
			//fp_in.open(oldACLFile.c_str(),ios::in);
			//fp_in.seekg(ios_base::beg);
			
			while(!fp2_in.eof()) {
	
				fp2_in>>currentString;
				//This control is for ACL File not well formatted 
				if(!fp2_in.eof()) {
	
					if(DEBUG) cout<<" currentString: "<<currentString<<" pos "<<fp2_in.tellg()<<endl;  	
					if(strstr(currentString.c_str(),"mask")!=NULL) 
						maskFound=true;
							
					if(maskFound) {
						if(userACLType&&(strstr(currentString.c_str(),"group")!=NULL)&& !ACLDone) {
							ACLDone = true;
							myFile = myFile + acl+"\n" + currentString+"\n";
						} else
							myFile = myFile + currentString+"\n";
					} else
						myFile = myFile + currentString+"\n";
				}			
			}
	
			if(maskFound&&(groupACLType|| !ACLDone) )
				myFile = myFile + acl+"\n";
			
			if(!maskFound)
				myFile = myFile + mask + acl +"\n";		
					
			//If new ACL Type == group, new ACL is inserted at the end of file   
			//if(groupACLType)
			//	myFile = myFile + acl +"\n";

			fp2_in.close();
		
		} else {
			
			myFile = tmpFile;
		
		}
		
		//MyFile String contain  old ACL File

		if(DEBUG) cout<<"String New ACL :\n"<<myFile<<endl;	 
		long protection = 0666;		
		//Write new ACL on File
		fp_out.open(newACLFile.c_str(),ios::out|ios::trunc );
		
		fp_out<<myFile;
		
		fp_out.close();
		//fp_in.close();

		//Invoke command for PUTTING new ACL on file

		pid = fork();

		if (pid == 0) {
			
			//close(fd);
			if(DEBUG) cout<<"CHILD process"<<endl;
			//if(execl("/usr/lpp/mmfs/bin/mmgetacl", "mmgetacl","-o","oldACL","/mnt/gpfs/testACL",NULL) == -1) {
			if(execl(initPutCom.c_str(), putACL.c_str(),putOption.c_str(), newACLFile.c_str(), filename.c_str(), NULL) == -1) {
			//	if( execl("mmgetacl -o oldACL /mnt/gpfs/testACL",NULL)==-1) {
			if(DEBUG) cout<<"error in exec"<<endl;
			if(DEBUG) perror("GPFS_Wrapper::Error: ");
			return(-2);
			}
	
			exit(1);

		} else {
		
			if(DEBUG) cout<<"In the PARENT process"<<endl;
			//wait for child termination
			//NULL cose don't care about exit child status
			wait(NULL);
	//		fp_out.close();
	//		fp_in.close();
	//		fp2_in.close();	
	
			//Remove File created for  ACL purposes
			unlink(oldACLFile.c_str());
			unlink(newACLFile.c_str());
 
		
			//Release Lock deleting file
			close(fd);
			unlink(lockFile.c_str());	
	
			if(DEBUG) cout<<"<:ENDD:GPFSWrapper> :ACL on GPFS done.."<<endl;
	
		}

	} 

};

//int GPFSWrapper::getACL(){};

int GPFSWrapper::removeAcl(string tempDir, string path,string user) {
		
	pid_t pid;
	string filename = path;
	const string initGetCom = "/usr/lpp/mmfs/bin/mmgetacl";
	const string initPutCom = "/usr/lpp/mmfs/bin/mmputacl";
	const string getACL = "mmgetacl";
	const string getOption = "-o";
	const string putACL = "mmputacl";
	const string putOption = "-i";
	const string fileName = path.substr(path.rfind("/",path.size())+1, path.size() - path.rfind("/",path.size()));	
	const string oldACLFile = tempDir+"/"+"acl_"+fileName;
	const string newACLFile = tempDir+"/"+"nacl_"+fileName;
	const string lockFile =  tempDir+"/"+"lock_"+fileName;
	
	//File access
	ifstream fp_in;
	ofstream fp_out;

	string myFile;
	string currentString;
	bool maskFound = false;	
	bool userACLType = false;
	bool groupACLType = false;
	bool ACLDone = false;		

	int status;

	// GET USERNAME FROM UID
	// Check if the user is specified by UID or by user name (obsolete case, to be removed!)
	
	//Create uid_t from string	
	uid_t user_uid;	
	struct passwd * passwd_p;
	int uid_int;
	//Convert String into INT rapresentation
	uid_int = atoi(user.c_str());
	if(uid_int!=0) {
		//'User' string contains a valid UID!
		user_uid = uid_int;
		if(DEBUG) cout<<"USER:"<<user<<"  UID:"<<uid_int<<endl;
		//Mapping UID  with local user name 
		passwd_p = getpwuid(user_uid);
		if(passwd_p!=NULL) {
			user = passwd_p->pw_name;
			if(DEBUG) cout<<"UID: "<<uid_int<<" mapped into:"<<user<<endl;
		} else {
			if(DEBUG) cout<<"Mapping not Found for user "<<user<<"!"<<endl;
		}

	} else {
		//Not a valid UID, using user name indicate as a string in 'user' specified
		if (DEBUG) cout<<"User '"<<user<<"' does not contains a valid UID!";
	}


 


	if(DEBUG) cout<<"<::GPFSWrapper> : Removing ACL  on file "<<filename<<" for user "<<user<<endl;

	//fp_in.open(oldACLFile.c_str(),ios::in|ios::trunc);
	//fp_in.close();
	
	//OPEN FILE FOR LOCK PURPOSES
	int fd;
 	do{
		fd = open(lockFile.c_str(), O_RDWR| O_CREAT | O_EXCL);
 		// the file already exist; another process is 
		// holding the lock
		//cout<<"the file is currently locked; try again"<<endl;
		//sleep(2);
	 }while(fd<0);

	//
	
	pid = fork( );

	if (pid == 0) {

		//close(fd);
		if(DEBUG) cout<<"CHILD process"<<endl;
		//if(execl("/usr/lpp/mmfs/bin/mmgetacl", "mmgetacl","-o","oldACLrm","/mnt/gpfs/testACL",NULL) == -1) {
		execl(initGetCom.c_str(), getACL.c_str(), getOption.c_str(), oldACLFile.c_str(), filename.c_str(),NULL);
		//	if( execl("mmgetacl -o oldACL /mnt/gpfs/testACL",NULL)==-1) {
			if(DEBUG) cout<<"error in exec"<<endl;
			if(DEBUG) perror("GPFS_Wrapper::Error: ");
			return(-2);
			exit(1);

	} else {
		
		if(DEBUG) cout<<"In the PARENT process"<<endl;
		//wait for child termination
		//NULL cose don't care about exit child status
		wait(NULL);

		//Read OLDACL file, modify it deleting ACL specified by request ang call mmputacl command
		
		//Read OLDACL FILE
		fp_in.open(oldACLFile.c_str(),ios::in);

		if(DEBUG) cout<<"oldFile Opened"<<endl;

		while(!fp_in.eof()) {
			fp_in>>currentString;
			//This control is for ACL File not well formatted 
			if(!fp_in.eof()){
				if(DEBUG) cout<<" curretnString: "<<currentString<<"pos"<<fp_in.tellg()<<endl;  	
				//if((strstr(currentString.c_str(),user.c_str())!=NULL))
				
				// The check for "#" char identify the #owner/#group string
				// that should not be modified by StoRM!
				if( (strstr(currentString.c_str(),user.c_str())!=NULL)&&(strstr(currentString.c_str(),"#")==NULL) )
						;
					
				else
						myFile = myFile + currentString+"\n";
			}			
		
		}

		fp_in.close();

		//MyFile String contain  old ACL File
		if(DEBUG) cout<<"String New ACL :\n"<<myFile<<endl;	 
			
		//Write new ACL on File
		fp_out.open(newACLFile.c_str(),ios::out|ios::trunc);
		
		fp_out<<myFile;
		
		fp_out.close();
		

		//Invoke command for PUTTING new ACN on file

		pid = fork();

		if (pid == 0) {
		//	close(fd);
			if(DEBUG) cout<<"CHILD process"<<endl;
			//if(execl("/usr/lpp/mmfs/bin/mmgetacl", "mmgetacl","-o","oldACL","/mnt/gpfs/testACL",NULL) == -1) {
			execl(initPutCom.c_str(), putACL.c_str(),putOption.c_str(), newACLFile.c_str(), filename.c_str(), NULL);
				//	if( execl("mmgetacl -o oldACL /mnt/gpfs/testACL",NULL)==-1) {
				if(DEBUG) cout<<"error in exec"<<endl;
				if(DEBUG) perror("GPFS_Wrapper::Error: ");
				return(-2);
				exit(1);

		} else {
			if(DEBUG) cout<<"In the PARENT process"<<endl;
			//wait for child termination
			//NULL cose don't care about exit child status
			wait(NULL);
			//fp_in.close();
			//Release Lock deleting File
			close(fd);				
			
			//Remove File created for  ACL purposes
			unlink(oldACLFile.c_str());
			unlink(newACLFile.c_str());
		
			//UNLOCK FILE	
			unlink(lockFile.c_str());	
	
			if(DEBUG) cout<<"<:ENDD:GPFSWrapper> :ACL on GPFS file system ..."<<endl;
	
			
		}
	}
};

int GPFSWrapper::spaceAlloc(string filePath, string fileName, unsigned long size) {
	
			
	if(DEBUG) cout<<"GPFS Alloc: "<<size<<" in Path:"<<filePath<<"for  FileName: "<<fileName<<endl;
	int rc;
	int file_h;
	int fh = -1;
	//string completePath = fileName;
	const char * file = fileName.c_str();
	long long startOffset = 0;
	long long numBytes = size;
	

	fh = open(file, O_CREAT|O_RDWR, 0640);
	if(fh < 0)
	{
		perror(file);
		return(-2);
	} 
	
	rc = gpfs_prealloc(fh, startOffset, numBytes);
	
	if(rc < 0)
	{
		fprintf(stderr, "Erorr %d Preallcation %lld for %lld in %s \n", errno, startOffset, numBytes, file);
		return(-3);
	}
	
	//Get user ID e Group ID with stat syscall in source directory.
	//chown must be done for each new directory created for soddisfare Space Reservation Request.
	
	//fchown(fh,)
		
	close(fh);	
};

	


int GPFSWrapper::compactSpaceInFile(string pathToFile) {
	if(DEBUG) cout<<"<GPFSWrapper> : Compact Space on GPFS File System is not yet implemented... be patient.."<<endl;
	return(1);
};

int GPFSWrapper::truncateFile(string pathToFile,long size) {
	truncate(pathToFile.c_str(),size);
}

string GPFSWrapper::getACL(string tempDir, string filename) {

	pid_t pid;
	const string initGetCom = "/usr/lpp/mmfs/bin/mmgetacl";
	const string getACL = "mmgetacl";
	const string getOption = "-o";
	const string oldACLFile = tempDir+"/"+"oldACL";//i+threadName;
	string resultString;
	string tmpString;
	//File access
	ifstream fp_in;
	
	if(DEBUG) cout<<"<GPFSWrapper> : GetAcl on GPFS:"<<endl;
	
	pid = fork( );

	if (pid == 0) {

		if(DEBUG) cout<<"CHILD process"<<endl;
		//if(execl("/usr/lpp/mmfs/bin/mmgetacl", "mmgetacl","-o","oldACLrm","/mnt/gpfs/testACL",NULL) == -1) {
		execl(initGetCom.c_str(), getACL.c_str(), getOption.c_str(), oldACLFile.c_str(), filename.c_str(),NULL);
		//	if( execl("mmgetacl -o oldACL /mnt/gpfs/testACL",NULL)==-1) {
			if(DEBUG) cout<<"error in exec"<<endl;
			if(DEBUG) perror("GPFS_Wrapper::Error: ");
			return(NULL);
			exit(1);

	} else {

		if(DEBUG) cout<<"In the PARENT process"<<endl;
		//wait for child termination
		//NULL cose don't care about exit child status
		wait(NULL);
	
		//Read OLDACL FILE
		fp_in.open(oldACLFile.c_str(),ios::in);
		while(!fp_in.eof()) {
			fp_in>>tmpString;
			if(!fp_in.eof()) {
				resultString = resultString+tmpString+"\n";
			}
		}

		unlink(oldACLFile.c_str());

		return (resultString);
	}
	
}

unsigned long long GPFSWrapper::statFS(string rootDir) {
	struct statfs fp ;
	unsigned long long res;
	
	// Check underlying File System Type
	if(statfs(rootDir.c_str(),&fp)==-1)	
	{
		//Path specified for file doesn't exist in file system structure
		//Return Error value to JVM
		if(DEBUG) cout<<"path: "<<rootDir<<endl;
		return(0);
	
	} else {
		//cout<<"<::StatFS> Directory "<<rootDir<<endl;
		//cout<<"<::StatFS> Size: "<<fp.f_bsize*fp.f_bfree<<endl;
		res = fp.f_bsize ;
		res = res* fp.f_bfree;
		//cout<<"<::StatFS> Size: "<<res<<endl;
		/*
		;cout<<"<::StatFS> FP_BSize: "<<fp.f_bsize<<endl;
		cout<<"<::StatFS> FP_BFree: "<<fp.f_bfree<<endl;
		cout<<"<::StatFS> FP_BAvail: "<<fp.f_bavail<<endl;
		
		rootDir = "/home/";		
		statfs(rootDir.c_str(),&fp);	
		cout<<"<::StatFS> Directory "<<rootDir<<endl;
		cout<<"<::StatFS> Size: "<<fp.f_bsize*fp.f_bfree<<endl;
		cout<<"<::StatFS> FP_BSize: "<<fp.f_bsize<<endl;
		cout<<"<::StatFS> FP_BFree: "<<fp.f_bfree<<endl;
		cout<<"<::StatFS> FP_BAvail: "<<fp.f_bavail<<endl;
		*/
		
		return(res);
	}
		

}

unsigned long GPFSWrapper::statFile(string file) {
	
	struct stat fp ;
	// Check underlying File System Type
	
	if(DEBUG) cout<<"GPFS STAT FILE"<<endl;
	if(stat(file.c_str(),&fp)==-1)	
	{
		//Path specified for file doesn't exist in file system structure
		
		if(DEBUG) cout<<"<::GPFS Stat> Error Opening Path: "<<file<<endl;
		return(0);
	}
	else
		return(fp.st_size);
		

};
#endif
