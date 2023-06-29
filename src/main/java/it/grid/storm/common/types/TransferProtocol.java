/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

/**
 * This class represents the possible transfer protocols of StoRM.
 * 
 * @author EGRID ICTP - CNAF Bologna
 * @date March 23rd, 2005
 * @version 2.0
 */
public class TransferProtocol {

	private String protocol;

	/**
	 * Static attribute that indicates EMPTY TransferProtocol
	 */
	public static TransferProtocol EMPTY = new TransferProtocol("empty") {

		public int hashCode() {

			return 0;
		}
	};

	/**
	 * Static attribute that indicates FILE TransferProtocol.
	 */
	public static TransferProtocol FILE = new TransferProtocol("file") {

		public int hashCode() {

			return 1;
		}
	};

	/**
	 * Static attribute that indicates GSIFTP TransferProtocol.
	 */
	public static TransferProtocol GSIFTP = new TransferProtocol("gsiftp") {

		public int hashCode() {

			return 2;
		}
	};

	/**
	 * Static attribute that indicates RFIO TransferProtocol.
	 */
	public static TransferProtocol RFIO = new TransferProtocol("rfio") {

		public int hashCode() {

			return 3;
		}
	};

	/**
	 * Static attribute that indicates ROOT TransferProtocol.
	 */
	public static TransferProtocol ROOT = new TransferProtocol("root") {

		public int hashCode() {

			return 4;
		}
	};
	
	/**
	 * Static attribute that indicates XROOT TransferProtocol.
	 */
	public static TransferProtocol XROOT = new TransferProtocol("xroot") {

		public int hashCode() {

			return 8;
		}
	};

	/**
	 * Static attribute that indicates HTTP TransferProtocol.
	 */
	public static TransferProtocol HTTP = new TransferProtocol("http") {

		public int hashCode() {

			return 5;
		}
	};

	/**
	 * Static attribute that indicates HTTPS TransferProtocol.
	 */
	public static TransferProtocol HTTPS = new TransferProtocol("https") {

		public int hashCode() {

			return 6;
		}
	};

	private TransferProtocol(String protocol) {

		this.protocol = protocol;
	}

	public String getValue() {

		return protocol;
	}

	public String toString() {

		return protocol;
	}

	/**
	 * Facility method to obtain a TransferProtocol object given its String
	 * representation. Any white spaces are removed. In case no match is found, an
	 * EMPTY TransferProtocol is returned.
	 */
	public static TransferProtocol getTransferProtocol(String protocol) {

		if (protocol.toLowerCase().replaceAll(" ", "").equals(FILE.toString()))
			return FILE;
		if (protocol.toLowerCase().replaceAll(" ", "").equals(GSIFTP.toString()))
			return GSIFTP;
		if (protocol.toLowerCase().replaceAll(" ", "").equals(RFIO.toString()))
			return RFIO;
		if (protocol.toLowerCase().replaceAll(" ", "").equals(ROOT.toString()))
			return ROOT;
		if (protocol.toLowerCase().replaceAll(" ", "").equals(XROOT.toString()))
			return XROOT;
		if (protocol.toLowerCase().replaceAll(" ", "").equals(HTTP.toString()))
			return HTTP;
		if (protocol.toLowerCase().replaceAll(" ", "").equals(HTTPS.toString()))
			return HTTPS;
		return EMPTY;
	}
}
