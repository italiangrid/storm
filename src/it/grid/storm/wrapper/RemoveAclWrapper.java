package it.grid.storm.wrapper;

import org.apache.log4j.Logger;

import java.io.*;


/**
 * Class for Remove ACL native Library.
 * This class provide method to remove ACL using native library provided by StoRM.
 * Return:
 * 1 if success
 * -1 if FIle does not exist.
 * -2 if Error in exec acl command.
 */
public class RemoveAclWrapper
{
	/**
	 * Logger.
	 * This Logger it's used to log information.
	 */ 	
	private static final Logger log = Logger.getLogger("wrapper");
	
native int removeAcl(String tempDir, String path, String user);
static  {
	//	System.out.println("File: "+pathToFile+", size = "+ size );
	try {
		System.loadLibrary("removeaclnativelib");
	} catch (UnsatisfiedLinkError e) {
      		log.fatal("RemoveACL native library failed to load!\n" + e);
		System.exit(1);
	}
}


}
