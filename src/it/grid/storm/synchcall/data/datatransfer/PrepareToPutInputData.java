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

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.synchcall.data.AbstractInputData;

/**
 * @author Michele Dibenedetto
 *
 */
public class PrepareToPutInputData extends AbstractInputData
{

    private final GridUserInterface user;
    private final TSURL surl;
    private TOverwriteMode overwriteMode;
    private TSizeInBytes fileSize;
    
    /**
     * Forbidden
     */
    @SuppressWarnings("unused")
    private PrepareToPutInputData() 
    {
        throw new IllegalAccessError("No arguments constructor is forbidden");
    }
    
    public PrepareToPutInputData(GridUserInterface user, TSURL surl) throws IllegalArgumentException
    {
        if(user == null || surl == null)
        {
            throw new IllegalArgumentException("Unable to create PrepareToPutInputData. Received nul parameters: user = "
                                                       + user + " , surl = " + surl);
        }
        this.surl = surl;
        this.user = user;
    }
    
    @Override
    public Boolean hasPrincipal()
    {
        return Boolean.TRUE;
    }

    @Override
    public String getPrincipal()
    {
        return user.getDn();
    }

    /**
     * @return the overwriteMode
     */
    public TOverwriteMode getOverwriteMode()
    {
        return overwriteMode;
    }

    /**
     * @param overwriteMode the overwriteMode to set
     */
    public void setOverwriteMode(TOverwriteMode overwriteMode)
    {
        this.overwriteMode = overwriteMode;
    }

    /**
     * @return the fileSize
     */
    public TSizeInBytes getFileSize()
    {
        return fileSize;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(TSizeInBytes fileSize)
    {
        this.fileSize = fileSize;
    }

    /**
     * @return the user
     */
    public GridUserInterface getUser()
    {
        return user;
    }

    /**
     * @return the surl
     */
    public TSURL getSurl()
    {
        return surl;
    }

}
