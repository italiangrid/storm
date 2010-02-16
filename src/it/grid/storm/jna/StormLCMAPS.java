package it.grid.storm.jna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class StormLCMAPS {

    /* Functions implemented by libstorm_lcmaps.so */
    private interface StormLcmapsLibrary extends Library {

        StormLcmapsLibrary INSTANCE = (StormLcmapsLibrary) Native.loadLibrary(("storm_lcmaps"),
                                                                              StormLcmapsLibrary.class);


        int init_lcmaps();
        // void map_user(const char *user_dn, const char **fqan_list, int nfqan, int *uid, int **gids, int *ngids)
        int map_user(String user_dn, String[] fqan_list, int nfqan, IntByReference uid,
                PointerByReference gids, IntByReference ngids);
        void free_gids(PointerByReference gids);
    }

    private static final Logger log = LoggerFactory.getLogger(StormLCMAPS.class);

    public static void test() {
        PointerByReference gids = new PointerByReference();
        IntByReference uid = new IntByReference();
        IntByReference ngids = new IntByReference();

        String[] fqanArray = new String[2];
        fqanArray[0] = "fqan0";
        fqanArray[1] = "fqan1";
        StormLcmapsLibrary.INSTANCE.map_user("Alberto", fqanArray, fqanArray.length, uid, gids, ngids);

        Pointer p = gids.getValue();
        int[] gidsArray = p.getIntArray(0, ngids.getValue());
        
        System.out.println("uid  : " + uid.getValue());
        System.out.println("ngids: " + gidsArray.length);
        for (int gid:gidsArray) {
            System.out.println("  gid[" + gid + "]: " + gid);
        }
        
        System.out.println();
        System.out.println("Game over");

    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("usage: <dn> [[fqan]... [fqan]");
            System.exit(1);
        }
        
        String dn = args[0];
        String[] fqanArray = new String[args.length - 1];
        
        for (int i = 1; i< args.length; i++) {
            fqanArray[i-1] = args[i];
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

        for (int gid:gidsArray) {
            System.out.println("  gid[" + gid + "]: " + gid);
        }        

        StormLcmapsLibrary.INSTANCE.free_gids(gids);

    }
}

