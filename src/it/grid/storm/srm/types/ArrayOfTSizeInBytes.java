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
 * This class represents the ArrayOfTSizeInBytes SRM type.
 *
 * @author  Alberto Forti
 * @author  CNAF - INFN  Bologna
 * @date    Luglio, 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import it.grid.storm.common.types.SizeUnit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ArrayOfTSizeInBytes implements Serializable {

	private static final long serialVersionUID = -1987674620390240434L;

	public static final String PNAME_arrayOfExpectedFileSizes = "arrayOfExpectedFileSizes";
    
    private ArrayList  sizeInBytesList;
    
    public ArrayOfTSizeInBytes() {
        sizeInBytesList = new ArrayList();
    }
    
    public static ArrayOfTSizeInBytes decode(Map inputParam, String fieldName) {
        List inputList = null;
        try {
        	 inputList = Arrays.asList((Object[]) inputParam.get(fieldName));
        } catch (NullPointerException e) {
        	//log.warn("Empty SURL array found!");
        }
       
        if (inputList == null) return null;
        
        ArrayOfTSizeInBytes list = new ArrayOfTSizeInBytes();
        for (int i=0; i<inputList.size(); i++) {
            TSizeInBytes size = null;
            String strLong = (String) inputList.get(i);
            try {
                size = TSizeInBytes.make(Long.parseLong(strLong), SizeUnit.BYTES);
            }
            catch (InvalidTSizeAttributesException e) {
                return null;
            }
            list.addTSizeInBytes(size);
        }
        return list;
    }

    public Object[] getArray()
    {
      return sizeInBytesList.toArray();
    }
    
    public TSizeInBytes getTSizeInBytes(int i) {
        return (TSizeInBytes) sizeInBytesList.get(i);
    }
    
    public void setTSizeInBytes(int index, TSizeInBytes size) {
        sizeInBytesList.set(index, size);
    }
    
    public void addTSizeInBytes(TSizeInBytes size) {
        sizeInBytesList.add(size);
    }
    
    public int size() {
        return sizeInBytesList.size();
    }
}
