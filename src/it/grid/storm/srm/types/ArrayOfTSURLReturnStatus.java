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
 * This class represents a TExtraInfoArray
 *
 * @author  EGRID ICTP Trieste / CNAF Bologna
 * @date    March 23rd, 2005
 * @version 2.0
 */

package it.grid.storm.srm.types;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ArrayOfTSURLReturnStatus  {

    public static String PNAME_ARRAYOFFILESTATUSES = "arrayOfFileStatuses";
    
    ArrayList  surlRetList;

    /**
     * Construct an ArrayOfTSURLReturnStatus of numItems empty elements.
     */
    public ArrayOfTSURLReturnStatus(int numItems)
    {
        surlRetList = new ArrayList(numItems);
    }
    
    /**
     * Constructor that requires a String. If it is null, then an
     * InvalidArrayOfTExtraInfoAttributeException is thrown.
     */
    public ArrayOfTSURLReturnStatus(TSURLReturnStatus[] surlArray) throws InvalidArrayOfTSURLReturnStatusAttributeException {

        if (surlArray == null) throw new InvalidArrayOfTSURLReturnStatusAttributeException(surlArray);
        //FIXME this.tokenArray = tokenArray;
    } 
	
    public ArrayOfTSURLReturnStatus() {
	    surlRetList = new ArrayList();
    }
   
    
    public ArrayList getArray()
    {
      return surlRetList;
    }

    public TSURLReturnStatus getTSURLReturnStatus(int i) {
        return (TSURLReturnStatus) surlRetList.get(i);
	}

    public void setTSURLReturnStatus(int index, TSURLReturnStatus surl) {
	    surlRetList.set(index, surl);
    }

    public void addTSurlReturnStatus(TSURLReturnStatus surl) {
	    surlRetList.add(surl);
    }

    public int size(){
        return surlRetList.size();
    }

    public void encode(Map outputParam, String name){ 
        List list = new ArrayList();
        for(int i=0;i<surlRetList.size();i++) {
           ((TSURLReturnStatus)surlRetList.get(i)).encode(list);
        }
        
        outputParam.put(name, list);
    }
}
