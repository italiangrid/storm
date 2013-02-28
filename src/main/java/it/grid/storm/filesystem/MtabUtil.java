/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.grid.storm.filesystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 */
public class MtabUtil
{

    private static final Logger log = LoggerFactory.getLogger(MtabUtil.class);
    
    private static final String MTAB_FILE_PATH = "/etc/mtab";

    private static final int MTAB_DEVICE_INDEX = 0;
    
    private static final int MTAB_MOUNT_POINT_INDEX = 1;
    
    private static final int MTAB_FS_NAME_INDEX = 2;
    
    private static final int MTAB_MOUNT_OPTIONS_INDEX = 3;
    
    private static final int MTAB_DUMP_INDEX = 4;
    
    private static final int MTAB_FSC_ORDER_POSITION_INDEX = 5;

    public static String getFilePath()
    {
        return MTAB_FILE_PATH;
    }

    /**
     * @return the mtabDeviceIndex
     */
    public static final int getMtabDeviceIndex()
    {
        return MTAB_DEVICE_INDEX;
    }
    
    public static int getMountPointIndex()
    {
        return MTAB_MOUNT_POINT_INDEX;
    }
    
    public static int getFsNameIndex()
    {
        return MTAB_FS_NAME_INDEX;
    }

    /**
     * @return the mtabMountOptionsIndex
     */
    public static final int getMtabMountOptionsIndex()
    {
        return MTAB_MOUNT_OPTIONS_INDEX;
    }

    /**
     * @return the mtabDumpIndex
     */
    public static final int getMtabDumpIndex()
    {
        return MTAB_DUMP_INDEX;
    }

    /**
     * @return the mtabFscOrderPositionIndex
     */
    public static final int getMtabFscOrderPositionIndex()
    {
        return MTAB_FSC_ORDER_POSITION_INDEX;
    }

    protected static boolean skipLineForMountPoints(String line)
    {
        if (line.startsWith("#") || !line.startsWith("/dev/"))
        {
            return true;
        }
        return false;
    }


    public static Map<String, String> getFSMountPoints() throws Exception
    {
        HashMap<String, String> mountPointToFSMap = new HashMap<String, String>();
        BufferedReader mtab = null;
        try
        {
            try
            {
                mtab = new BufferedReader(new FileReader(getFilePath()));
            }
            catch (FileNotFoundException e)
            {
                log.error("Unable to find mtab file at " +getFilePath() +  " . FileNotFoundException: " + e.getMessage());
                throw new Exception("Unable to get mount points. mtab file not found");
            }
            String line;
            try
            {
                while ((line = mtab.readLine()) != null)
                {
                    if (skipLineForMountPoints(line))
                    {
                        continue;
                    }
                    LinkedList<String> elementsList = tokenizeLine(line);
                    if ((elementsList.size() - 1) < getMountPointIndex() || (elementsList.size() - 1) < getFsNameIndex())
                    {
                        log.warn("Unable to produce a valid file system mount point from line \'" + line
                                + "\' . not enough elements in the tokenized array : " + elementsList.toString() + ". Skipping the line");
                    }
                    else
                    {
                        mountPointToFSMap.put(elementsList.get(getMountPointIndex()), elementsList.get(getFsNameIndex()));
                    }
                }
            }
            catch (IOException e)
            {
                log.error("Unable to read from mtab file at " + getFilePath() +  " . IOException: " + e.getMessage());
                throw new Exception("Unable to get mount points. Erro reading from mtab");
            }
        }
        finally
        {
            if (mtab != null)
            {
                try
                {
                    mtab.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return mountPointToFSMap;
    }

    public static List<MtabRow> getRows() throws IOException 
    {
        List<MtabRow> rows = new ArrayList<MtabRow>();
        BufferedReader mtab = new BufferedReader(new FileReader(getFilePath()));
        String line;
        while ((line = mtab.readLine()) != null)
        {
            if (skipLineForMountPoints(line))
            {
                continue;
            }
            log.debug("Creting an mtab row from string \'" + line + "\'");
            MtabRow row = null;
            try
            {
                row = produceRow(line);
            }catch (IllegalArgumentException e)
            {
                log.warn("Unable to produce a valid row from line \'" + line
                        + "\' . IllegalArgumentException : " + e.getMessage() + ". Skipping the line");
            }
            if(row != null)
            {
                rows.add(row);    
            }
        }
        log.debug("Produced " + rows.size() + " mtab rows from file at " + MTAB_FILE_PATH);
        return rows;
    }
    
    private static MtabRow produceRow(String line) throws IllegalArgumentException
    {
        LinkedList<String> elementsList = tokenizeLine(line);
        return new MtabRow(elementsList);
    }

    public static LinkedList<String> tokenizeLine(String line)
    {
        String[] elementsArray = line.split(" ");
        LinkedList<String> elementsList = new LinkedList<String>(Arrays.asList(elementsArray));
        while (elementsList.remove(""))
        {
        }
        return elementsList;
    }
}
