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

package it.grid.storm.wrapper;


/**
 * This class represents an Exception throws if SrmLS request it's specified into a regular file , non a valid directory.
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */

import it.grid.storm.common.types.PFN;

public class InvalidLSRequestException extends Exception {

    private PFN pfn;
    
    public InvalidLSRequestException(PFN pfn) {
        this.pfn = pfn;
    }

    public String toString() {
        return "Error Directory = "+pfn;
    }
}
