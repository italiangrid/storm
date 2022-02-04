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

package it.grid.storm.common.types;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.namespace.model.Protocol;

/**
 * This class represent the Transport Protocol available to get file from a
 * certain Storage Element. This Trasnport Protocol prefix will be used to match
 * with user specifed prefix to TTURL Creation.
 */
public class TURLPrefix {

	private static final Logger log = LoggerFactory.getLogger(TURLPrefix.class);
	public static final String PNAME_TURL_PREFIX = "turlPrefix";
	private ArrayList<Protocol> desiredProtocols;

	public TURLPrefix() {

		this.desiredProtocols = new ArrayList<Protocol>();
	}

	public TURLPrefix(Collection<Protocol> protocols) {

		this.desiredProtocols = new ArrayList<Protocol>(protocols);
	}

	/**
	 * Method used to add a TransferProtocol to this holding structure. Null may
	 * also be added. A boolean true is returned if the holding structure changed
	 * as a result of the add. If this holding structure does not change, then
	 * false is returned.
	 */
	public boolean addProtocol(Protocol protocol) {

		return this.desiredProtocols.add(protocol);
	}

	/**
	 * Method used to retrieve a TransferProtocol from this holding structure. An
	 * int is needed as index to the TransferProtocol to retrieve. Elements are
	 * not removed!
	 */
	public Protocol getProtocol(int index) {

		return desiredProtocols.get(index);
	}

	public List<Protocol> getDesiredProtocols() {

		return this.desiredProtocols;
	}

	public int size() {

		return desiredProtocols.size();
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("TURLPrefix: ");
		for (Iterator<Protocol> i = desiredProtocols.iterator(); i.hasNext();) {
			sb.append(i.next());
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * @param inputParam
	 * @param memberName
	 * @return
	 */
	public static TURLPrefix decode(Map<String, Object> inputParam, String memberName) {

		TURLPrefix decodedTurlPrefix = null;
		if (inputParam.containsKey(memberName)) {
			if (inputParam.get(memberName) != null) {
				Object[] valueArray = null;
				if (inputParam.get(memberName).getClass().isArray()) {
					valueArray = (Object[]) inputParam.get(memberName);
				} else {
					valueArray = new Object[] { inputParam.get(memberName) };
				}
				LinkedList<Protocol> protocols = new LinkedList<Protocol>();
				for (Object value : valueArray) {
					Protocol protocol = Protocol.getProtocol(value.toString());
					if (protocol.equals(Protocol.UNKNOWN)) {
						log.warn("Protocol {} is unknown." , value);
					} else {
						protocols.add(protocol);
					}
				}
				if (protocols.size() > 0) {
					decodedTurlPrefix = new TURLPrefix(protocols);
				}
			}
		}
		return decodedTurlPrefix;
	}

	public boolean allows(Protocol protocol) {

		return desiredProtocols.contains(protocol);
	}
}
