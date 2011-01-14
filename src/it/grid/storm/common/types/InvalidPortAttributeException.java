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
 * This class represents an exception thrown if a Port is attempted to be built with
 * an int <0 or >65535.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 25th, 2005
 * @version 1.0
 */
public class InvalidPortAttributeException extends Exception {

    private int port;

    /**
     * Constructor requiring the port that caused the exception.
     */
    public InvalidPortAttributeException(int port) {
        this.port = port;
    }

    public String toString() {
        return "Port exceeded limits; supplied port was: "+port;
    }
}
