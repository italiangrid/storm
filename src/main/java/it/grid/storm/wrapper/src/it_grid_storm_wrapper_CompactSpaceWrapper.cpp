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

#include <pthread.h>
#include <iostream.h>

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

#include "../h/StoRM_PosixWrapper.h"

//Exception
#include "../h/StoRM_Ex.h"

#include "../h/magic.h" //File System Magic Number
#include "../h/common.h" //Common configuration option (DEBUG)
#include "../h/it_grid_storm_wrapper_CompactSpaceWrapper.h"

//DEBUG value defined by compilation option

JNIEXPORT jint JNICALL Java_it_grid_storm_wrapper_CompactSpaceWrapper_compactSpace 
(JNIEnv * env, jobject obj, jstring JPathToFile)

{
  	JavaVM* jvm_local;
	JavaVMOption *options;
	JavaVMInitArgs vm_args;
	
	jint result;
	
//	options = (void *) malloc(3 * sizeof(JavaVMOption));
//	options[0].optionString = "-Djava.class.path=.";
//	options[1].optionString = "-Djava.compiler=NONE";

	vm_args.version = JNI_VERSION_1_2;
//	vm_args.options = options;
//	vm_args.nOptions = 2;
//	vm_args.ignoreUnrecognized = JNI_FALSE;

	JNI_GetDefaultJavaVMInitArgs ( &vm_args );
	
	
	result = env->GetJavaVM(&jvm_local);
	 
	if(result == JNI_ERR ) {
		cout<<"Error invoking the JVM"<<endl;
		exit (-1);
	}


	//TStorageSystemInfo storageType;
	
	/**
	 * Check Underlying FileSystem Type with Statfs SysCall
	*/
	//Parsing path to eliminate name file if specified

		
	//getFileName divided in spaceAreaPath and FileName if Specified 
	
	//fileName = common_getFileName(&(spacePath));


	string file = env->GetStringUTFChars(JPathToFile,0);
	string delim="/";
	//Create path for space file
	string path = file.substr(0,file.rfind(delim,file.length())+1 ) ;

	struct statfs fp;
	struct stat64 fp_gpfs;	
	int ret;
	
	
	if(DEBUG) cout<<"<::CompactSpaceLibrary> :FileName = "<<file<<endl;
	//cout<<"SPACEALLOC: PT: "<<spacePath<<endl;


	// Check underlying File System Type
	if(statfs(path.c_str(),&fp)==-1)	
	{
		//Path not found.;
		return(-1);
	}
	
	if(DEBUG) cout<<"<::CompactSpaceLibrary>: FileSystemType: "<<fp.f_type<<endl;

#ifdef GPFS
	//Trying using gpfs_stat() function
	

	ret = gpfs_stat((char *)path.c_str(),&fp_gpfs);
	
	if(ret==0){
	///if((fp.f_type == 48879)||(fp.f_type == 1382369651))
	//if((fp.f_type == GPFS_1_MAGIC)||(fp.f_type == GPFS_2_MAGIC))
	//{
		cout<<"<::CompactSpaceLibrary>: GPFS File System Identified!"<<endl;
		GPFSWrapper gpfs;
		return(	gpfs.compactSpaceInFile(file));
	}

	else
#endif
	//if(fp.f_type == 61267)
	if( (fp.f_type == EXT3_SUPER_MAGIC)||(fp.f_type == EXT2_SUPER_MAGIC))
	{
		cout<<"<::CompactSpaceLibrary>: EXT3, EXT2... File System identified!"<<endl;
		PosixWrapper fs;
		return(fs.compactSpaceInFile(file));
	}
	
	
#ifdef LUSTRE
	if(fp.f_type == LUSTRE_MAGIC)
	{
		if(DEBUG) cout<<"<::CompactSpaceLibrary>: LUSTRE File System identified!"<<endl;
		LustreWrapper lustre;
		return(lustre.compactSpaceInFile(file));
	}
#endif


	
	
};




	                                  
