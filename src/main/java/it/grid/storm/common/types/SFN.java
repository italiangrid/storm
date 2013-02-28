/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.common.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a SFN, that is a Site File Name. It is used as part of
 * a SURL.
 *
 * @author  EGRID ICTP - CNAF Bologna
 * @version 2.0
 * @date    March 2005
 */
public class SFN {

    static private Logger log = LoggerFactory.getLogger(SFN.class);

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
        return new SFN(Machine.makeEmpty(), Port.makeEmpty(), EndPoint.makeEmpty(), StFN.makeEmpty(), true);
    }

    /**
     * Static method that requires a Machine m, the Port p on that Machine, and the StFN stfn.
     * An InvalidSFNAttributesException is thrown if any is null or empty.
     */
    public static SFN makeInSimpleForm(Machine m, Port p, StFN stfn) throws InvalidSFNAttributesException {
        if ((m == null) || (p == null) || (stfn == null) || m.isEmpty() || p.isEmpty() || stfn.isEmpty()) {
            throw new InvalidSFNAttributesException(m, p, stfn);
        }
        return new SFN(m, p, EndPoint.makeEmpty(), stfn, false);
    }

    /**
     * Static method that requires a Machine m, the Port p on that Machine, and the StFN stfn.
     * An InvalidSFNAttributesException is thrown if any is null or empty.
     */
	public static SFN makeInQueryForm(Machine m, Port p, EndPoint ep, StFN stfn)
			throws InvalidSFNAttributesException {

		if((m == null) || (p == null) || (ep == null) || (stfn == null) || m.isEmpty()
			|| p.isEmpty() || (ep.isEmpty()) || stfn.isEmpty())
		{
			throw new InvalidSFNAttributesException(m, p, ep, stfn);
		}
		return new SFN(m, p, ep, stfn, false);
	}

    /**
     * Static method that requires a Machine m, and the StFN stfn.
     * An InvalidSFNAttributesException is thrown if any is null or empty.
     */
    public static SFN makeInSimpleForm(Machine m, StFN stfn) throws InvalidSFNAttributesException {
        if ((m == null) || (stfn == null) || m.isEmpty() || stfn.isEmpty()) {
            throw new InvalidSFNAttributesException(m, null, stfn);
        }
            return new SFN(m,
            			   Port.makeEmpty(),
                           EndPoint.makeEmpty(),
                           stfn,
                           false);
    }

    /**
     * Static method that requires a Machine m, the EndPoint ep, and the StFN stfn.
     * An InvalidSFNAttributesException is thrown if any is null or empty.
     */
    public static SFN makeInQueryForm(Machine m, EndPoint ep, StFN stfn) throws InvalidSFNAttributesException {

		if((m == null) || (stfn == null) || (ep == null) || m.isEmpty() || stfn.isEmpty()
			|| (ep.isEmpty()))
		{
			throw new InvalidSFNAttributesException(m, null, stfn);
		}
			return new SFN(m, Port.makeEmpty(), ep, stfn,
				false);
    }

    /**
     * Static method that returns an SFN from a String representation. If the supplied String is
     * null or malformed, an InvalidSFNAttributesException is thrown.
     * 
	 * @param surlString a surl string without the protocol schema part
	 * @return
	 * @throws ParsingSFNAttributesException
	 * @throws InvalidSFNAttributesException
	 */
	public static SFN makeFromString(String surlString)
			throws ParsingSFNAttributesException, InvalidSFNAttributesException {

		if(surlString == null)
		{
			throw new ParsingSFNAttributesException(surlString, "Supplied SFN String was null!");
		}
		int colon = surlString.indexOf(":"); // first occurence of :
		int slash = surlString.indexOf("/"); // first occurence of /
		/* First occurence of ?SFN= */
		int question = surlString.toUpperCase().indexOf("?SFN="); 
		// TODO MICHELE USER_SURL refactored
		if(colon > 0)
		{
			if(question < 0)
			{
				/*
				 * Supplied string does not contain a colon, and does not
				 * contain question mark! Treat it as optional port
				 * specification, _in_ simple form!
				 */
				if((slash == -1) || (slash == 0))
				{
					/* Slash not found or right at the beginning! */
					throw new ParsingSFNAttributesException(
						surlString,
						"String interpreted as omitting the optional port specification, and as referring to query form;"
							+ " but the first slash was either not found or right at the beginning!");
				}
				return makeFromSimpleFormNoPort(surlString, slash);
			}
			else
			{
				/*
				 * Supplied string does not contain a colon! Treat it as
				 * optional port specification, _in_ query form!
				 */
				if((slash == -1) || (slash == 0) || (slash > question))
				{
					/*
					 * Slash not found or right at the beginning! Or, slash
					 * follows question!
					 */
					throw new ParsingSFNAttributesException(
						surlString,
						"String interpreted as omitting the optional port specification,"
							+ " and as referring to query form; but the first slash was either not found, "
							+ "or right at the beginning, or only followed the question mark!");
				}
				return makeFromQueryFormNoPort(surlString, question, slash);
			}
		}
		else
		{
			if(question < 0)
			{
				/*
				 * Supplied string contains a colon! Treat it as if port _is_
				 * specified, and _not_ in query form!
				 */

				if((colon == 0) || (colon > slash))
				{
					/*
					 * Solon or slash not found or right at the beginning! or,
					 * colon follows slash!
					 */
					throw new ParsingSFNAttributesException(surlString,
						"String interpreted as specifying port, and as not referring to query form; "
							+ "but either the colon is missing, or it follows the first slash!");
				}
				return makeFromSimpleForm(surlString, colon, slash);
			}
			else
			{
				/*
				 * Supplied string contains a port and it also is in query form!
				 */
				if((colon == 0) || (colon > slash) || (slash > question))
				{
					/*
					 * Colon or slash not found or right at the beginning! Or,
					 * colon follows slash! Or slash follows question!
					 */
					throw new ParsingSFNAttributesException(surlString,
						"String interpreted as having the optional port specification, "
							+ "and as referring to query form; but either colon is missing, "
							+ "colon follows first slash, or first slash follows question mark!");
				}
				return makeFromQueryForm(surlString, colon, slash, question);
			}
		}
    }

	/**
	 * Returns an SFN from the received string that is supposed to contain the port and to be in simple form
	 * 
	 * @param surlString
	 * @param colon
	 * @param slash
	 * @param question
	 * @return
	 * @throws ParsingSFNAttributesException
	 * @throws InvalidSFNAttributesException
	 */
	// TODO MICHELE USER_SURL created
	private static SFN makeFromQueryForm(String surlString, int colon, int slash, int question)  throws ParsingSFNAttributesException, InvalidSFNAttributesException {

		String machineString = surlString.substring(0, colon);
        Machine machine = null;
        try {
            machine = Machine.make(machineString);
        } catch (InvalidMachineAttributeException e) {
            log.warn("SFN: Unable to build -machine- attribute with the String '"+machineString+"'."+e);
        }
        if ((colon + 1) == slash) {
            throw new ParsingSFNAttributesException(surlString,
                                                    "String interpreted as specifying the optional port, and as referring to query form; but the port number is missing since the first slash was found right after the colon!"); //slash found right after colon! There is no port!
        }
        String portString = surlString.substring(colon + 1, slash);
        Port port = null;
        try {
            port = Port.make(Integer.parseInt(portString));
        } catch (InvalidPortAttributeException e) {
            log.warn("SFN: Unable to build -port- attribute with the String '"+portString+"'."+e);
        } catch (NumberFormatException e) {
            log.warn("SFN: Unable to build -port- attribute with the String (NFE) '"+portString+"'."+e);
        }
        //EndPoint
        String endpointString = surlString.substring(slash, question);
        EndPoint endpoint = null;
        try {
            endpoint = EndPoint.make(endpointString);
        } catch (InvalidEndPointAttributeException e) {
            log.warn("SFN: Unable to build -endpoint- attribute with the String '"+endpointString+"'."+e);
        }
        //StFN checks only for a starting / while the rest can be empty! So it is sufficient to choose whatever String starts at the /... even just the slash itself if that is what is left!!! Should the StFN definition be changed???
        if (question + 5 >= surlString.length()) {
            throw new ParsingSFNAttributesException(surlString,
                                                    "String interpreted as omitting the optional port specification, and as referring to query form; but theere is nothing left after the question mark!"); //nothing left after question!!!
        }
        String stfnString = surlString.substring(question + 5, surlString.length());
        StFN stfn = null;
        try {
            stfn = StFN.make(stfnString);
        } catch (InvalidStFNAttributeException e) {
            log.warn("SFN: Unable to build -stfn- attribute with the String '"+stfnString+"'."+e);
        }
        return SFN.makeInQueryForm(machine, port, endpoint, stfn);
	}

	/**
	 * 
	 * Returns an SFN from the received string that is supposed to contain the port and to be in simple form
	 * 
	 * @param surlString
	 * @param colon
	 * @param slash
	 * @return
	 * @throws ParsingSFNAttributesException
	 * @throws InvalidSFNAttributesException
	 */
	// TODO MICHELE USER_SURL created
	private static SFN makeFromSimpleForm(String surlString, int colon, int slash) throws ParsingSFNAttributesException, InvalidSFNAttributesException {

		String machineString = surlString.substring(0, colon);
        Machine machine = null;
        try {
            machine = Machine.make(machineString);
        } catch (InvalidMachineAttributeException e) {
            log.warn("SFN: Unable to build -machine- attribute with the String '"+machineString+"'."+e);
        }
        if ((colon + 1) == slash) {
        	/* Slash found right after colon! There is no port!*/
            throw new ParsingSFNAttributesException(surlString,
                                                    "String interpreted as specifying port, and as not referring to query form;" +
                                                    " but the actual port number is missing since the first slash is " +
                                                    "found right after the colon"); 
        }
        String portString = surlString.substring(colon + 1, slash);
        Port port = null;
        try {
            port = Port.make(Integer.parseInt(portString));
        } catch (InvalidPortAttributeException e) {
            log.warn("SFN: Unable to build -port- attribute with the String '"+portString+"'."+e);
        } catch (NumberFormatException e) {
            log.warn("SFN: Unable to build -port- attribute with the String (NFE) '"+portString+"'."+e);
        }
        //StFN checks only for a starting / while the rest can be empty! So it is sufficient to choose whatever String starts at the /... even just the slash itself if that is what is left!!! Should the StFN definition be changed???
        String stfnString = surlString.substring(slash, surlString.length());
        StFN stfn = null;
        try {
            stfn = StFN.make(stfnString);
        } catch (InvalidStFNAttributeException e) {
            log.warn("SFN: Unable to build -stfn- attribute with the String '"+stfnString+"'."+e);
        }
        return SFN.makeInSimpleForm(machine, port, stfn);
	}

	/**
	 * Returns an SFN from the received string that is supposed to not contain the port and to be in query form
	 * 
	 * @param surlString
	 * @param slash 
	 * @param question 
	 * @return
	 * @throws ParsingSFNAttributesException
	 * @throws InvalidSFNAttributesException
	 */
	// TODO MICHELE USER_SURL created
	private static SFN makeFromQueryFormNoPort(String surlString, int question, int slash) throws ParsingSFNAttributesException, InvalidSFNAttributesException {
		
        String machine = surlString.substring(0, slash);
        Machine machineType = null;
        try {
            machineType = Machine.make(machine);
        } catch (InvalidMachineAttributeException e) {
            log.warn("SFN: Unable to build -machine- attribute with the String '"+machine+"'."+e);
        }
        //EndPoint
        String endpoint = surlString.substring(slash, question);
        EndPoint endpointType = null;
        try {
            endpointType = EndPoint.make(endpoint);
        } catch (InvalidEndPointAttributeException e) {
            log.warn("SFN: Unable to build -endpoint- attribute with the String '"+endpoint+"'."+e);
        }
        //StFN checks only for a starting / while the rest can be empty! So it is sufficient to choose whatever String starts at the /... even just the slash itself if that is what is left!!! Should the StFN definition be changed???
        if (question + 5 >= surlString.length()) {
            throw new ParsingSFNAttributesException(surlString,
                                                    "String interpreted as omitting the optional port specification, and as referring to query form; but nothing left after the question mark!"); //nothing left after question!!!
        }
        String stfnString = surlString.substring(question + 5, surlString.length());
        StFN stfn = null;
        try {
            stfn = StFN.make(stfnString);
        } catch (InvalidStFNAttributeException e) {
            log.warn("SFN: Unable to build -stfn- attribute with the String '"+stfnString+"'."+e);
        }
        return SFN.makeInQueryForm(machineType, endpointType, stfn);
	}

	/**
	 * 
	 * Returns an SFN from the received string that is supposed to not contain the port and to be in simple form
	 * @param surlString
	 * @param slash 
	 * @return
	 * @throws ParsingSFNAttributesException
	 * @throws InvalidSFNAttributesException
	 */
	// TODO MICHELE USER_SURL created
	private static SFN makeFromSimpleFormNoPort(String surlString, int slash)
			throws ParsingSFNAttributesException, InvalidSFNAttributesException {

		String machine = surlString.substring(0, slash);
		Machine machineType = null;
		try
		{
			machineType = Machine.make(machine);
		} catch(InvalidMachineAttributeException e)
		{
			log.warn("SFN: Unable to build -machine- attribute with the String '" + machine + "'."
				+ e);
		}
		// StFN checks only for a starting / while the rest can be empty! So it
		// is sufficient to choose whatever String starts at the /... even just
		// the slash itself if that is what is left!!! Should the StFN
		// definition be changed???
		String stfnString = surlString.substring(slash, surlString.length());
		StFN stfn = null;
		try
		{
			stfn = StFN.make(stfnString);
		} catch(InvalidStFNAttributeException e)
		{
			log.warn("SFN: Unable to build -stfn- attribute with the String '" + stfnString + "'."
				+ e);
		}
		return SFN.makeInSimpleForm(machineType, stfn);

	}

	/**
     * Method that returns a Collection of all parent SFNs. The following example clarifies
     * what is meant by parent SFNs.
     *
     * Original SFN:
     *       storage.egrid.it:8444/EGRID/original/data/nyse/file.txt
     *
     * Parent SFNs:
     *       storage.egrid.it:8444/EGRID/original/data/nyse
     *       storage.egrid.it:8444/EGRID/original/data
     *       storage.egrid.it:8444/EGRID/original
     *       storage.egrid.it:8444/EGRID
     *
     * An empty collection is returned if any error occurs during creation of parent SFNs.
     * Likewise if This is an EmptySFN.
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
     * Method that returns the parent SFN. The following example clarifies
     * what is meant by parent SFN.
     *
     * Original SFN:
     *       storage.egrid.it:8444/EGRID/original/data/nyse/file.txt
     *
     * Parent SFN:
     *       storage.egrid.it:8444/EGRID/original/data/nyse
     *
     * An empty SFN is returned if any error occurs during creation.
     * Likewise if This is an EmptySFN.
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
     * Method that returns the Machine specified in this SFN. If this is
     * an empty SFN, then an empty Machine is returned.
     */
    public Machine machine() {
        if (empty) {
            return Machine.makeEmpty();
        }
        return m;
    }

    /**
     * Method that returns the Port specified in this SFN. If this is an
     * empty SFN, then an empty Port is returned.
     */
    public Port port() {
        if (empty) {
            return Port.makeEmpty();
        }
        return p;
    }

    /**
     * Method that returns th EndPoint specified in This SFN. If This is an
     * empty SFN, then an Empty EndPoint is returned; likewise if none was
     * specified at creation time.
     */
    public EndPoint endPoint() {
        if (empty) {
            return EndPoint.makeEmpty();
        }
        return ep;
    }

    /**
     * Method that returns the StFN specified in this SFN. If this is an
     * empty SFN, then an empty StFN is returned.
     */
    public StFN stfn() {
        if (empty) {
            return StFN.makeEmpty();
        }
        return pn;
    }

    @Override
	public String toString() {

		if(empty)
		{
			return "Empty SFN";
		}
		if(ep.isEmpty())
		{
			if(p.isEmpty())
			{
				return m.toString() + pn;
			}
			else
			{
				return m + ":" + p + pn;
			}
		}
		else
		{
    		if(p.isEmpty())
    		{
    			return m.toString() + ep.toString() + "?SFN=" + pn;
    		}
    		else
    		{
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
        return !empty && !sfno.empty && m.equals(sfno.m) && p.equals(sfno.p) && ep.equals(sfno.ep)
                && pn.equals(sfno.pn);
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

    /*
    public static void main(String[] args) {
        //
        //Testing empty SFN
        System.out.println("Testing empty SFN objects...");
        SFN te1 = SFN.makeEmpty(); System.out.println("te1 is an empty SFN; should see Empty SFN:"+te1+"; should see hashCode 0:"+te1.hashCode()+"; it is empty so should see true:"+te1.isEmpty());
        SFN te2 = SFN.makeEmpty(); System.out.println("te2 is an empty SFN; should see Empty SFN:"+te2+"; should see hashCode 0:"+te2.hashCode()+"; it is empty so should see true:"+te2.isEmpty());
        System.out.println("te1.equals(te2) should see true:"+ te1.equals(te2)+"; te2.equals(te1) should see true:"+te2.equals(te1));
        System.out.println("te1 should have all empty parts - Machine: "+te1.machine()+", Port: "+te1.port()+", StFN: "+te1.stfn());
        //
        //Testing correct TFN creation
        System.out.println("\n\nTesting correct creation of TFN objects...");
        try {
            String m1s = "www.egrid.it";
            Machine m1 = Machine.make(m1s);
            int p1i = 1;
            Port p1 = Port.make(p1i);
            String stfn1s = "/home/user1";
            StFN stfn1 = StFN.make(stfn1s);
            String sfn1s = m1s+":"+p1i+stfn1s;

            String m2s = "www.infn.it";
            Machine m2 = Machine.make(m2s);
            int p2i = 2;
            Port p2 = Port.make(p2i);
            String stfn2s = "/home/user2";
            StFN stfn2 = StFN.make(stfn2s);
            String sfn2s = m2s+":"+p2i+stfn2s;

            SFN sfn1 = SFN.make(m1,p1,stfn1); System.out.println("SFN 1 - should see "+ sfn1s +":   "+sfn1+";    hashCode:"+sfn1.hashCode()+";    isEmpty should be false:"+sfn1.isEmpty());
            SFN sfn2 = SFN.make(m2,p2,stfn2); System.out.println("SFN 2 - should see "+ sfn2s +":   "+sfn2+";    hashCode:"+sfn2.hashCode()+";    isEmpty should be false:"+sfn2.isEmpty());
            System.out.println("sfn1.equals(sfn2) false: "+sfn1.equals(sfn2));
            System.out.println("sfn2.equals(sfn1) false: "+sfn2.equals(sfn1));
            System.out.println("sfn1.equals(sfn1) true: "+sfn1.equals(sfn1));
            System.out.println("sfn1.equals(null) false: "+sfn1.equals(null));
            System.out.println("sfn1.equals(Object) false: "+sfn1.equals(new Object()));
            System.out.println("sfn1.equals(empty) false: "+sfn1.equals(SFN.makeEmpty()));
            System.out.println("empty.equals(sfn1) false: "+SFN.makeEmpty().equals(sfn1));
            System.out.println("sfn1 is "+sfn1+" - Machine: "+sfn1.machine()+", Port: "+sfn1.port()+", StFN: "+sfn1.stfn());
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
        //
        //Testing Exception handling
        System.out.println("\n\nTesting object creation with invalid attribute...");
        try {
            String m1s = "www.egrid.it";
            Machine m1 = Machine.make(m1s);
            int p1i = 1;
            Port p1 = Port.make(p1i);
            String stfn1s = "/home/user1";
            StFN stfn1 = StFN.make(stfn1s);
            String sfn1s = m1s+":"+p1i+stfn1s;
            SFN sfn1 = SFN.make(m1,p1,stfn1); System.out.println("Successfully created "+sfn1);

            System.out.print("Now attempting creation with null Machine... ");
            try {
                SFN.make(null,p1,stfn1);
                System.out.println("Should not see this!");
            } catch (InvalidSFNAttributesException e) {
                System.out.println(" OK creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with empty Machine... ");
            try {
                SFN.make(Machine.makeEmpty(),p1,stfn1);
                System.out.println("Should not see this!");
            } catch (InvalidSFNAttributesException e) {
                System.out.println("OK creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with null Port... ");
            try {
                SFN.make(m1,null,stfn1);
                System.out.println("Should not see this!");
            } catch (InvalidSFNAttributesException e) {
                System.out.println("OK creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with empty Port... ");
            try {
                SFN.make(m1,Port.makeEmpty(),stfn1);
                System.out.println("Should not see this!");
            } catch (InvalidSFNAttributesException e) {
                System.out.println("OK creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with null StFN... ");
            try {
                SFN.make(m1,p1,null);
                System.out.println("Should not see this!");
            } catch (InvalidSFNAttributesException e) {
                System.out.println("OK creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with empty StFN: ");
            try {
                SFN.make(m1,p1,StFN.makeEmpty());
                System.out.println("Should not see this!");
            } catch (InvalidSFNAttributesException e) {
                System.out.println("OK creation failed as expected. " + e);
            }

        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
        //
        //Testing creation from String
        System.out.println("\n\nTesting creation from String!");
        String s = "testbed006.cnaf.infn.it:8444/tmp/file.txt";
        System.out.print("Should see: "+s);
        try {
            System.out.println("OK: "+SFN.makeFromString(s));
        } catch (Exception e) {
            System.out.println("Should not see this! "+e);
        }
        s = "testbed006.cnaf.infn.it:8444/t";
        System.out.print("Should see: "+s);
        try {
            System.out.println("OK: "+SFN.makeFromString(s));
        } catch (Exception e) {
            System.out.println("Should not see this! "+e);
        }
        s = "testbed006.cnaf.infn.it:8/t";
        System.out.print("Should see: "+s);
        try {
            System.out.println("OK: "+SFN.makeFromString(s));
        } catch (Exception e) {
            System.out.println("Should not see this! "+e);
        }
        s = "t:8/t";
        System.out.print("Should see: "+s);
        try {
            System.out.println("OK: "+SFN.makeFromString(s));
        } catch (Exception e) {
            System.out.println("Should not see this! "+e);
        }
        s = ":8/t";
        System.out.print("Creation failure: "+s);
        try {
            System.out.println("; should not see this: "+SFN.makeFromString(s));
        } catch (Exception e) {
            System.out.println("; OK failed as expected! "+e);
        }
        s = "s:/t";
        System.out.print("Creation failure: "+s);
        try {
            System.out.println("; should not see this: "+SFN.makeFromString(s));
        } catch (Exception e) {
            System.out.println("; OK failed as expected! "+e);
        }
        s = "s:/";
        System.out.print("Creation failure: "+s);
        try {
            System.out.println("; should not see this: "+SFN.makeFromString(s));
        } catch (Exception e) {
            System.out.println("; OK failed as expected! "+e);
        }
        s = ":/t";
        System.out.print("Creation failure: "+s);
        try {
            System.out.println("; should not see this: "+SFN.makeFromString(s));
        } catch (Exception e) {
            System.out.println("; OK failed as expected! "+e);
        }
        s = ":/";
        System.out.print("Creation failure: "+s);
        try {
            System.out.println("; should not see this: "+SFN.makeFromString(s));
        } catch (Exception e) {
            System.out.println("; OK failed as expected! "+e);
        }
    }*/
}
