package it.grid.storm.jna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class CUtil {
    
    /* Functions implemented by libstorm_cutil.so */
    private interface LibcLibrary extends Library {

        LibcLibrary INSTANCE = (LibcLibrary) Native.loadLibrary(("storm_cutil"), LibcLibrary.class);

        long stat_get_blocks_size(String fileName);
        int set_file_group(String fileName, String groupName);
        
    }
    
    private static final Logger log = LoggerFactory.getLogger(CUtil.class);
    
    public static long getFileBlocksSize(String fileName) {
        return LibcLibrary.INSTANCE.stat_get_blocks_size(fileName);
    }
    
    public static int setFileGroup(String fileName, String groupName) {
        int ret = LibcLibrary.INSTANCE.set_file_group(fileName, groupName);
        
        if (ret != 0) {
            int errno = Native.getLastError();
            log.error("Error setting group name. errno=" + errno);
        }
        
        return ret;
    }
}
