package it.grid.storm.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for Space Reservation native Library.
 * This class whill provide space reserevation functionality usgin native library of StoRM.
 * The reserveSpace functionality return:
 * 1 if correct
 * -1 if Path specified does not exist
 * -2 if Space File Specified can't be created
 * -3 if Space reservation functionality (gpfs only) does not work.
 * 
 */
public class SpaceReservationWrapper
{
    /**
     * Logger.
     * This Logger it's used to log information.
     */
    private static final Logger log = LoggerFactory.getLogger(SpaceReservationWrapper.class);


    native int reserveSpace(String pathToFile, long size);
    static  {
        //	System.out.println("File: "+pathToFile+", size = "+ size );
        try {
            System.loadLibrary("spacenativelib");
        } catch (UnsatisfiedLinkError e) {
            log.error("SpaceReservation native library failed to load!", e);
            System.exit(1);
        }

    }

}
