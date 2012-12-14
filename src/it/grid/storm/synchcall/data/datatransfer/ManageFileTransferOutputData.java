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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.synchcall.data.OutputData;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 * This class represents the PutDone Output Data associated with the SRM request PutDone
 *
 * Authors:
 *     @author=lucamag luca.magnoniATcnaf.infn.it
 *     @author  Alberto Forti
 *
 * @date = Oct 10, 2008
 *
 */

public class ManageFileTransferOutputData implements OutputData
{
    
    private static final Logger log = LoggerFactory.getLogger(ManageFileTransferOutputData.class);
    private final TReturnStatus returnStatus;
    private final ArrayOfTSURLReturnStatus arrayOfFileStatus;

    public ManageFileTransferOutputData(TReturnStatus retStatus) throws IllegalArgumentException
    {

        if (retStatus == null)
        {
            log.error("Unable to create the object, invalid arguments: retStatus=" + retStatus);
            throw new IllegalArgumentException("Unable to create the object, invalid arguments");
        }

        this.returnStatus = retStatus;
        this.arrayOfFileStatus = new ArrayOfTSURLReturnStatus();
    }

    public ManageFileTransferOutputData(TReturnStatus retStatus, ArrayOfTSURLReturnStatus arrayOfFileStatus)
        throws IllegalArgumentException
    {
        if (retStatus == null || arrayOfFileStatus == null || arrayOfFileStatus.getArray().isEmpty())
        {
            log.error("Unable to create the object, invalid arguments: retStatus=" + retStatus
                    + " arrayOfFileStatus=" + arrayOfFileStatus);
            throw new IllegalArgumentException("Unable to create the object, invalid arguments");
        }
        this.returnStatus = retStatus;
        this.arrayOfFileStatus = arrayOfFileStatus;
    }

    /**
     * Returns the returnStatus field
     * @return TReturnStatus
     */
    public TReturnStatus getReturnStatus()
    {
        return returnStatus;
    }

    /**
     * Returns the arrayOfFileStatuses field
     * @return TSURLReturnStatus
     */
    public ArrayOfTSURLReturnStatus getArrayOfFileStatuses()
    {
        return arrayOfFileStatus;
    }

    public void addFileStatus(TSURLReturnStatus surlStatus)
    {
        arrayOfFileStatus.addTSurlReturnStatus(surlStatus);
    }

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.OutputData#isSuccess()
     */
    public boolean isSuccess() {
        return returnStatus.isSRM_SUCCESS();
    }
}
