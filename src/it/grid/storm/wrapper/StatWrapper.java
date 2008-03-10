package it.grid.storm.wrapper;

import org.apache.log4j.Logger;
import java.io.*;


/**
 * This class represent wrapper for native get acl libraryy.
 * Long containing size if success
 * null
 *
 */
public class StatWrapper {
	/**
	 * Logger.
	 * This Logger it's used to log information.
	 */ 	
	private static final Logger log = Logger.getLogger("wrapper");
	
native long statfs(String rootDir);
static  {	
		//DEBUG	
		//log.debug("<StatWrapper>## Load Library ##");
		String libraryPath = System.getProperty("java.library.path");
		log.debug("<StatWrapper> JAVA.LIBRARY.PATH = "+libraryPath);

	try {
		System.loadLibrary("statnativelib");
	} catch (UnsatisfiedLinkError e) {
      		log.fatal("Get ACL native library failed to load!\n" + e);
		System.exit(1);
	}
}

native long stat(String file);

static 	{
	try {
		System.loadLibrary("statnativelib");
	} catch (UnsatisfiedLinkError e) {
      		log.fatal("Get ACL native library failed to load!\n" + e);
		System.exit(1);
	}	
}


}
