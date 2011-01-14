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
 * This class represents an Exception thrown when the constructor for TLifeTimeInSeconds
 * is invoked with a null TimeUnit.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 23rd, 2005
 * @version 1.0
 */

import it.grid.storm.common.types.TimeUnit;

public class InvalidTLifeTimeAttributeException extends Exception {

    private boolean nullTimeUnit;

    /**
     * Constructor requiring the TimeUnit u that caused the Exception to be thrown.
     */
    public InvalidTLifeTimeAttributeException(TimeUnit u) {
        nullTimeUnit= u==null;
    }

    public String toString() {
        return "Invalid TLifeTime attributes: null-TimeUnit="+nullTimeUnit;
    }
}
