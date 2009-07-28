package it.grid.storm.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class CUtilJNA {

    public interface LibcLibrary extends Library {

        LibcLibrary INSTANCE = (LibcLibrary) Native.loadLibrary(("storm_cutil"), LibcLibrary.class);

        long stat_get_blocks_size(String fileName);
        
        int set_file_group(String fileName, String groupName);
        
    }

//    public static void main(String[] args) {
//
//        if (args.length != 1) {
//            System.out.println("usage: <fileName>");
//            System.exit(1);
//        }
//        
//        String fileName = args[0];
//        
//    }
}
