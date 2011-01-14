/**
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

//For struct stat
#include <sys/stat.h>
#include <sys/statfs.h>
#include <sys/types.h>
#include <sys/vfs.h>
#include <fcntl.h>
#include <string>

#ifdef GPFS
#include "../h/StoRM_GPFSWrapper.h"
#endif

#ifdef LUSTRE
#include "../h/StoRM_LustreWrapper.h"
#endif

#include "../h/magic.h" //Magic Number for File System detecting

#include "../h/StoRM_PosixWrapper.h"
#include "../h/common.h" //Common configuration option (DEBUG)
#include "../h/it_grid_storm_wrapper_StatWrapper.h"


/*
 * Class:     it_grid_storm_wrapper_StatWrapper
 * Method:    statfs
 * Signature: (Ljava/lang/String;)J
 */

JNIEXPORT jlong JNICALL Java_it_grid_storm_wrapper_StatWrapper_statfs
  (JNIEnv * env, jobject obj, jstring JPathRoot) 
{

	JavaVM* jvm_local;
	JavaVMOption *options;
	JavaVMInitArgs vm_args;
	jint result;
	
	vm_args.version = JNI_VERSION_1_2;
	
	//vm_args.options = options;
	//      vm_args.nOptions = 2;
	//      vm_args.ignoreUnrecognized = JNI_FALSE;
	
	JNI_GetDefaultJavaVMInitArgs ( &vm_args );
	
	result = env->GetJavaVM(&jvm_local);
	                              
 	if(result == JNI_ERR ) {
		cout<<"Error invoking the JVM"<<endl;
		exit (-1);
	}
 
	string rootDir = env->GetStringUTFChars(JPathRoot,0);
	
	struct statfs fp ;
	struct stat64 fp_gpfs;	
	int ret;
	
cout<<"STAT ON FILE:_"<<rootDir<<"_";	
	// Check underlying File System Type
	if(statfs(rootDir.c_str(),&fp)==-1)	
	{
		//Path specified for file doesn't exist in file system structure
		//Return Error value to JVM
		cout<<"<::StatWrapperLibrary> Error opening path:"<<rootDir<<endl;
		return(-1);
	}
	
	if(DEBUG) cout<<"<::StatWrapperLibrary> : FileSystemType: "<<fp.f_type<<endl;

#ifdef GPFS
	//Trying using gpfs_stat() function
	

	ret = gpfs_stat((char *)rootDir.c_str(),&fp_gpfs);
	
	if(ret==0){
	///if((fp.f_type == 48879)||(fp.f_type == 1382369651))
	//if((fp.f_type == GPFS_1_MAGIC)||(fp.f_type == GPFS_2_MAGIC))
	//{
		if(DEBUG) cout<<"<::StatLibrary>: GPFS File System Identified!"<<endl;
		GPFSWrapper gpfs;
		return(gpfs.statFS(rootDir));
	}

	else
#endif

#ifdef LUSTRE	
	if( (fp.f_type == LUSTRE_MAGIC))
	{
		if(DEBUG) cout<<"<::StatLibrary>:  LUSTRE File System identified!"<<endl;
		LustreWrapper lustre;	
		return(lustre.statFS(rootDir));
	} else
#endif
	
	if( (fp.f_type == EXT3_SUPER_MAGIC)||(fp.f_type==EXT2_SUPER_MAGIC))
	{
		if(DEBUG) cout<<"<::StatLibrary>: EXT  File System identified!"<<endl;
		PosixWrapper fs;
		return(fs.statFS(rootDir));
		//return(fs.compactSpaceInFile(file));
	}


};	

/*
 * Class:     it_grid_storm_wrapper_StatWrapper
 * Method:    stat
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_it_grid_storm_wrapper_StatWrapper_stat
  (JNIEnv *env , jobject obj, jstring JPathToFile)

{
  
  	JavaVM* jvm_local;
	JavaVMOption *options;
	JavaVMInitArgs vm_args;
	jint result;
	
	vm_args.version = JNI_VERSION_1_2;
	
	//vm_args.options = options;
	//      vm_args.nOptions = 2;
	//      vm_args.ignoreUnrecognized = JNI_FALSE;
	
	JNI_GetDefaultJavaVMInitArgs ( &vm_args );
	
	result = env->GetJavaVM(&jvm_local);
	                              
 	if(result == JNI_ERR ) {
		cout<<"Error invoking the JVM"<<endl;
		exit (-1);
	}
 
	string file = env->GetStringUTFChars(JPathToFile,0);
	
	struct statfs fp ;
	struct stat64 fp_gpfs;	
	int ret;
	
	// Check underlying File System Type
	if(statfs(file.c_str(),&fp)==-1)	
	{
		//Path specified for file doesn't exist in file system structure
		//Return Error value to JVM
		cout<<"Error opening path: "<<file<<endl;
		return(-1);
	}
	
	if(DEBUG) cout<<"<::StatWrapperLibrary> : FileSystemType: "<<fp.f_type<<endl;

#ifdef GPFS
	//if((fp.f_type == 48879)||(fp.f_type == 1382369651))
//	if((fp.f_type == GPFS_1_MAGIC)||(fp.f_type == GPFS_2_MAGIC))

	ret = gpfs_stat((char *)file.c_str(),&fp_gpfs);
	
	if(ret==0){
	
		if(DEBUG) cout<<"<::StatLibrary>: GPFS File System Identified!"<<endl;
		GPFSWrapper gpfs;
		return(gpfs.statFile(file));
	}

	else
#endif
	
#ifdef LUSTRE
  	if(fp.f_type == LUSTRE_MAGIC)
	{
		if(DEBUG) cout<<"<::StatLibrary.stat>: LUSTRE... File System identified! File: "<<file<<endl;
		LustreWrapper lustre;
		return(lustre.statFile(file));
	} else
#endif
	
	//if(fp.f_type == 61267)
	if( (fp.f_type == EXT3_SUPER_MAGIC)||(fp.f_type == EXT2_SUPER_MAGIC))
	{
		if(DEBUG) cout<<"<::StatLibrary.stat>: EXT3, EXT2... File System identified! File: "<<file<<endl;
		PosixWrapper fsW;
		return(fsW.statFile(file));
		//return(fs.compactSpaceInFile(file));
	}

  
};
	  


