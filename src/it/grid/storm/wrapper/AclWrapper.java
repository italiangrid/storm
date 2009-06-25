
package it.grid.storm.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for Native ACL support.
 * This class provide add acl functionality using native library provided by StoRM.
 * 
 * path : Path of destination File.
 * user : local user  or group
 * acl  : acl in unix like form (user:username:r--).
 *
 * return:
 * 1 if success
 * -1  Path specified does not exist.
 * -2 Error in exec addacl command.
 * -3 Error in ACL Format Error.
 */

public class AclWrapper
{
    /**
     * Logger.
     * This Logger it's used to log information.
     */
    private static final Logger log = LoggerFactory.getLogger(AclWrapper.class);

    native int addAcl(String tempDir, String path, String user,String acl);
    static  {
        //	System.out.println("File: "+pathToFile+", size = "+ size );
        try {
            System.loadLibrary("aclnativelib");
        } catch (UnsatisfiedLinkError e) {
            log.error("ACL native library failed to load!\n", e);
            System.exit(1);
        }
    }

}
