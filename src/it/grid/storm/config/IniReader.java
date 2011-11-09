/*
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

package it.grid.storm.config;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IniReader {
    
    private static Logger log = LoggerFactory.getLogger(IniReader.class);
    private final String confPath;
    public static final String DEFAULT_CONF_PATH = Configuration.getInstance().configurationDir();
    private List<File> iniFiles = new ArrayList<File>();
    
    /**
     * 
     */
    public IniReader() throws IllegalArgumentException
    {
        this(DEFAULT_CONF_PATH);
    }


    /**
     * @param configurationPath
     */
    public IniReader(String configurationPath) throws IllegalArgumentException
    {
        if(configurationPath == null)
        {
            log.error("Null configurationPath argument provided");
            throw new IllegalArgumentException("Null configurationPath argument provided");
        }
        File confFolder = new File(configurationPath);
        if(!confFolder.isDirectory())
        {
            log.error("The provided configurationPath " + configurationPath + " is not a valid directory");
            throw new IllegalArgumentException("The provided configurationPath " + configurationPath + " is not a valid directory");
        }
        this.confPath = configurationPath;
        for(File file : confFolder.listFiles())
        {
            if (file.isFile() && (file.getName().endsWith(".ini") || file.getName().endsWith(".INI")))
            {
                iniFiles.add(file);
            }
        }
        if(iniFiles.size() == 0)
        {
            log.error("The provided configurationPath " + configurationPath + " does not contains ini files");
            throw new IllegalArgumentException("The provided configurationPath " + configurationPath + " does not contains ini files");
        }
    }
    
    /**
     * @return the confPath
     */
    public final String getConfPath()
    {
        return confPath;
    }
    
    /**
     * @param iniFileName
     * @return null if 
     */
    public Ini getIniFile(String iniFileName) throws IllegalArgumentException
    {
        if(iniFileName == null)
        {
            log.error("Null iniFileName argument provided");
            throw new IllegalArgumentException("Null iniFileName argument provided");
        }
        Ini ini = null;
        for (File iniFile : iniFiles)
        {
            if (iniFile.getName().equals(iniFileName.trim()))
            {
                try
                {
                    ini = new Ini(new FileReader(iniFile));
                }
                catch (InvalidFileFormatException e)
                {
                    log.error("Unable to parse '" + iniFile + "' InvalidFileFormatException: ", e);
                }
                catch (FileNotFoundException e)
                {
                    log.error("Unable to find '" + iniFile + "' FileNotFoundException: ", e);
                }
                catch (IOException e)
                {
                    log.error("IO Exception during load '" + iniFile + "' IOException: ", e);
                }
                break;
            }
        }
        return ini;
    }
}
