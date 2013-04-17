/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;

// import com.sun.image.codec.jpeg.TruncatedFileException;

/**
 * This class represents the TFileStorageType of an Srm request.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */
public class TFileLocality {

	public static String PNAME_FILELOCALITY = "fileLocality";

	public static final TFileLocality ONLINE = new TFileLocality("ONLINE");
	public static final TFileLocality NEARLINE = new TFileLocality("NEARLINE");
	public static final TFileLocality ONLINE_AND_NEARLINE = new TFileLocality(
		"ONLINE_AND_NEARLINE");
	public static final TFileLocality LOST = new TFileLocality("LOST");
	public static final TFileLocality NONE = new TFileLocality("NONE");
	public static final TFileLocality UNAVAILABLE = new TFileLocality(
		"UNAVAILABLE");
	public static final TFileLocality EMPTY = new TFileLocality("");

	private String fileLocality = null;

	private TFileLocality(String fileLoc) {

		this.fileLocality = fileLoc;
	}

	public String toString() {

		return fileLocality;
	}

	public String getValue() {

		return fileLocality;
	}

	/**
	 * Facility method to obtain a TFileStorageType object given its String
	 * representation. If an invalid String is supplied, then an EMPTY
	 * TFileStorageType is returned.
	 */
	public static TFileLocality getTFileLocality(String loc) {

		if (loc.toLowerCase().replaceAll(" ", "")
			.equals(ONLINE.getValue().toLowerCase())) {
			return ONLINE;
		} else if (loc.toLowerCase().replaceAll(" ", "")
			.equals(NEARLINE.getValue().toLowerCase())) {
			return NEARLINE;
		} else if (loc.toLowerCase().replaceAll(" ", "")
			.equals(ONLINE_AND_NEARLINE.getValue().toLowerCase())) {
			return ONLINE_AND_NEARLINE;
		} else if (loc.toLowerCase().replaceAll(" ", "")
			.equals(LOST.getValue().toLowerCase())) {
			return LOST;
		} else if (loc.toLowerCase().replaceAll(" ", "")
			.equals(NONE.getValue().toLowerCase())) {
			return NONE;
		} else if (loc.toLowerCase().replaceAll(" ", "")
			.equals(UNAVAILABLE.getValue().toLowerCase())) {
			return UNAVAILABLE;
		} else {
			return EMPTY;
		}
	}

	/**
	 * Facility method to obtain a TFileStorageType object given its int
	 * representation. If an invalid String is supplied, then an EMPTY
	 * TFileStorageType is returned.
	 */
	public static TFileLocality getTFileLocality(int loc) {

		switch (loc) {
		case 0:
			return ONLINE;
		case 1:
			return NEARLINE;
		case 2:
			return ONLINE_AND_NEARLINE;
		case 3:
			return LOST;
		case 4:
			return NONE;
		case 5:
			return UNAVAILABLE;
		default:
			return EMPTY;
		}
	}

	/**
	 * Decode method, use to create a TFileLocaliy object from the information
	 * contained into a structure parametet received from FE.
	 * 
	 * @param inputParam
	 * @param name
	 * @return
	 */

	public static TFileLocality decode(Map inputParam, String name) {

		Integer fileLoc = (Integer) inputParam.get(name);
		if (fileLoc != null)
			return TFileLocality.getTFileLocality(fileLoc.intValue());
		else
			return TFileLocality.EMPTY;
	}

	/**
	 * Encode method use to create a structured paramter that represents this
	 * object, used for pass information to FE.
	 * 
	 * @param param
	 * @param name
	 */
	public void encode(Map param, String name) {

		Integer value = null;
		if (this.equals(TFileLocality.ONLINE))
			value = new Integer(0);
		if (this.equals(TFileLocality.NEARLINE))
			value = new Integer(1);
		if (this.equals(TFileLocality.ONLINE_AND_NEARLINE))
			value = new Integer(2);
		if (this.equals(TFileLocality.LOST))
			value = new Integer(3);
		if (this.equals(TFileLocality.NONE))
			value = new Integer(4);
		if (this.equals(TFileLocality.UNAVAILABLE))
			value = new Integer(5);
		param.put(name, value);

	}

	/*
	 * public static void main(String[] args) { //Testing all types
	 * System.out.println("Testing all types...");
	 * System.out.println("VOLATILE: "+
	 * TFileStorageType.VOLATILE+"; getTFileStorageType from String:"
	 * +TFileStorageType
	 * .getTFileStorageType(TFileStorageType.VOLATILE.toString()));
	 * System.out.println
	 * ("DURABLE: "+TFileStorageType.DURABLE+"; getTFileStorageType from String:"
	 * +TFileStorageType
	 * .getTFileStorageType(TFileStorageType.DURABLE.toString()));
	 * System.out.println
	 * ("PERMANENT: "+TFileStorageType.PERMANENT+"; getTFileStorageType from String:"
	 * +
	 * TFileStorageType.getTFileStorageType(TFileStorageType.PERMANENT.toString())
	 * ); System.out.println("EMPTY: "+TFileStorageType.EMPTY+
	 * "; getTFileStorageType from String:"
	 * +TFileStorageType.getTFileStorageType(TFileStorageType.EMPTY.toString()));
	 * }
	 */

}
