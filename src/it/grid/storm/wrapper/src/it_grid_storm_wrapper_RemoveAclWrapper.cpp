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
#include "../h/it_grid_storm_wrapper_RemoveAclWrapper.h"

JNIEXPORT jint JNICALL Java_it_grid_storm_wrapper_RemoveAclWrapper_removeAcl
  (JNIEnv *env, jobject obj, jstring JTempDir, jstring JPath, jstring JUser)
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


	string file = env->GetStringUTFChars(JPath,0);
	string user = env->GetStringUTFChars(JUser,0);
	string tempDir = env->GetStringUTFChars(JTempDir,0);
	
	struct statfs fp ;
	struct stat64 fp_gpfs;	
	int ret;
	
	
	
	if(DEBUG) cout<<"<::AclLibrary> FileName: "<<file<<endl;
	//cout<<"SPACEALLOC: PT: "<<spacePath<<endl;


	// Check underlying File System Type
	if(statfs(file.c_str(),&fp)==-1)	
	{
		//File specified does not exist;
		return(-1);
	}
	
	
//This chek is not necessary if no ACL to remove is specified

/*	//
	//Verifiy ACL format.
	//
	
	acltype = acl.substr(0,acl.find(":",0));

	//Check ACL Type
	
	if(acltype!="user"&&(acltype!="group")) {
		acl_error=true;
		cout<<"<ACLWrapper>::ACL Type Error"<<endl;
		return -3;
	}
	//Check USER
	//DA FARE....
	
	
	//Check ACL Permission
	aclpermission = acl.substr(acl.rfind(":",acl.length())+1,(acl.length(),acl.rfind(":",acl.length())));
	//		cout<<"---------"+aclpermission<<endl;
	if(aclpermission.length()>ACL_NUM_CHAR)
		acl_error=true;
	
	//Check if ACL contain only rigth kind of char (r,w,x,-)
	for(int i=0;i<aclpermission.length();i++) {
		if( (aclpermission.substr(i,1)!="r")&&(aclpermission.substr(i,1)!="w")&&(aclpermission.substr(i,1)!="x")&&(aclpermission.substr(i,1)!="-") )
					acl_error=true;
	}
	
	if(acl_error) {
		//ACL not in correct format
		cout<<"<ACLWrapper>::ACL Perission Format Error"<<endl;
		return -3;
	}
*/
	
	if(DEBUG) cout<<"<::RemoveAclLibrary> : FileSystemType: "<<fp.f_type<<endl;

#ifdef GPFS
	//if((fp.f_type == 48879)||(fp.f_type == 1382369651))
	//Trying using gpfs_stat() function
	

	ret = gpfs_stat((char *)file.c_str(),&fp_gpfs);
	
	if(ret==0){
	//if((fp.f_type == GPFS_1_MAGIC)||(fp.f_type == GPFS_2_MAGIC))
	//{
		if(DEBUG) cout<<"<::RemoveAclLibrary>: GPFS File System Identified!"<<endl;
		GPFSWrapper gpfs;
		return(	gpfs.removeAcl(tempDir, file, user));
	}

	else
#endif

	
#ifdef LUSTRE
	if(fp.f_type == LUSTRE_MAGIC)
	{
		if(DEBUG) cout<<"<::RemoveACLLibrary>: LUSTRE File System identified!"<<endl;
		LustreWrapper lustre;
		return(lustre.removeAcl(tempDir, file, user));
	}
#endif

	//if(fp.f_type == 61267)
	if( (fp.f_type == EXT3_SUPER_MAGIC)||(fp.f_type == EXT2_SUPER_MAGIC))
	{
		if(DEBUG) cout<<"<::RemoveAclLibrary>: EXT3, EXT2... File System identified!"<<endl;
		PosixWrapper fs;
		return(fs.removeAcl(tempDir, file, user));
	}
	


	
};




	                                  
