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

import java.util.Map;
import java.util.HashMap;

/**
 * This class represents the possible site protocols of StoRM.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 26th, 2005
 * @version 1.0
 */
public class SiteProtocol {
	
	private String protocol = null;
    private static Map<String, SiteProtocol> m = new HashMap<String, SiteProtocol>();

	public static final SiteProtocol SRM = new SiteProtocol("srm") {
        public int hashCode() {
            return 1;
        }
    };

	public static final SiteProtocol EMPTY = new SiteProtocol("empty") {
        public int hashCode() {
            return 0;
        }
    };
	
   	private SiteProtocol(String protocol) {
		this.protocol = protocol;
        m.put(protocol,this);
	}


    /**
     * Facility method to obtain a SiteProtocol object from its String representation.
     * An IllegalArgumentExceptin is thrown if the supplied String does not have a
     * SiteProtocol counterpart. The supplied String may contain white spaces and be in
     * a mixture of upper and lower case characters.
     */
    public static SiteProtocol fromString(String value) throws IllegalArgumentException {
      value = value.toLowerCase().replaceAll(" ","");
      SiteProtocol aux = (SiteProtocol) m.get(value);
      if (aux==null) throw new IllegalArgumentException();
      return aux;
    }

    public String toString() {
        return protocol;
    }


    //Maybe should be removed!
	 public String getValue() {
		 return protocol;
	 }

}
