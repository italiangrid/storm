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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class represents a SFN, that is a Site File Name. It is used as part of
 * a SURL.
 * 
 * @author EGRID ICTP - CNAF Bologna
 * @version 2.0
 * @date March 2005
 */
public class SFN {

	private Machine m = null;
	private Port p = null;
	private EndPoint ep = null;
	private StFN pn = null;
	private boolean empty = true;

	private SFN(Machine m, Port p, EndPoint ep, StFN pn, boolean empty) {

		this.m = m;
		this.p = p;
		this.ep = ep;
		this.pn = pn;
		this.empty = empty;
	}

	/**
	 * Static method that returns an empty SFN.
	 */
	public static SFN makeEmpty() {

		return new SFN(Machine.makeEmpty(), Port.makeEmpty(), EndPoint.makeEmpty(),
			StFN.makeEmpty(), true);
	}

	/**
	 * Static method that requires a Machine m, the Port p on that Machine, and
	 * the StFN stfn. An InvalidSFNAttributesException is thrown if any is null or
	 * empty.
	 */
	public static SFN makeInSimpleForm(Machine m, Port p, StFN stfn)
		throws InvalidSFNAttributesException {

		if ((m == null) || (p == null) || (stfn == null) || m.isEmpty()
			|| p.isEmpty() || stfn.isEmpty()) {
			throw new InvalidSFNAttributesException(m, p, stfn);
		}
		return new SFN(m, p, EndPoint.makeEmpty(), stfn, false);
	}

	/**
	 * Static method that requires a Machine m, the Port p on that Machine, and
	 * the StFN stfn. An InvalidSFNAttributesException is thrown if any is null or
	 * empty.
	 */
	public static SFN makeInQueryForm(Machine m, Port p, EndPoint ep, StFN stfn)
		throws InvalidSFNAttributesException {

		if ((m == null) || (p == null) || (ep == null) || (stfn == null)
			|| m.isEmpty() || p.isEmpty() || (ep.isEmpty()) || stfn.isEmpty()) {
			throw new InvalidSFNAttributesException(m, p, ep, stfn);
		}
		return new SFN(m, p, ep, stfn, false);
	}

	/**
	 * Static method that requires a Machine m, and the StFN stfn. An
	 * InvalidSFNAttributesException is thrown if any is null or empty.
	 */
	public static SFN makeInSimpleForm(Machine m, StFN stfn)
		throws InvalidSFNAttributesException {

		if ((m == null) || (stfn == null) || m.isEmpty() || stfn.isEmpty()) {
			throw new InvalidSFNAttributesException(m, null, stfn);
		}
		return new SFN(m, Port.makeEmpty(), EndPoint.makeEmpty(), stfn, false);
	}

	/**
	 * Static method that requires a Machine m, the EndPoint ep, and the StFN
	 * stfn. An InvalidSFNAttributesException is thrown if any is null or empty.
	 */
	public static SFN makeInQueryForm(Machine m, EndPoint ep, StFN stfn)
		throws InvalidSFNAttributesException {

		if ((m == null) || (stfn == null) || (ep == null) || m.isEmpty()
			|| stfn.isEmpty() || (ep.isEmpty())) {
			throw new InvalidSFNAttributesException(m, null, stfn);
		}
		return new SFN(m, Port.makeEmpty(), ep, stfn, false);
	}

	/**
	 * Method that returns a Collection of all parent SFNs. The following example
	 * clarifies what is meant by parent SFNs.
	 * 
	 * Original SFN: storage.egrid.it:8444/EGRID/original/data/nyse/file.txt
	 * 
	 * Parent SFNs: storage.egrid.it:8444/EGRID/original/data/nyse
	 * storage.egrid.it:8444/EGRID/original/data
	 * storage.egrid.it:8444/EGRID/original storage.egrid.it:8444/EGRID
	 * 
	 * An empty collection is returned if any error occurs during creation of
	 * parent SFNs. Likewise if This is an EmptySFN.
	 */
	public Collection<SFN> getParents() {

		if (empty) {
			return new ArrayList<SFN>();
		}
		try {
			Collection<SFN> aux = new ArrayList<SFN>();
			Collection<StFN> auxStFN = pn.getParents();
			for (Iterator<StFN> i = auxStFN.iterator(); i.hasNext();) {
				if (ep.isEmpty()) {
					aux.add(SFN.makeInSimpleForm(m, p, (StFN) i.next()));
				} else {
					aux.add(SFN.makeInQueryForm(m, p, ep, (StFN) i.next()));
				}
			}
			return aux;
		} catch (InvalidSFNAttributesException e) {
			return new ArrayList<SFN>();
		}
	}

	/**
	 * Method that returns the parent SFN. The following example clarifies what is
	 * meant by parent SFN.
	 * 
	 * Original SFN: storage.egrid.it:8444/EGRID/original/data/nyse/file.txt
	 * 
	 * Parent SFN: storage.egrid.it:8444/EGRID/original/data/nyse
	 * 
	 * An empty SFN is returned if any error occurs during creation. Likewise if
	 * This is an EmptySFN.
	 */
	public SFN getParent() {

		if (empty) {
			return makeEmpty();
		}
		try {
			if (ep.isEmpty()) {
				return SFN.makeInSimpleForm(m, p, pn.getParent());
			} else {
				return SFN.makeInQueryForm(m, p, ep, pn.getParent());
			}
		} catch (InvalidSFNAttributesException e) {
			return makeEmpty();
		}
	}

	/**
	 * Method that returns a boolean true if this object is empty.
	 */
	public boolean isEmpty() {

		return empty;
	}

	/**
	 * Method that returns the Machine specified in this SFN. If this is an empty
	 * SFN, then an empty Machine is returned.
	 */
	public Machine machine() {

		if (empty) {
			return Machine.makeEmpty();
		}
		return m;
	}

	/**
	 * Method that returns the Port specified in this SFN. If this is an empty
	 * SFN, then an empty Port is returned.
	 */
	public Port port() {

		if (empty) {
			return Port.makeEmpty();
		}
		return p;
	}

	/**
	 * Method that returns the StFN specified in this SFN. If this is an empty
	 * SFN, then an empty StFN is returned.
	 */
	public StFN stfn() {

		if (empty) {
			return StFN.makeEmpty();
		}
		return pn;
	}

	@Override
	public String toString() {

		if (empty) {
			return "Empty SFN";
		}
		if (ep.isEmpty()) {
			if (p.isEmpty()) {
				return m.toString() + pn;
			} else {
				return m + ":" + p + pn;
			}
		} else {
			if (p.isEmpty()) {
				return m.toString() + ep.toString() + "?SFN=" + pn;
			} else {
				return m + ":" + p + ep + "?SFN=" + pn;
			}
		}
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		if (!(o instanceof SFN)) {
			return false;
		}
		SFN sfno = (SFN) o;
		if (empty && sfno.empty) {
			return true;
		}
		return !empty && !sfno.empty && m.equals(sfno.m) && p.equals(sfno.p)
			&& ep.equals(sfno.ep) && pn.equals(sfno.pn);
	}

	@Override
	public int hashCode() {

		if (empty) {
			return 0;
		}
		int hash = 17;
		hash = 37 * hash + m.hashCode();
		hash = 37 * hash + p.hashCode();
		hash = 37 * hash + ep.hashCode();
		hash = 37 * hash + pn.hashCode();
		return hash;
	}

}
