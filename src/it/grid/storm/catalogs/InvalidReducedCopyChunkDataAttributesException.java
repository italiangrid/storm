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

package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TReturnStatus;

/**
 * This class represents an exception thrown when the attributes supplied to the
 * constructor of ReducedCopyChunkData are invalid, that is if any of the following is
 * _null_: fromsURL, toSURL, status.
 *
 * @author  Michele Dibenedetto
 */
@SuppressWarnings("serial")
public class InvalidReducedCopyChunkDataAttributesException extends Exception {

    //booleans that indicate whether the corresponding variable is null
    private boolean nullFromSURL;
    private boolean nullToSURL;
    private boolean nullStatus;

    /**
     * Constructor that requires the attributes that caused the exception
     * to be thrown.
     */
    public InvalidReducedCopyChunkDataAttributesException(TSURL fromSURL, TSURL toSURL, TReturnStatus status) {

        nullFromSURL = fromSURL==null;
        nullToSURL = toSURL==null;
        nullStatus = status==null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Invalid CopyChunkData attributes: null-requestToken=");
        sb.append("; null-fromSURL="); sb.append(nullFromSURL);
        sb.append("; null-toSURL="); sb.append(nullToSURL);
        sb.append("; null-status="); sb.append(nullStatus);
        sb.append(".");
        return sb.toString();
    }
}
