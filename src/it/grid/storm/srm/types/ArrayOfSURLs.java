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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ArrayOfSURLs implements Serializable {

    private static final long serialVersionUID = -6162739978949956886L;

    public static final String ARRAYOFSURLS = "arrayOfSURLs";

    ArrayList<TSURL> surlList;

    /**
     * Constructor that requires a String. If it is null, then an InvalidArrayOfTExtraInfoAttributeException is thrown.
     */
    public ArrayOfSURLs(TSURL[] surlArray) throws InvalidArrayOfSURLsAttributeException {

        if (surlArray == null)
            throw new InvalidArrayOfSURLsAttributeException(null);
        // FIXME this.tokenArray = tokenArray;
        // surlList = Arrays.asList(surlArray);
    }

    public ArrayOfSURLs() {
        surlList = new ArrayList<TSURL>();
    }

    public List<TSURL> getArrayList() {
        return surlList;
    }

    public TSURL getTSURL(int i) {
        return (TSURL) surlList.get(i);
    }

    public void setTSURL(int index, TSURL surl) {
        surlList.set(index, surl);
    }

    public void addTSURL(TSURL surl) {
        surlList.add(surl);
    }

    public int size() {
        return surlList.size();
    }

	public static ArrayOfSURLs decode(Map inputParam, String name)
			throws InvalidArrayOfSURLsAttributeException {

		List<Object> list = null;
		ArrayOfSURLs surlArray = new ArrayOfSURLs();
		try
		{
			/* here we can have a cast exception if the array contained in 
			 * the hashmap has been created as an object array! */
			list = Arrays.asList((Object[]) inputParam.get(name));
		} catch(NullPointerException e)
		{
			// log.warn("Empty SURL array found!");
		}

		if(list == null)
		{
			throw new InvalidArrayOfSURLsAttributeException(list);			
		}
		for(Object surlString : list)
		{
			TSURL surl = null;
			try
			{
				surl = TSURL.makeFromStringValidate((String)surlString);
			} catch(InvalidTSURLAttributesException e)
			{
				throw new InvalidArrayOfSURLsAttributeException(null);
			}
			surlArray.addTSURL(surl);
		}
		return surlArray;
	}

    public String toString() {
        StringBuffer buf = new StringBuffer("");

        if (surlList != null) {
            for (int i = 0; i < surlList.size(); i++) {
                buf.append("'" + (TSURL) surlList.get(i) + "'");
                if (i + 1 < surlList.size())
                    buf.append(",");
            }

        } else {
            return buf.toString();
        }

        return buf.toString();

    }
}
