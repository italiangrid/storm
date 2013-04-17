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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a TFN Transfer File Name.
 * 
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 25th, 2005
 * @version 2.0
 */
public class TFN {

	private static Logger log = LoggerFactory.getLogger(TFN.class);

	private Machine m = null;
	private Port p = null;
	private PFN pfn = null;
	private boolean empty = true; // boolean indicating whether this is an empty
																// TFN

	private TFN(Machine m, Port p, PFN pfn, boolean empty) {

		this.m = m;
		this.p = p;
		this.pfn = pfn;
		this.empty = empty;
	}

	/**
	 * Static method that returns an empty TFN.
	 */
	public static TFN makeEmpty() {

		return new TFN(Machine.makeEmpty(), Port.makeEmpty(), PFN.makeEmpty(), true);
	}

	/**
	 * Static method that returns a TFN, and requires a Machine m, the Port p on
	 * that Machine, and the PhysicalFileName pfn. An
	 * InvalidTFNAttributesException is thrown if any is null or empty.
	 */
	public static TFN make(Machine m, Port p, PFN pfn)
		throws InvalidTFNAttributesException {

		if ((m == null) || (p == null) || (pfn == null) || m.isEmpty()
			|| pfn.isEmpty())
			throw new InvalidTFNAttributesException(m, p, pfn);
		return new TFN(m, p, pfn, false);
	}

	/**
	 * Static method that returns a TFN with the specified PFN, and EmptyMachine
	 * as well as EmptyPort. The intended use of this method is to create TFN to
	 * be used in TURLs with FILE protocol, where no machine and port are needed.
	 * An InvalidTFNAttributesException is thrown if pfn is null or empty: notice
	 * that the exception will also show Machine and Port as null.
	 */
	public static TFN makeByPFN(PFN pfn) throws InvalidTFNAttributesException {

		if ((pfn == null) || (pfn.isEmpty()))
			throw new InvalidTFNAttributesException(null, null, pfn);
		return new TFN(Machine.makeEmpty(), Port.makeEmpty(), pfn, false);
	}

	/**
	 * Static method that returns a TFN from a String representation. If the
	 * supplied String is null or malformed, an InvalidTFNAttributesException is
	 * thrown.
	 */
	public static TFN makeFromString(String s)
		throws InvalidTFNAttributesException {

		if (s == null)
			throw new InvalidTFNAttributesException(null, null, null);
		int colon = s.indexOf(":"); // first occurence of :
		int slash = s.indexOf("/"); // first occurence of /

		if (colon == -1) {
			// missing port specification
			if ((slash == -1) || (slash == 0))
				throw new InvalidTFNAttributesException(null, null, null); // no colon
																																		// and no
																																		// slash or
																																		// slash
																																		// right at
																																		// the
																																		// beginning!
			// machine
			String mString = s.substring(0, slash);
			Machine m = null;
			try {
				m = Machine.make(mString);
			} catch (InvalidMachineAttributeException e) {
				log.warn("TFN: Unable to build -machine- attribute with the String '"
					+ mString + "'." + e);
			}

			// Port is empty because it is optional specification
			Port p = Port.makeEmpty();
			// PFN checks only for a starting / while the rest can be empty! So it is
			// sufficient to choose whatever String starts at the /... even just the
			// slash itself if that is what is left!!! Should the StFN definition be
			// changed???
			String pfnString = s.substring(slash, s.length());
			PFN pfn = null;
			try {
				pfn = PFN.make(pfnString);
			} catch (InvalidPFNAttributeException e) {
				log.warn("TFN: Unable to build -pfn- attribute with the String '"
					+ pfnString + "'." + e);
			}
			return TFN.make(m, p, pfn);
		} else if ((slash != -1) && (colon > slash)) {
			// colon follows existing slash: the colon does NOT stand as port number
			// delimiter
			// treat it as missing port specification
			// machine
			if (slash == 0)
				throw new InvalidTFNAttributesException(null, null, null); // slash
																																		// right at
																																		// the
																																		// beginning!
			String mString = s.substring(0, slash);
			Machine m = null;
			try {
				m = Machine.make(mString);
			} catch (InvalidMachineAttributeException e) {
				log.warn("TFN: Unable to build -machine- attribute with the String '"
					+ mString + "'." + e);
			}
			// Port is empty because it is optional specification
			Port p = Port.makeEmpty();
			// PFN checks only for a starting / while the rest can be empty! So it is
			// sufficient to choose whatever String starts at the /... even just the
			// slash itself if that is what is left!!! Should the StFN definition be
			// changed???
			String pfnString = s.substring(slash, s.length());
			PFN pfn = null;
			try {
				pfn = PFN.make(pfnString);
			} catch (InvalidPFNAttributeException e) {
				log.warn("TFN: Unable to build -pfn- attribute with the String '"
					+ pfnString + "'." + e);
			}
			return TFN.make(m, p, pfn);
		} else if ((slash != -1) && (colon < slash)) {
			// both machine and port are present
			// machine
			if (colon == 0)
				throw new InvalidTFNAttributesException(null, null, null); // colon
																																		// right at
																																		// the
																																		// beginning!
			String mString = s.substring(0, colon);
			Machine m = null;
			try {
				m = Machine.make(mString);
			} catch (InvalidMachineAttributeException e) {
				log.warn("TFN: Unable to build -machine- attribute with the String '"
					+ mString + "'." + e);
			}
			// port
			if ((colon + 1) == slash)
				throw new InvalidTFNAttributesException(m, null, null); // slash found
																																// right after
																																// colon! There
																																// is no port!
			String pString = s.substring(colon + 1, slash);
			Port p = null;
			try {
				p = Port.make(Integer.parseInt(pString));
			} catch (InvalidPortAttributeException e) {
				log.warn("TFN: Unable to build -port- attribute with the String '"
					+ pString + "'." + e);
			} catch (NumberFormatException e) {
				log
					.warn("TFN: Unable to build -port- attribute with the String (NFE) '"
						+ pString + "'." + e);
			}
			// PFN checks only for a starting / while the rest can be empty! So it is
			// sufficient to choose whatever String starts at the /... even just the
			// slash itself if that is what is left!!! Should the StFN definition be
			// changed???
			String pfnString = s.substring(slash, s.length());
			PFN pfn = null;
			try {
				pfn = PFN.make(pfnString);
			} catch (InvalidPFNAttributeException e) {
				log.warn("TFN: Unable to build -pfn- attribute with the String '"
					+ pfnString + "'." + e);
			}
			return TFN.make(m, p, pfn);
		} else {
			// slash missing! Only colon is present: the TFN does not make sense!
			throw new InvalidTFNAttributesException(null, null, null);
		}
	}

	/**
	 * Method that returns true if this Object is the empty TFN
	 */
	public boolean isEmpty() {

		return empty;
	}

	/**
	 * Method that returns the Machine specified in this TFN. If this is an empty
	 * TFN, then an empty Machine is returned.
	 */
	public Machine machine() {

		if (empty)
			return Machine.makeEmpty();
		return m;
	}

	/**
	 * Method that returns the Port specified in this TFN. If this is an empty
	 * TFN,then an empty Port is returned.
	 */
	public Port port() {

		if (empty)
			return Port.makeEmpty();
		return p;
	}

	/**
	 * Method that returns the PhysicalFileName specified in this TFN. If this is
	 * an empty TFN, then an empty PFN is returned.
	 */
	public PFN pfn() {

		if (empty)
			return PFN.makeEmpty();
		return pfn;
	}

	public String toString() {

		if (empty)
			return "Empty TFN";
		if (m.isEmpty() && p.isEmpty())
			return pfn.toString();
		if ((!m.isEmpty()) && p.isEmpty())
			return m.toString() + pfn.toString();
		return m + ":" + p + pfn;
	}

	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof TFN))
			return false;
		TFN tfno = (TFN) o;
		if (empty && tfno.empty)
			return true;
		return (!empty) && (!tfno.empty) && m.equals(tfno.m) && p.equals(tfno.p)
			&& pfn.equals(tfno.pfn);
	}

	public int hashCode() {

		if (empty)
			return 0;
		int hash = 17;
		hash = 37 * hash + m.hashCode();
		hash = 37 * hash + p.hashCode();
		hash = 37 * hash + pfn.hashCode();
		return hash;
	}

	/*
	 * public static void main(String[] args) { // //Testing empty TFN
	 * System.out.println("Testing empty TFN objects..."); TFN te1 =
	 * TFN.makeEmpty();
	 * System.out.println("te1 is an empty TFN; should see Empty TFN:"
	 * +te1+"; should see hashCode 0:"
	 * +te1.hashCode()+"; it is empty so should see true:"+te1.isEmpty()); TFN te2
	 * = TFN.makeEmpty();
	 * System.out.println("te2 is an empty TFN; should see Empty TFN:"
	 * +te2+"; should see hashCode 0:"
	 * +te2.hashCode()+"; it is empty so should see true:"+te2.isEmpty());
	 * System.out.println("te1.equals(te2) should see true:"+
	 * te1.equals(te2)+"; te2.equals(te1) should see true:"+te2.equals(te1));
	 * System
	 * .out.println("te1 should have all empty parts - Machine: "+te1.machine
	 * ()+", Port: "+te1.port()+", PFN: "+te1.pfn()); // //Testing correct TFN
	 * creation
	 * System.out.println("\n\nTesting correct creation of TFN objects..."); try {
	 * String m1s = "www.egrid.it"; Machine m1 = Machine.make(m1s); int p1i = 1;
	 * Port p1 = Port.make(p1i); String pfn1s = "/home/user1"; PFN pfn1 =
	 * PFN.make(pfn1s); String tfn1s = m1s+":"+p1i+pfn1s;
	 * 
	 * String m2s = "www.infn.it"; Machine m2 = Machine.make(m2s); int p2i = 2;
	 * Port p2 = Port.make(p2i); String pfn2s = "/home/user2"; PFN pfn2 =
	 * PFN.make(pfn2s); String tfn2s = m2s+":"+p2i+pfn2s;
	 * 
	 * String pfn3s = "/home/user3"; PFN pfn3 = PFN.make(pfn3s); String tfn3s =
	 * pfn3s;
	 * 
	 * TFN tfn1 = TFN.make(m1,p1,pfn1); System.out.println("TFN 1 - should see "+
	 * tfn1s
	 * +":"+tfn1+"; hashCode:"+tfn1.hashCode()+"; isEmpty should be false:"+tfn1
	 * .isEmpty()); TFN tfn2 = TFN.make(m2,p2,pfn2);
	 * System.out.println("TFN 2 - should see "+ tfn2s
	 * +":"+tfn2+"; hashCode:"+tfn2
	 * .hashCode()+"; isEmpty should be false:"+tfn2.isEmpty());
	 * System.out.println("tfn1.equals(tfn2) false: "+tfn1.equals(tfn2));
	 * System.out.println("tfn2.equals(tfn1) false: "+tfn2.equals(tfn1));
	 * System.out.println("tfn1.equals(tfn1) true: "+tfn1.equals(tfn1));
	 * System.out.println("tfn1.equals(null) false: "+tfn1.equals(null));
	 * System.out.println("tfn1.equals(Object) false: "+tfn1.equals(new
	 * Object()));
	 * System.out.println("tfn1.equals(empty) false: "+tfn1.equals(TFN.
	 * makeEmpty()));
	 * System.out.println("empty.equals(tfn1) false: "+TFN.makeEmpty
	 * ().equals(tfn1));
	 * System.out.println("tfn1 is "+tfn1+" - Machine: "+tfn1.machine
	 * ()+", Port: "+tfn1.port()+", PFN: "+tfn1.pfn());
	 * 
	 * TFN tfn3 = TFN.makeByPFN(pfn3); System.out.println("\nTFN 3 - should see "+
	 * tfn3s
	 * +":"+tfn3+"; hashCode:"+tfn3.hashCode()+"; isEmpty should be false:"+tfn3
	 * .isEmpty());
	 * System.out.println("tfn3.equals(tfn2) false: "+tfn3.equals(tfn2));
	 * System.out.println("tfn2.equals(tfn3) false: "+tfn2.equals(tfn3));
	 * System.out.println("tfn3.equals(tfn3) true: "+tfn3.equals(tfn3));
	 * System.out.println("tfn3.equals(null) false: "+tfn3.equals(null));
	 * System.out.println("tfn3.equals(Object) false: "+tfn3.equals(new
	 * Object()));
	 * System.out.println("tfn3.equals(empty) false: "+tfn3.equals(TFN.
	 * makeEmpty()));
	 * System.out.println("empty.equals(tfn3) false: "+TFN.makeEmpty
	 * ().equals(tfn3));
	 * System.out.println("tfn3 is "+tfn3+" - Machine: "+tfn3.machine
	 * ()+", Port: "+tfn3.port()+", PFN: "+tfn3.pfn());
	 * 
	 * } catch (Exception e) { System.out.println("Should not see this!"); } //
	 * //TEsting Exception handling
	 * System.out.println("\n\nTesting object creation with invalid attribute..."
	 * ); try { String m1s = "www.egrid.it"; Machine m1 = Machine.make(m1s); int
	 * p1i = 1; Port p1 = Port.make(p1i); String pfn1s = "/home/user1"; PFN pfn1 =
	 * PFN.make(pfn1s); String tfn1s = m1s+":"+p1i+pfn1s; TFN tfn1 =
	 * TFN.make(m1,p1,pfn1); System.out.println("Successfully created "+tfn1);
	 * 
	 * System.out.print("Now attempting creation with null Machine: "); try {
	 * TFN.make(null,p1,pfn1); System.out.println("Should not see this!"); } catch
	 * (InvalidTFNAttributesException e) {
	 * System.out.println(" creation failed as expected. " + e); }
	 * System.out.print("Now attempting creation with empty Machine: "); try {
	 * TFN.make(Machine.makeEmpty(),p1,pfn1);
	 * System.out.println("Should not see this!"); } catch
	 * (InvalidTFNAttributesException e) {
	 * System.out.println(" creation failed as expected. " + e); }
	 * System.out.print("Now attempting creation with null Port: "); try {
	 * TFN.make(m1,null,pfn1); System.out.println("Should not see this!"); } catch
	 * (InvalidTFNAttributesException e) {
	 * System.out.println(" creation failed as expected. " + e); }
	 * System.out.print("Now attempting creation with empty Port: "); try {
	 * TFN.make(m1,Port.makeEmpty(),pfn1);
	 * System.out.println("Should not see this!"); } catch
	 * (InvalidTFNAttributesException e) {
	 * System.out.println(" creation failed as expected. " + e); }
	 * System.out.print("Now attempting creation with null PFN: "); try {
	 * TFN.make(m1,p1,null); System.out.println("Should not see this!"); } catch
	 * (InvalidTFNAttributesException e) {
	 * System.out.println(" creation failed as expected. " + e); }
	 * System.out.print("Now attempting creation with empty PFN: "); try {
	 * TFN.make(m1,p1,PFN.makeEmpty());
	 * System.out.println("Should not see this!"); } catch
	 * (InvalidTFNAttributesException e) {
	 * System.out.println(" creation failed as expected. " + e); }
	 * 
	 * } catch (Exception e) { System.out.println("Should not see this!"); } }
	 */
}
