/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents a Space Token
 * 
 * @author EGRID ICTP Trieste / CNAF Bologna
 * @date March 23rd, 2005
 * @version 2.0
 */
package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TSpaceToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4511316534032776357L;

	public static String PNAME_SPACETOKEN = "spaceToken";

	private String token = ""; // string representing the token!
	private boolean empty = true;

	static private TSpaceToken emptyToken; // only instance of empty spaceToken

	private TSpaceToken(String token, boolean empty) {

		this.token = token;
		this.empty = empty;
	}

	/**
	 * Factory method thta requires a String; if it is null, an
	 * InvalidTSpaceTokenAttributeException is thrown.
	 */
	public static TSpaceToken make(String s)
		throws InvalidTSpaceTokenAttributesException {

		if (s == null)
			throw new InvalidTSpaceTokenAttributesException();
		return new TSpaceToken(s, false);
	}

	/**
	 * Factory method that returns an Empty TSpaceToken
	 */
	public static TSpaceToken makeEmpty() {

		if (emptyToken != null)
			return emptyToken;
		emptyToken = new TSpaceToken("", true);
		return emptyToken;
	}

	public String getValue() {

		return token;
	}

	public boolean isEmpty() {

		return empty;
	}

	public String toString() {

		if (empty)
			return "Empty";
		return token;
	}

	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof TSpaceToken))
			return false;
		TSpaceToken st = (TSpaceToken) o;
		if ((empty) && (st.empty))
			return true;
		return (!empty) && (!st.empty) && token.equals(st.token);
	}

	public int hashCode() {

		if (empty)
			return 0;
		int hash = 17;
		return 37 * hash + token.hashCode();
	}

	/**
	 * Decode method, used to represents this object into a structured parameter for
	 * FE communication.
	 * 
	 * @param vector
	 */
	public final static TSpaceToken decode(Map<?, ?> param, String name) {

		String tokenString = (String) param.get(name);
		TSpaceToken spaceToken = TSpaceToken.makeEmpty();
		if (tokenString != null) {
			// Creation of srm TSpaceToken
			try {
				spaceToken = TSpaceToken.make(tokenString);
			} catch (InvalidTSpaceTokenAttributesException e) {
				;// log.warn("Error creating TSpaceToken:"+e);
			}
		}
		return spaceToken;
	}

	/**
	 * Encode method, used to represents this object into a structured parameter for
	 * FE communication.
	 * 
	 * @param vector
	 */
	public void encode(List<String> list) {

		list.add(this.toString());
	}

	public void encode(Map<String, String> outputParam, String fieldName) {

		outputParam.put(fieldName, (String) token);
	}
}
