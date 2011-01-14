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
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.InputData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbortGeneralInputData extends AbstractInputData
{
    private static final Logger log = LoggerFactory.getLogger(AbortGeneralInputData.class);

    private GridUserInterface auth = null;
    private TRequestToken reqToken = null;
    private ArrayOfSURLs arrayOfSURLs = null;


    private int type;

    public static final int ABORT_REQUEST = 0;
    public static final int ABORT_FILES = 1;

    public AbortGeneralInputData() {}

    private AbortGeneralInputData(GridUserInterface auth, TRequestToken reqToken, ArrayOfSURLs surlArray, int type)
    //throws InvalidAbortFilesInputDataAttributeException
    {
        boolean ok = true; //= (!(surlArray == null));
        if (!ok) {
            ;//throw new InvalidAbortFilesInputDataAttributeException(surlArray);
        }

        this.auth = auth;
        this.reqToken = reqToken;
        this.arrayOfSURLs = surlArray;
        this.type = type;
    }

    public static AbortGeneralInputData make(AbortRequestInputData requestInputData) {
        //Create an AbortFiles data from an AbortRequest data
        //In this case the SURLArray MUST BE null.
        log.debug("abortRequest: Creating general input data from abortRequest inputdata.");
        if(requestInputData == null) {
            return null;
        }
        return new AbortGeneralInputData(requestInputData.getUser(), requestInputData.getRequestToken(),
                null, AbortGeneralInputData.ABORT_REQUEST);
        //this.auth = requestInputData.getUser();
        //this.reqToken = requestInputData.getRequestToken();
        //set type
        //this.type = AbortGeneralInputData.ABORT_REQUEST;
    }

    public static AbortGeneralInputData make(AbortFilesInputData requestInputData) {
        //Create an AbortGeneral data from an AbortFiles data
        //In this case the SURLArray MUST NOT BE null.
        log.debug("abortRequest: Creating general input data from abortFiles inputdata.");
        if (requestInputData == null) {
            return null;
        } else {
            return new AbortGeneralInputData(requestInputData.getUser(), requestInputData.getRequestToken(),
                    requestInputData.getArrayOfSURLs(), AbortGeneralInputData.ABORT_FILES);
            //this.auth = requestInputData.getUser();
            //this.reqToken = requestInputData.getRequestToken();
            //this.arrayOfSURLs = requestInputData.getArrayOfSURLs();
        }

        //Set Type
        //this.type = AbortGeneralInputData.ABORT_FILES;
    }

    public TRequestToken getRequestToken()
    {
        return reqToken;
    }

    public void setRequestToken(TRequestToken reqToken)
    {
        this.reqToken = reqToken;
    }

    public GridUserInterface getUser()
    {
        return this.auth;
    }

    public void setUser(GridUserInterface user)
    {
        this.auth = user;
    }

    public ArrayOfSURLs getArrayOfSURLs()
    {
        return arrayOfSURLs;
    }

    public void setArrayOfSURLs(ArrayOfSURLs arrayOfSURLs)
    {
        this.arrayOfSURLs = arrayOfSURLs;
    }

    public int getType() {
        return type;
    }

}
