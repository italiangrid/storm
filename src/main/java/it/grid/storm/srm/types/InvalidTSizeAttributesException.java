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
 * This class represents an Exception thrown when FileSize receives null as constructor
 * attributes; or a negative size.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 23rd, 2005
 * @version 1.0
 */
import it.grid.storm.common.types.SizeUnit;

public class InvalidTSizeAttributesException extends Exception {

    private boolean negativeSize;
    private boolean nullUnit;

    /**
     * Constructor that requires the long and the SizeUnit that caused the
     * exception to be thrown.
     */
    public InvalidTSizeAttributesException(long size, SizeUnit unit) {
        nullUnit = unit==null;
        negativeSize = size<0;
    }

    public String toString() {
        return "Ivalid TFileSize Attributes: nullSizeUnit="+nullUnit+"; negativeSize="+negativeSize;
    }
}
