package it.grid.storm.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represent wrapper for native get acl libraryy.
 * String containing acl if success
 * null if Path specified does not exist
 * 
 */
public class GetAclWrapper
{
    /**
     * Logger.
     * This Logger it's used to log information.
     */
    private static final Logger log = LoggerFactory.getLogger(GetAclWrapper.class);

    native String getAcl(String tempDir, String pathToFile);

    static  {
        //	System.out.println("File: "+pathToFile+", size = "+ size );
        try {
            System.loadLibrary("getaclnativelib");
        } catch (UnsatisfiedLinkError e) {
            log.error("Get ACL native library failed to load!\n", e);
            System.exit(1);
        }
    }


}
