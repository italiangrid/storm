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
 * This class represents the TSURLInfo data associated with the SRM request, that is
 * it contains info about: TSURL , StorageSystemInfo
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
* @date
 * @version 1.0
 */

package it.grid.storm.srm.types;



public class TSURLInfo {
	private TSURL surl = null;
	private TStorageSystemInfo systemInfo = null;
	public TSURLInfo(){ }


	public TSURLInfo(TSURL surl,TStorageSystemInfo info ) throws InvalidTSURLInfoAttributeException {
		boolean ok = ( !(surl==null));
		if (!ok) throw new InvalidTSURLInfoAttributeException(surl);
		this.surl = surl;
		this.systemInfo = info;
		//System.out.println("TSUrlInfo created: surl: "+surl+", sysInfo: "+systemInfo);
	}



	/**
	 * Method that return SURL specified in SRM request.
	*/
	
	public TSURL getSurl() {
		return surl;
	}

	public void setSurl(TSURL surl) {
		this.surl = surl;
	}

	/**
	* Set StorageSystemInfo	
	*/
	public void setInfo(TStorageSystemInfo info) {
		this.systemInfo = info;
	}

	/**
	* Get StorageSystemInfo
	*/
	public TStorageSystemInfo getInfo() {
		return this.systemInfo;
	}


}
