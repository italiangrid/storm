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
 * Class that represents the TStorageSystemInfo.
 *
 * @author:  CNAF Bologna
 * @version: 1.0
 * @date:    May 2005
 */
package it.grid.storm.srm.types;

import java.io.Serializable;

public class TStorageSystemInfo implements Serializable{

	private String info  = null;
	private boolean empty = true;


    public String toString() {
        return info;
    }


	public TStorageSystemInfo(String s, boolean empty) {
		this.info = s  ;
		this.empty = empty;
	}

	public static TStorageSystemInfo makeEmpty() {
		return new TStorageSystemInfo("",true);
	}

	public static TStorageSystemInfo make(String s) {
		return new TStorageSystemInfo(s,false);
	}

	public boolean isEmpty() {
		return empty;
	}

	public String getValue() {
		return info;
	}
}

