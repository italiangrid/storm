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
import com.sun.jna.NativeLibrary;

/**
 * @author Michele Dibenedetto
 *
 */
public class CUtilManager
{
    
    private static final Logger log = LoggerFactory.getLogger(CUtilManager.class);
    
    private static Boolean gpfsFileSystem = null;
    
    /**
     * Initialize the Manager. Tries to load CUtil .so library and stores the result in gpfsFileSystem field 
     */
    private synchronized static void init()
    {
        if(gpfsFileSystem == null)
        {
            boolean loaded = false;
            try
            {
                NativeLibrary.getInstance(CUtil.JNA_LIBRARY_NAME);
                loaded = true;
            }catch(UnsatisfiedLinkError e)
            {
                log.error("Unable to create the NativeLibrary instance for library named "
                        + CUtil.JNA_LIBRARY_NAME + " : " + e.getMessage());
            }
            gpfsFileSystem = new Boolean(loaded);
        }
    }

    /**
     * @param fileName
     * @return
     * @throws NoGPFSFileSystemException
     */
    public static long getFileBlocksSize(String fileName) throws NoGPFSFileSystemException
    { 
        init();
        if(gpfsFileSystem.booleanValue() == false)
        {
            log.error("CUtilManager : attempt to use CUtil native code library JNA " +
            		"wrapping in a non GPFS installation - getFileBlocksSize unavailable");
            throw new NoGPFSFileSystemException("Attempt to use CUtil native code library JNA " +
                    "wrapping in a non GPFS installation - getFileBlocksSize unavailable");
        }
        return CUtil.getFileBlocksSize(fileName); 
            
    }
    
    /**
     * @param fileName
     * @param groupName
     * @return
     * @throws NoGPFSFileSystemException
     */
    public static int setFileGroup(String fileName, String groupName) throws NoGPFSFileSystemException
    {
        init();
        if(gpfsFileSystem.booleanValue() == false)
        {
            log.error("CUtilManager : attempt to use CUtil native code library JNA " +
                    "wrapping in a non GPFS installation - setFileGroup unavailable");
            throw new NoGPFSFileSystemException("Attempt to use CUtil native code library JNA " +
                    "wrapping in a non GPFS installation - setFileGroup unavailable");
        }
        return CUtil.setFileGroup(fileName, groupName);
    }
    
}
