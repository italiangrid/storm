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

package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when a TSURL constructor is invoked with
 * null or empty SiteProtocol or SFN.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 26th, 2005
 * @version 1.0
 */

import it.grid.storm.common.types.SiteProtocol;
import it.grid.storm.common.types.SFN;

public class InvalidTSURLAttributesException extends Exception {

    private boolean nullProtocol; //boolean true if Protocol is null
    private boolean nullSFN; //boolean true if SFN is null
    private boolean emptyProtocol = false; //boolean true if the supplied SiteProtocol is empty
    private boolean emptySFN = false; //boolean true if the supplied SFN is empty
//    private boolean relativePath = false;
    /**
     * Constructor that requires the Protocol and SFN that caused the exception
     * to be thrown.
     */
    public InvalidTSURLAttributesException(SiteProtocol prt, SFN sfn) {
        nullProtocol=(prt==null); if (!nullProtocol) emptyProtocol = (prt==SiteProtocol.EMPTY);
        nullSFN=(sfn==null); if (!nullSFN) emptySFN = sfn.isEmpty();
    }

    public String toString() {
        return "Invalid TSURL Attibutes: nullProtocol="+nullProtocol +"; emptyProtocol="+emptyProtocol +"; nullSFN="+nullSFN +"; emptySFN="+emptySFN+".";
    }
}
