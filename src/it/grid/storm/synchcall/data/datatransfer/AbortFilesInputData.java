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

/**
 * This class represents the general Abort Input Data associated with the SRM request Abort
 * @author  Magnoni Luca
 * @author  CNAF -INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.exception.InvalidAbortFilesInputDataAttributeException;

public class AbortFilesInputData extends AbortGeneralInputData
{
    private final ArrayOfSURLs arrayOfSURLs;


    public AbortFilesInputData(GridUserInterface auth, TRequestToken reqToken, ArrayOfSURLs surlArray)
                    throws InvalidAbortFilesInputDataAttributeException
    {
        super(auth, reqToken, AbortType.ABORT_REQUEST);
        if (surlArray == null)
        {
            throw new InvalidAbortFilesInputDataAttributeException(surlArray);
        }
        this.arrayOfSURLs = surlArray;
    }
    
    public ArrayOfSURLs getArrayOfSURLs()
    {
        return arrayOfSURLs;
    }

}
