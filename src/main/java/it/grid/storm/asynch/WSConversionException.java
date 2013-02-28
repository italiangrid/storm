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

/**
 * Class that represens an exception thrown when the conversion between WS types
 * and StoRM Object Model types, fails for some reason.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    October, 2005
 */
public class WSConversionException extends Exception {

    private String explanation=""; //String containing the reason for the exception

    /**
     * Constructor that requires a String describing the reason for the exception.
     */
    public WSConversionException(String explanation) {
        if (explanation!=null) this.explanation=explanation;
    }

    public String toString() {
        return explanation;
    }
}
