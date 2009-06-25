package it.grid.storm.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for Truncate functionality provided by  native Library.
 * This class whill provide  truncate functionality usgin native library of StoRM.
 * The reserveSpace functionality return:
 * 1 if correct
 * -1 if Path specified does not exist
 * -2 if Space File Specified can't be created
 * -3 if Space reservation functionality (gpfs only) does not work.
 * 
 */
public class TruncateWrapper
{
    /**
     * Logger.
     * This Logger it's used to log information.
     */
    private static final Logger log = LoggerFactory.getLogger(TruncateWrapper.class);

    native int truncateFile(String pathToFile, long newsize);
    static  {
        //	System.out.println("File: "+pathToFile+", size = "+ size );
        System.loadLibrary("truncatenativelib");
    }


}
