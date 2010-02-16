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

        // void map_user(const char *user_dn, const char **fqan_list, int nfqan, int *uid, int **gids, int *ngids)
        void map_user(String user_dn, String[] fqan_list, int nfqan, IntByReference uid,
                PointerByReference gids, IntByReference ngids);
   
        void prova(PointerByReference uid);

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
        
//        test();
        PointerByReference uid = new PointerByReference();
        StormLcmapsLibrary.INSTANCE.prova(uid);

        System.out.println("OK");

        Pointer p = uid.getValue();
        int[] gidsArray = p.getIntArray(0, 10);

        for (int gid:gidsArray) {
            System.out.println("  gid[" + gid + "]: " + gid);
        }


    }

}
