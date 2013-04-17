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

/**
 * This class represents the TTransferParameters SRM type.
 * 
 * @author Alberto Forti
 * @author Cnaf -INFN Bologna
 * @date July, 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TTransferParameters implements Serializable {

	private static final long serialVersionUID = 7309411351545907539L;

	private static final Logger log = LoggerFactory
		.getLogger(TTransferParameters.class);

	public static final String PNAME_transferParameters = "transferParameters";

	private TAccessPattern accessPattern = null;
	private TConnectionType connectionType = null;
	private String[] arrayOfClientNetworks;
	private String[] arrayOfTransferProtocols;

	public TTransferParameters() {

	}

	public TTransferParameters(TAccessPattern accessPattern,
		TConnectionType connectionType, String[] arrayOfClientNetworks,
		String[] arrayOfTransferProtocols) {

		this.accessPattern = accessPattern;
		this.connectionType = connectionType;
		this.arrayOfClientNetworks = arrayOfClientNetworks;
		this.arrayOfTransferProtocols = arrayOfTransferProtocols;
	}

	/**
	 * Fills this class using the values found in a structure inside a Hashtable.
	 * The Hashtable may contain different structures inside, all are identifiend
	 * by a name. Used for communication with the FE.
	 * 
	 * @param inputParam
	 *          Hashtable to read.
	 * @param fieldName
	 *          Name that identifies the TTransferParameters structure in the
	 *          Hashtable.
	 * @return A new TTransferParameters instance.
	 */
	public static TTransferParameters decode(Map inputParam, String fieldName) {

		Map param;
		param = (Map) inputParam.get(fieldName);
		if (param == null) {
			return null;
		}

		return TTransferParameters.decode(param);
	}

	/**
	 * Fills this class using a Hashtable structure. The Hashtable contains only
	 * the TTransferParameters fields. Used for communication with the FE.
	 * 
	 * @param param
	 *          Hashtable to read.
	 * @return A new TTransferParameters instance
	 */
	public static TTransferParameters decode(Map param) {

		String[] clientNetworks = null;
		String[] transferProtocols = null;
		String memberName;
		List vector = null;

		TAccessPattern accPatt = TAccessPattern.decode(param,
			TAccessPattern.PNAME_accessPattern);

		TConnectionType connType = TConnectionType.decode(param,
			TConnectionType.PNAME_connectionType);

		memberName = new String("arrayOfClientNetworks");
		// vector = (Hashtable) param.get(memberName);
		try {
			vector = Arrays.asList((Object[]) param.get(memberName));
		} catch (NullPointerException e) {
			// log.warn("Empty SURL array found!");
		}

		if (vector != null) {
			int arraySize = vector.size();
			clientNetworks = new String[arraySize];
			for (int i = 0; i < arraySize; i++) {
				clientNetworks[i] = (String) vector.get(i);
			}
		}

		memberName = new String("arrayOfTransferProtocols");
		// vector = (Hashtable) param.get(memberName);
		try {
			vector = Arrays.asList((Object[]) param.get(memberName));
		} catch (NullPointerException e) {
			// log.warn("Empty SURL array found!");
		}
		if (vector != null) {
			int arraySize = vector.size();
			transferProtocols = new String[arraySize];
			for (int i = 0; i < arraySize; i++) {
				transferProtocols[i] = (String) vector.get(i);
			}
		}

		return new TTransferParameters(accPatt, connType, clientNetworks,
			transferProtocols);
	}

	/**
	 * Returns the accessPattern value
	 * 
	 * @return accessPattern is of type TAccessPattern
	 */
	public TAccessPattern getAccessPattern() {

		return accessPattern;
	}

	/**
	 * Returns the connectionType value
	 * 
	 * @return connectionType is of type TConnectionType
	 */
	public TConnectionType getConnectionType() {

		return connectionType;
	}

	/**
	 * Returns the arrayOfClientNetworks field
	 * 
	 * @return arrayOfClientNetworks is of type String[]
	 */
	public String[] getArrayOfClientNetworks() {

		return arrayOfClientNetworks;
	}

	/**
	 * Returns the arrayOfTransferProtocols value
	 * 
	 * @return arrayOfTransferProtocols is of type String[]
	 */
	public String[] getArrayOfTransferProtocols() {

		return arrayOfTransferProtocols;
	}
}
