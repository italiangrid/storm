package it.grid.storm.jna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Native;

public class CUtil {
    
    private static final Logger log = LoggerFactory.getLogger(CUtil.class);
    
    public static long getFileBlocksSize(String fileName) {
        return CUtilJNA.LibcLibrary.INSTANCE.stat_get_blocks_size(fileName);
    }
    
    public static int setFileGroup(String fileName, String groupName) {
        int ret = CUtilJNA.LibcLibrary.INSTANCE.set_file_group(fileName, groupName);
        
        if (ret != 0) {
            int errno = Native.getLastError();
            log.error("Error setting group name. errno=" + errno);
        }
        
        return ret;
    }
}
