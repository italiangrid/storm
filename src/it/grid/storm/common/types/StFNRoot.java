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
 * This class represent a Storage File Name Root.
 * A virtual directory path assigned to a single Virtual Oraganization, so each SURL of this VO must start with correct StFNRoot.
 */
public class StFNRoot {
	private String stfnroot;

	public StFNRoot(String stfnroot)throws InvalidStFNRootAttributeException {
        	if ((stfnroot==null) || (stfnroot.equals("")) || (stfnroot.charAt(0)!='/')) throw new InvalidStFNRootAttributeException(stfnroot);
       		 this.stfnroot=stfnroot.replaceAll(" ","");
 	}

	public String getValue() {
		return stfnroot;
	}
		
	public String toString() {
		return stfnroot;
	}
        public boolean equals(Object o) {
       	 if (o==this) return true;
       	 if (!(o instanceof StFNRoot)) return false;
       	 StFNRoot po = (StFNRoot) o;
       	 return stfnroot.equals(po.stfnroot);
   	 }
}
