/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.jna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Library;
import com.sun.jna.Native;

class CUtil {
    
    public static final java.lang.String JNA_LIBRARY_NAME = "storm_cutil"; 
    
    /* Functions implemented by libstorm_cutil.so */
    private interface LibcLibrary extends Library {

        LibcLibrary INSTANCE = (LibcLibrary) Native.loadLibrary((JNA_LIBRARY_NAME), LibcLibrary.class);

        long gpfs_stat_get_blocks_size(String fileName);
        long stat_get_blocks(String fileName);
        int set_file_group(String fileName, String groupName);
        
    }
    
    private static final Logger log = LoggerFactory.getLogger(CUtil.class);
    
    public static long getFileBlocksSize(String fileName) {
        return LibcLibrary.INSTANCE.gpfs_stat_get_blocks_size(fileName);
    }
    
    public static long getFileBlocks(String fileName) {
        return LibcLibrary.INSTANCE.stat_get_blocks(fileName);
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
