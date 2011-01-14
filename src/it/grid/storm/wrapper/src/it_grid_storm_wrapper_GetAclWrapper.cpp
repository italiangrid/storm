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
#include "../h/it_grid_storm_wrapper_GetAclWrapper.h"

JNIEXPORT jstring JNICALL Java_it_grid_storm_wrapper_GetAclWrapper_getAcl
  (JNIEnv *env, jobject obj, jstring JTempDir, jstring JPathToFile) 
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
	struct statfs fp ;
	string tempDir = env->GetStringUTFChars(JTempDir,0);
	string returnString;
	struct stat64 fp_gpfs;	
	int ret;
	
	if(DEBUG) cout<<"<::GetAclLibrary> FileName: "<<file<<" in Path: "<<endl;

	
	

	// Check underlying File System Type
	if(statfs(file.c_str(),&fp)==-1)	
	{
		//Path specified for file doesn't exist in file system structure
		//Return Error value to JVM
		cout<<"path: "<<file<<endl;
		return(NULL);
	}
	
	if(DEBUG) cout<<"<::GetAclLibrary> : FileSystemType: "<<fp.f_type<<endl;

#ifdef GPFS
	//Trying using gpfs_stat() function
	

	ret = gpfs_stat((char *)file.c_str(),&fp_gpfs);
	
	if(ret==0){
	///if((fp.f_type == 48879)||(fp.f_type == 1382369651))
	//if((fp.f_type == GPFS_1_MAGIC)||(fp.f_type == GPFS_2_MAGIC))
	//{
		if(DEBUG) cout<<"<::GetAclLibrary>: GPFS File System Identified!"<<endl;
		GPFSWrapper gpfs;
		return( env->NewStringUTF((gpfs.getACL(tempDir,file)).c_str()) );
		
	} else
#endif
	
#ifdef LUSTRE
	if(fp.f_type == LUSTRE_MAGIC)
	{
		if(DEBUG) cout<<"<::GetAclLibrary>: LUSTRE File System identified!"<<endl;
		LustreWrapper lustre;
		return(env->NewStringUTF( (lustre.getACL(tempDir, file)).c_str()) );
	}
#endif

	//if(fp.f_type == 61267)
	if( (fp.f_type == EXT3_SUPER_MAGIC)||(fp.f_type == EXT2_SUPER_MAGIC))
	{
		if(DEBUG) cout<<"<::GetAclLibrary>: EXT3, EXT2... File System identified!"<<endl;
		PosixWrapper fs;
		return ( env->NewStringUTF( (fs.getACL(tempDir, file)).c_str()) );
	}
	
	
};



