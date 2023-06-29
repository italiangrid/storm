/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

/**
 * This class represents a port in a SFN. An int between 0 and 65535 is
 * required: if the limits are exceeded then an InvalidPortAttributeException is
 * thrown.
 * 
 * @author EGRID - ICTP Trieste; CNAF - Bologna
 * @date March 25th, 2005
 * @version 2.0
 */
public class Port {

	private int port; // int representing the port number
	private boolean empty = true; // boolean true id this object refers to the
																// empty port

	/**
	 * Private constructor.
	 */
	private Port(int port, boolean empty) {

		this.port = port;
		this.empty = empty;
	}

	/**
	 * Static method to make an empty port.
	 */
	public static Port makeEmpty() {

		return new Port(-1, true);
	}

	/**
	 * Static method used to make a non empty Port object. It requires an int
	 * between 0 and 65535 representing the port: if the limits are exceeded then
	 * an InvalidPortAttributeException is thrown.
	 */
	public static Port make(int port) throws InvalidPortAttributeException {

		if ((port < 0) || (port > 65535))
			throw new InvalidPortAttributeException(port);
		return new Port(port, false);
	}

	/**
	 * Method that returns whether this object refers to the empty port or not.
	 */
	public boolean isEmpty() {

		return empty;
	}

	/**
	 * Method that returns an int representing this port. An empty port will
	 * return -1.
	 */
	public int toInt() {

		if (empty)
			return -1;
		return port;
	}

	public int getValue() {

		return port;
	}

	public String toString() {

		if (empty)
			return "Empty Port";
		return "" + port;
	}

	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof Port))
			return false;
		Port po = (Port) o;
		if (po.empty && empty)
			return true;
		return (!po.empty) && (!empty) && (port == po.port);
	}

	public int hashCode() {

		if (empty)
			return -1;
		int hash = 17;
		return 37 * hash + port;
	}
}
