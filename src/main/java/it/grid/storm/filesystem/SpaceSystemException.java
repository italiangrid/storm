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

package it.grid.storm.filesystem;

/**
 * Class that represents an Exception thrown whenever a SpaceSystem cannot
 * be instantiated.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    June 2006
 */
public class SpaceSystemException extends Exception {
    private String explanation="";
    
    /**
     * Constructor that requires a non-null String describing the 
     * problem encountered. If a null is supplied, then an empty String
     * is used instead.
     */
    public SpaceSystemException(String explanation) {
        if (explanation!=null) this.explanation=explanation;
    }
    
    public String toString() {
        return explanation;
    }
} 
