package it.grid.storm.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    private static final Logger log = LoggerFactory.getLogger(StatWrapper.class);

    native long statfs(String rootDir);
    static  {
        //DEBUG
        //log.debug("<StatWrapper>## Load Library ##");
        String libraryPath = System.getProperty("java.library.path");
        log.debug("<StatWrapper> JAVA.LIBRARY.PATH = "+libraryPath);

        try {
            System.loadLibrary("statnativelib");
        } catch (UnsatisfiedLinkError e) {
            log.error("Get ACL native library failed to load!", e);
            System.exit(1);
        }
    }

    native long stat(String file);

    static 	{
        try {
            System.loadLibrary("statnativelib");
        } catch (UnsatisfiedLinkError e) {
            log.error("Get ACL native library failed to load!", e);
            System.exit(1);
        }
    }


}
