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

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.InvalidTLifeTimeAttributeException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;

/**
 * @author Michele Dibenedetto
 *
 */
public class PrepareToPutInputData extends FileTransferInputData
{

    private TOverwriteMode overwriteMode;
    private TSizeInBytes fileSize;
    private TLifeTimeInSeconds desiredFileLifetime;

//    public PrepareToPutInputData(FileTransferInputData inputData)
//    {
//        this(inputData.getUser(), inputData.getSurl(), inputData.getTransferProtocols());
//    }
    
    /**
     * @param user
     * @param surl
     * @param transferProtocols
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    public PrepareToPutInputData(GridUserInterface user, TSURL surl, TURLPrefix transferProtocols) throws IllegalArgumentException, IllegalStateException
    {
        super(user, surl, transferProtocols);
        try
        {
            this.desiredFileLifetime = TLifeTimeInSeconds.make(Configuration.getInstance().getFileLifetimeDefault(), TimeUnit.SECONDS);
        } catch(InvalidTLifeTimeAttributeException e)
        {
           throw new IllegalStateException("Unexpected InvalidTLifeTimeAttributeException: " + e);
        }
    }
    
    public PrepareToPutInputData(GridUserInterface user, TSURL surl, TURLPrefix transferProtocols, TLifeTimeInSeconds desiredFileLifetime) throws IllegalArgumentException, IllegalStateException
    {
        this(user, surl, transferProtocols);
        this.desiredFileLifetime = desiredFileLifetime;
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
     * @param desiredFileLifetime
     */
    public void setDesiredFileLifetime(TLifeTimeInSeconds desiredFileLifetime)
    {
        this.desiredFileLifetime = desiredFileLifetime;
    }

    /**
     * @return
     */
    public TLifeTimeInSeconds getDesiredFileLifetime()
    {
        return desiredFileLifetime;
    }
}
