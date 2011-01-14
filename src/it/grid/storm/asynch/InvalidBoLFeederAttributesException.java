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

package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * Class that represents an Exception thrown when a BoLFeeder could not be created because the
 * supplied RequestSummayData or GridUser or GlobalStatusManager were null.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug, 2009
 */
public class InvalidBoLFeederAttributesException extends Exception {

    private static final long serialVersionUID = -5043656524831277137L;

    private boolean nullRequestSummaryData = false;
    private boolean nullGridUser = false;
    private boolean nullGlobalStatusManager = false;

    /**
     * Public constructor that requires the RequestSummaryData and the GridUser that caused the
     * exception to be thrown.
     */
    public InvalidBoLFeederAttributesException(RequestSummaryData rsd, GridUserInterface gu,
            GlobalStatusManager gsm) {
        
        nullRequestSummaryData = (rsd == null);
        nullGridUser = (gu == null);
        nullGlobalStatusManager = (gsm == null);
    }

    public String toString() {
        return "null-RequestSummaryData=" + nullRequestSummaryData + "; null-GridUser=" + nullGridUser
                + "; null-GlobalStatusManagerr=" + nullGlobalStatusManager;
    }
}
