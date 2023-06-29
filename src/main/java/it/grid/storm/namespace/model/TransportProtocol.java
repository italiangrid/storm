/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import it.grid.storm.namespace.naming.*;

public class TransportProtocol {

	private int protocolID = -1;
	private Protocol protocol = null;
	private Authority service = null;

	public TransportProtocol(Protocol protocol, Authority service) {

		this.protocol = protocol;
		this.service = service;
	}

	public TransportProtocol(Protocol protocol) {

		this.protocol = protocol;
	}

	public Protocol getProtocol() {

		return this.protocol;
	}

	// Used in Protocol Pool definition
	public void setProtocolID(int id) {

		this.protocolID = id;
	}

	// Used in Protocol Pool definition
	public int getProtocolID() {

		return this.protocolID;
	}

	public Authority getAuthority() {

		if (this.protocol.equals(Protocol.FILE)) {
			return Authority.EMPTY;
		} else {
			return this.service;
		}
	}

	public void setLocalAuthority() {

		if (!this.protocol.equals(Protocol.FILE)) {
			this.service = new Authority(NamingConst.getServiceDefaultHost());
		}
	}

	public void setAuthority(Authority service) {

		this.service = service;
	}

	private String getURIRoot() {

		StringBuilder sb = new StringBuilder();
		if (protocolID != -1)
			sb.append("[id:" + this.protocolID + "] ");
		sb.append(protocol.getSchema());
		sb.append("://");
		if (service != null) {
			sb.append(service);
		}
		return sb.toString();
	}

	public String toString() {

		return getURIRoot();
	}

	public boolean equals(Object other) {

		boolean result = false;
		if (other instanceof TransportProtocol) {
			TransportProtocol otherTP = (TransportProtocol) other;
			if (otherTP.getProtocol().equals(this.getProtocol())) { // Protocol is
																															// equal
				// Check if the Authority is equal.
				if (otherTP.getAuthority().equals(this.getAuthority())) {
					result = true;
				}
			}
		}
		return result;
	}

	@Override
	public int hashCode() {

		int result = 17;
		result = 31 * result + protocolID;
		result = 31 * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = 31 * result + ((service == null) ? 0 : service.hashCode());
		return result;
	}

}
