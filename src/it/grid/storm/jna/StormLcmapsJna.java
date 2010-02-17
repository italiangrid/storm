package it.grid.storm.jna;

import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class StormLcmapsJna {

    /* Functions implemented by libstorm_lcmaps.so */
    private interface StormLcmapsLibrary extends Library {

        StormLcmapsLibrary INSTANCE = (StormLcmapsLibrary) Native.loadLibrary(("storm_lcmaps"),
                                                                              StormLcmapsLibrary.class);

        void free_gids(PointerByReference gids);

        int init_lcmaps();

        int map_user(String user_dn, String[] fqan_list, int nfqan, IntByReference uid,
                PointerByReference gids, IntByReference ngids);
    }
    
    private static final Logger log = LoggerFactory.getLogger(StormLcmapsJna.class);
    /** To synchronize on LCMAPS invocation. */
    private static Object lock = new Object();
    
    static {
        if (StormLcmapsLibrary.INSTANCE.init_lcmaps() != 0) {
            String lcmapsLogFile = System.getenv("LCMAPS_LOG_FILE");
            
            if (lcmapsLogFile == null) {
                lcmapsLogFile = "";
            }
            
            log.error("Error while initializing LCMAPS, see LCMAPS logfile: " + lcmapsLogFile);
        }
    }
    
    /*
     * To be used for test purposes. In order to correctly initialize the lcmaps library be sure the following
     * environment variable are set: LCMAPS_LOG_FILE (path to the log file) and LCMAPS_DB_FILE (path to the lcmaps db
     * file).
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("usage: <dn> [[fqan]... [fqan]");
            System.exit(1);
        }

        String dn = args[0];
        String[] fqanArray = new String[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            fqanArray[i - 1] = args[i];
            System.out.println("fqan: " + fqanArray[i]);
        }

        IntByReference uid = new IntByReference();
        IntByReference ngids = new IntByReference();
        PointerByReference gids = new PointerByReference();

        int rc = StormLcmapsLibrary.INSTANCE.init_lcmaps();
        System.out.println("Return code for initialization: " + rc);
        if (rc != 0) {
            System.exit(2);
        }

        rc = StormLcmapsLibrary.INSTANCE.map_user(dn, fqanArray, fqanArray.length, uid, gids, ngids);

        System.out.println("Return code map_user: " + rc);
        if (rc != 0) {
            System.exit(2);
        }

        System.out.println("uid: " + uid.getValue());
        System.out.println("Number of gids: " + ngids.getValue());

        Pointer p = gids.getValue();
        int[] gidsArray = p.getIntArray(0, ngids.getValue());

        for (int gid : gidsArray) {
            System.out.println("  gid[" + gid + "]: " + gid);
        }

        StormLcmapsLibrary.INSTANCE.free_gids(gids);

    }

    public LocalUser map(final String dn, final String[] fqans) throws CannotMapUserException {
        
        IntByReference uid = new IntByReference();
        IntByReference ngids = new IntByReference();
        PointerByReference gids = new PointerByReference();

        int rc;
        synchronized (lock) {
            rc = StormLcmapsLibrary.INSTANCE.map_user(dn, fqans, fqans.length, uid, gids, ngids);
        }
        
        if (rc != 0) {
            throw new CannotMapUserException("LCMAPS error, cannot map user credentials to local user.");
        }
        
        Pointer p = gids.getValue();
        LocalUser localUser = new LocalUser(uid.getValue(), p.getIntArray(0, ngids.getValue()), ngids.getValue());
        
        StormLcmapsLibrary.INSTANCE.free_gids(gids);
        
        return localUser;
    }
}
