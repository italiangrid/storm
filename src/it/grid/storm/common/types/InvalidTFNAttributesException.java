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

package it.grid.storm.common.types;

/**
 * This class represents an exception thrown when the TFN constructor is invoked
 * with null Machine, Port or PathName, or if any is empty.
 *
 * @author  EGRID - ICTP Trieste
 * @date    March 26th, 2005
 * @version 2.0
 */
public class InvalidTFNAttributesException extends Exception {

    private boolean nullMachine; //boolean true if Machine is null
    private boolean nullPort; //boolean true if Port is null
    private boolean nullPFN; //boolean true if PathName is null
    private boolean emptyMachine=false; //boolean true if Machine is empty
    private boolean emptyPort=false; //boolean true if Port is empty
    private boolean emptyPFN=false; //boolean true if PFN is empty

    /**
     * Constructor that requires the Machine m, the Port p and the PathName pn that
     * caused the Exception to be thrown.
     */
    public InvalidTFNAttributesException(Machine m, Port p, PFN pfn) {
        nullMachine = (m==null);
        nullPort = (p==null);
        nullPFN = (pfn==null);
        if (!nullMachine) emptyMachine = m.isEmpty();
        if (!nullPort) emptyPort = p.isEmpty();
        if (!nullPFN) emptyPFN = pfn.isEmpty();
    }

    public String toString() {
        return "nullMachine="+nullMachine+"; emptyMachine="+emptyMachine+"; nullPort="+nullPort+"; emptyPort="+emptyPort+"; nullPFN="+nullPFN+"; emptyPFN="+emptyPFN+".";
    }
}
