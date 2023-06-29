/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

import java.util.Map;

import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.InvalidTFNAttributesException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.TFN;
import it.grid.storm.common.types.TransferProtocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a TURL, that is a Transfer URL. It is made up of a
 * TransferProtocol and a TFN.
 * 
 * @author EGRID ICTP Trieste - CNAF Bologna
 * @date March 26th, 2005
 * @version 2.0
 */
public class TTURL {

	private static Logger log = LoggerFactory.getLogger(TTURL.class);;
	private TransferProtocol tp;
	private TFN tfn;
	private boolean empty = true; // boolean true if this is an empty object

	public static final String PNAME_TURL = "turl";

	private TTURL(TransferProtocol tp, TFN tfn, boolean empty) {

		this.tp = tp;
		this.tfn = tfn;
		this.empty = empty;
	}

	/**
	 * Static method that returns an empty TTURL.
	 */
	public static TTURL makeEmpty() {

		return new TTURL(TransferProtocol.EMPTY, TFN.makeEmpty(), true);
	}

	/**
	 * Static method that requires the TransferProtocol and the TFN of this TURL:
	 * if any is null or empty, an InvalidTURLAttributesException is thrown.
	 */
	public static TTURL make(TransferProtocol tp, TFN tfn)
		throws InvalidTTURLAttributesException {

		if ((tp == null) || (tfn == null) || (tp == TransferProtocol.EMPTY)
			|| (tfn.isEmpty())) {
			throw new InvalidTTURLAttributesException(tp, tfn);
		}
		return new TTURL(tp, tfn, false);
	}

	/**
	 * Static factory method that returns a TTURL from a String representation: if
	 * it is null or malformed then an InvalidTTURLAttributesException is thrown.
	 */
	public static TTURL makeFromString(String s)
		throws InvalidTTURLAttributesException {

		if (s == null) {
			throw new InvalidTTURLAttributesException(null, null);
		}
		int separator = s.indexOf("://"); // first occurence of ://
		if ((separator == -1) || (separator == 0)) {
			throw new InvalidTTURLAttributesException(null, null); // separator not
																															// found or right
																															// at the
																															// beginning!
		}
		String tpString = s.substring(0, separator);
		TransferProtocol tp = null;
		try {
			tp = TransferProtocol.getTransferProtocol(tpString);
		} catch (IllegalArgumentException e) {
			log.warn("TTURL: Transfer protocol by {} is empty, but that's fine.", 
			  tpString);
		}
		if ((separator + 3) > (s.length())) {
			throw new InvalidTTURLAttributesException(tp, null); // separator found at
																														// the end!
		}
		String tfnString = s.substring(separator + 3, s.length());
		TFN tfn = null;
		if (tfnString.startsWith("/")) {
			try {
				tfn = TFN.makeByPFN(PFN.make(tfnString));
			} catch (InvalidTFNAttributesException e) {
				log.warn("TFN by {} is empty, but that's fine.", tfnString);
			} catch (InvalidPFNAttributeException ex) {
				log.error("Invalid PFN: {}", tfnString, ex);
			}
		} else {
			try {
				tfn = TFN.makeFromString(tfnString);
			} catch (InvalidTFNAttributesException e) {
				log.warn("TFN by {} is empty, but that's fine.", tfnString);
			}
		}
		return TTURL.make(tp, tfn);
	}

	/**
	 * Method that returns true if this object is an empty TTURL
	 */
	public boolean isEmpty() {

		return empty;
	}

	/**
	 * Method that returns the TransferProtocol of this TURL. If it is an empty
	 * TTURL, then an empty TransferProtocol is returned.
	 */
	public TransferProtocol protocol() {

		if (empty) {
			return TransferProtocol.EMPTY;
		}
		return tp;
	}

	/**
	 * Method that returns the TFN of this TURL. If it is an empty TTURL, then an
	 * empty TFN is returned.
	 */
	public TFN tfn() {

		if (empty) {
			return TFN.makeEmpty();
		}
		return tfn;
	}

	/**
	 * Encode TTURL for xmlrpc communication.
	 */
	public void encode(Map<String, Object> param, String name) {

		param.put(name, toString());
	}

	@Override
	public String toString() {

		if (empty) {
			return "Empty TTURL";
		}
		return tp + "://" + tfn;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		if (!(o instanceof TTURL)) {
			return false;
		}
		TTURL turlo = (TTURL) o;
		if (empty && turlo.empty) {
			return true;
		}
		return (!empty) && (!turlo.empty) && tp.equals(turlo.tp)
			&& tfn.equals(turlo.tfn);
	}

	@Override
	public int hashCode() {

		if (empty) {
			return 0;
		}
		int hash = 17;
		hash = 37 * hash + tp.hashCode();
		hash = 37 * hash + tfn.hashCode();
		return hash;
	}
}
