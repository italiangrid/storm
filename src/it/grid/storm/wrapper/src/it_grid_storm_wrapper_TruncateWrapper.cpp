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

#include "../h/magic.h" //File System Magic Number 
#include "../h/common.h" //Common configuration optionDEBUG)
#include "../h/it_grid_storm_wrapper_TruncateWrapper.h"



/*
 * Class:     it_grid_storm_wrapper_TruncateWrapper
 * Method:    truncateFile
 * Signature: (Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_it_grid_storm_wrapper_TruncateWrapper_truncateFile
  (JNIEnv * env, jobject obj, jstring JPathToFile, jlong size) 
{
	JavaVM* jvm_local;
	JavaVMOption *options;
	JavaVMInitArgs vm_args;
	jint result;
	//      options = (void *) malloc(3 * sizeof(JavaVMOption));
	//      options[0].optionString = "-Djava.class.path=.";
	//      options[1].optionString = "-Djava.compiler=NONE";
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
	
long newsize = (long) size; 	
	if(DEBUG) cout<<"<::TruncateFile> FileName: "<<file<<endl;
	

	// Check underlying File System Type
	if(statfs(file.c_str(),&fp)==-1)	
	{
		//Path specified for file doesn't exist in file system structure
		//Return Error value to JVM
		cout<<"path: "<<file<<endl;
		return(-1);
	}
	
	if(DEBUG) cout<<"<::TruncateLibraryLibrary> : FileSystemType: "<<fp.f_type<<endl;

#ifdef GPFS
	//Trying using gpfs_stat() function
	

	ret = gpfs_stat((char *)file.c_str(),&fp_gpfs);
	
	if(ret==0){
	///if((fp.f_type == 48879)||(fp.f_type == 1382369651))
	//if((fp.f_type == GPFS_1_MAGIC)||(fp.f_type == GPFS_2_MAGIC))
	//{
		if(DEBUG) cout<<"<::TruncateLibrary>: GPFS File System Identified!"<<endl;
		GPFSWrapper gpfs;
		return(gpfs.truncateFile(file,newsize));
	} else
#endif

#ifdef LUSTRE
	if(fp.f_type == LUSTRE_MAGIC)
	{
		if(DEBUG) cout<<"<::TruncateLibrary>: LUSTRE File System identified!"<<endl;
		LustreWrapper lustre;
		return(lustre.truncateFile(file,newsize));
	} else
#endif

	//if(fp.f_type == 61267)
	if( (fp.f_type == EXT3_SUPER_MAGIC)||(fp.f_type == EXT2_SUPER_MAGIC))
	{
		if(DEBUG)cout<<"<::TruncateLibrary>: EXT3, EXT2... File System identified!"<<endl;
		PosixWrapper fs;
		return(fs.truncateFile(file,newsize));
		//return(fs.compactSpaceInFile(file));
	}
	
	
};

	
