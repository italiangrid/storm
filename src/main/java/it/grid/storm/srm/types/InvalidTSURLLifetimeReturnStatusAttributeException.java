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

/**
 * This class represents an Exception throwed if TSURLLifetimeReturnStatus is not well formed. *
 * @author  Alberto Forti
 * @author  CNAF-INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import it.grid.storm.srm.types.TSURL;

public class InvalidTSURLLifetimeReturnStatusAttributeException  extends Exception
{
    private boolean nullSurl = true;
    
        public InvalidTSURLLifetimeReturnStatusAttributeException(TSURL surl) {
                nullSurl = (surl==null);
        }
        
        public String toString() {
                return "nullSurl = "+nullSurl;
        }
}
