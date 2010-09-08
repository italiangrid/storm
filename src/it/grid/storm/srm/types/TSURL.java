package it.grid.storm.srm.types;

import it.grid.storm.common.types.EndPoint;
import it.grid.storm.common.types.InvalidEndPointAttributeException;
import it.grid.storm.common.types.InvalidMachineAttributeException;
import it.grid.storm.common.types.InvalidPortAttributeException;
import it.grid.storm.common.types.InvalidSFNAttributesException;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.Machine;
import it.grid.storm.common.types.Port;
import it.grid.storm.common.types.SFN;
import it.grid.storm.common.types.SiteProtocol;
import it.grid.storm.common.types.StFN;
import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.naming.SURL;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

/**
 * This class represents a TSURL, that is a Site URL. It is made up of a SiteProtocol and
 * a SFN.
 *
 * @author  Ezio Corso - Magnoni Luca
 * @author  EGRID ICTP Trieste / CNAF INFN Bologna
 * @date    Avril, 2005
 * @version 2.0
 */
public class TSURL {

	
    private static Logger log = NamespaceDirector.getLogger();
	/**
	 * The surl as provided by User
	 */
	private final String rawSurl;
    private final SiteProtocol sp;
    private final SFN sfn;
    private String normalizedStFN = null;
    private int uniqueID = 0;
    
    private boolean empty = true;

    public static String PNAME_SURL = "surl";
    public static String PNAME_FROMSURL = "fromSURL";
    public static String PNAME_TOSURL = "toSURL";

    private static ArrayList<TSURL> tsurlManaged = new ArrayList<TSURL>();
    private static LinkedList<Port> defaultPorts = new LinkedList<Port>();
    
	static
	{
		// Lazy initialization from Configuration
		if(tsurlManaged.isEmpty())
		{
			// This is the first call
			TSURL checkTSURL;
			String[] surlValid = Configuration.getInstance().getManagedSURLs();
			for(String checkSurl : surlValid)
			{
				// Building TSURL
				try
				{//TODO MICHELE USER_SURL FIXME!!! here the generated SURL has the port added also if it is blank!!
					checkTSURL = TSURL.makeFromStringWellFormed(checkSurl);
					tsurlManaged.add(checkTSURL);
					log.debug("### SURL Managed : " + checkTSURL);
				} catch(InvalidTSURLAttributesException e)
				{
					log.error("Unable to build a TSURL : '" + checkSurl + "'");
				}
			}
		}
		if(defaultPorts.isEmpty())
		{
			// This is the first call
			Integer[] ports = Configuration.getInstance().getManagedSurlDefaultPorts();
			for(Integer portInteger : ports)
			{
				// Building Port
				try
				{

					defaultPorts.add(Port.make(portInteger.intValue()));
					log.debug("### Default SURL port : " + defaultPorts.getLast());
				} catch(InvalidPortAttributeException e)
				{
					log.error("Unable to build a Port : '" + portInteger + "'" + e);
				}
			}
		}
	}
    
    
    private TSURL(SiteProtocol sp, SFN sfn, String rawSurl, boolean empty) {
        this.sp = sp;
        this.sfn = sfn;
        this.rawSurl = rawSurl;
        this.empty = empty;
    }

    /**
     * Method that create a TSURL from structure received from FE.
     * @throws InvalidTSURLAttributesException
     */
    public static TSURL decode(Map<String, String> inputParam, String name) throws InvalidTSURLAttributesException {
        String surlstring = inputParam.get(name);
        return TSURL.makeFromStringWellFormed(surlstring);
    }


    
    /**
     * Build a TSURL by extracting the content of the received SURL object and storing the received raw surl string 
     * 
     * @param surl
     * @param rawSurl
     * @return
     * @throws InvalidTSURLAttributesException
     */
//  TODO MICHELE USER_SURL as it is now we don't need to pass throught the SURL object, maybe
    //it will be useful for the equals method and storm normal form
    public static TSURL getWellFormed(SURL surl, String rawSurl) throws InvalidTSURLAttributesException {
        TSURL result;
        SFN sfn;
//        TODO MICHELE USER_SURL 
        Machine machine = null;
        try {
            machine = Machine.make(surl.getServiceHostname());
            log.debug("Machine built : '" + machine + "'");
        } catch (InvalidMachineAttributeException ex1) {
            log.error("MACHINE '" + surl.getServiceHostname() + "' is invalid! ");
            throw new InvalidTSURLAttributesException(null, null);
        }

        String stfn = surl.getStFN();
        StFN stfnClass = null;
        try {
            stfnClass = StFN.make(stfn);
            log.debug("StFN Class built : '" + stfnClass + "'");
        } catch (InvalidStFNAttributeException ex2) {
            log.error("StFN '" + stfn + "' is invalid! ");
            throw new InvalidTSURLAttributesException(null, null);
            try {
                sfn = SFN.make(m, p, stfnClass);
                log.debug("SFN built : '" + sfn + "'");
            } catch (InvalidSFNAttributesException ex4) {
                log.error("SFN building problem");
                throw new InvalidTSURLAttributesException(null, null);
            }
        } else {
            try {
                sfn = SFN.make(m, stfnClass);
                log.debug("SFN built : '" + sfn + "'");
            } catch (InvalidSFNAttributesException ex5) {
                log.error("SFN building problem");
                throw new InvalidTSURLAttributesException(null, null);
            }
        }
        int portInt = surl.getServiceHostPort();
        
		EndPoint serviceEndpoint = null;
		if(surl.isQueriedFormSURL())
		{
			String serviceEndPointString = surl.getServiceEndPoint();
			try
			{
				serviceEndpoint = EndPoint.make(serviceEndPointString);
				log.debug("EndPoint built : '" + serviceEndpoint + "'");
			} catch(InvalidEndPointAttributeException e)
			{
				log.error("EndPoint '" + serviceEndpoint + "' is invalid! " + e);
				throw new InvalidTSURLAttributesException(null, null);
			}
		}

		if(portInt > -1)
		{
			Port port = null;
			try
			{
				port = Port.make(portInt);
				log.debug("PORT built : '" + port + "'");
			} catch(InvalidPortAttributeException ex3)
			{
				log.error("PORT '" + portInt + "' is invalid! ");
				throw new InvalidTSURLAttributesException(null, null);
			}
			try
			{
				if(serviceEndpoint == null)
				{
					sfn = SFN.makeInSimpleForm(machine, port, stfnClass);
				}
				else
				{
					sfn = SFN.makeInQueryForm(machine, port, serviceEndpoint, stfnClass);
				}
    
    
				log.debug("SFN built : '" + sfn + "'");
			} catch(InvalidSFNAttributesException ex4)
			{
				log.error("SFN building problem");
				throw new InvalidTSURLAttributesException(null, null);
			}
		}
		else
		{
			try
			{
				if(serviceEndpoint == null)
				{
					sfn = SFN.makeInSimpleForm(machine, stfnClass);
				}
				else
				{
					sfn = SFN.makeInQueryForm(machine, serviceEndpoint, stfnClass);
				}
				log.debug("SFN built : '" + sfn + "'");
			} catch(InvalidSFNAttributesException ex5)
			{
				log.error("SFN building problem");
				throw new InvalidTSURLAttributesException(null, null);
			}
		}
		result = TSURL.make(SiteProtocol.SRM, sfn, rawSurl);
		return result;
	}
    
    
    /**
     * Static factory method that returns a TSURL and that requires the SiteProtocol
     * and the SFN of this TSURL: if any is null or empty an InvalidTSURLAttributesException
     * is thrown.
     * Check for ".." in Storage File Name for security issues.
     */
    private static TSURL make(SiteProtocol sp, SFN sfn, String userSurl) throws InvalidTSURLAttributesException {
        if ((sp == null) || (sfn == null) || (sp == SiteProtocol.EMPTY) || sfn.isEmpty()) {
            throw new InvalidTSURLAttributesException(sp, sfn);
        }
        return new TSURL(sp, sfn, userSurl, false);
    }



    /**
     * Static factory method that returns an empty TSURL.
     */
    public static TSURL makeEmpty() {
        return new TSURL(SiteProtocol.EMPTY, SFN.makeEmpty(), "", true);
    }

    /**
     * Static factory method that returns a TSURL from a String representation: if it is null
     * or malformed then an Invalid TSURLAttributesException is thrown.
     */
    public static TSURL makeFromStringWellFormed(String surlString) throws InvalidTSURLAttributesException {

        TSURL result = null;
        if(surlString == null)
		{
			throw new InvalidTSURLAttributesException(null, null);
		}
		// first occurrences of ://
		int separator = surlString.indexOf("://"); 
		if((separator == -1) || (separator == 0))
		{
			// separator not found or right at the beginning!
			throw new InvalidTSURLAttributesException(null, null); 
		}
		String spString = surlString.substring(0, separator);
		SiteProtocol sp = null;
		try
		{
			sp = SiteProtocol.fromString(spString);
		} catch(IllegalArgumentException e)
		{
			// do nothing - sp remains null and that is fine!
			log.warn("TSURL: Site protocol by '" + spString + "' is empty, but that's fine.");
		}
		if((separator + 3) > (surlString.length()))
		{
			// separator found at the end!
			throw new InvalidTSURLAttributesException(sp, null); 
		}
        log.debug("MAKE SURL : '" + surlString + "'");
        SURL surl;
        try {
            surl = SURL.makeSURLfromString(surlString);
        } catch (NamespaceException ex) {
            log.error("SURL '" + surlString + "' is invalid! ");
            throw new InvalidTSURLAttributesException(null, null);
        }
        result = getWellFormed(surl, surlString);
        
        return result;
    }
    
    
    /**
     * Static factory method that returns a TSURL from a String representation: if it is null
     * or malformed then an Invalid TSURLAttributesException is thrown.
     */
    public static TSURL makeFromStringWellFormed(String surlString) throws InvalidTSURLAttributesException {

        TSURL result = null;

        log.debug("MAKE SURL in Validating mode: '" + surlString + "'");
//      TODO MICHELE USER_SURL
        TSURL tsurl = makeFromStringWellFormed(surlString);
        if (!(isValid(tsurl))) {
            log.warn("The SURL '"+tsurl+"' is not managed by this instance of StoRM");
            throw new InvalidTSURLAttributesException(tsurl.sp, tsurl.sfn());            
        }
        return tsurl;
    }

    
    
    /**
     * Auxiliary method that returns true if the supplied TSURL corresponds to
     * some managed SURL as declared in Configuration. 
     * 
     */
    public static boolean isValid(TSURL surl) {
//      TODO MICHELE USER_SURL moved code from isValid to isManaged to support in future managedSurl separation by VO
    	return isManaged(surl, TSURL.tsurlManaged);
    }
    
    public static boolean isManaged(TSURL surl, List<TSURL> managedSurls)
    {
    	boolean result = false;
        for (TSURL tsurlReference : managedSurls) {
            if (isSURLManaged(surl, tsurlReference)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    /**
     * A SURL is managed by a managed SURL if their hosts are the same and if the comingSURL specifies a port
     * this port is the same as the one specified on the managed SURL or, if the managed SURL doesn't specifies
     * a port this port is listed in the default ports  
     * 
     * @param comingSURL
     * @param managedSURL
     * @return
     */
    private static boolean isSURLManaged(TSURL comingSURL, TSURL managedSURL) {
        boolean result = false;
    //  TODO MICHELE USER_SURL 
        String serviceHost = comingSURL.sfn().machine().toString();
        String expectedServiceHost = managedSURL.sfn().machine().toString();
        log.debug("SURL VALID [ coming-service-host = '" + serviceHost + "' expected : '" + expectedServiceHost + "'");

        if(comingSURL.sfn().port().isEmpty())
        {
        	//no port, check the host
        	if (serviceHost.equalsIgnoreCase(expectedServiceHost)) 
        	{
                result = true;
            }
        }
        else
        {
        	// we got port
        	if(!managedSURL.sfn().port().isEmpty())
        	{
        		//both got port, normal check
        		 int expectedServicePort = managedSURL.sfn().port().toInt();
        		 int port = comingSURL.sfn().port().toInt();
        		 log.debug("SURL VALID [ coming-service-port = '" + port + "' expected : '" + expectedServicePort + "'");
                if ((serviceHost.equalsIgnoreCase(expectedServiceHost)) && (expectedServicePort == port)) {
                    result = true;
                }
        	}
        	else
        	{
        		//surl with port, managed surl without,  check the default ports and the host
        		int port = comingSURL.sfn().port().toInt();
				try
				{
					Port comingPort = Port.make(port);
					if ((serviceHost.equalsIgnoreCase(expectedServiceHost)) && (defaultPorts.contains(comingPort))) {
	                    result = true;
	                }
				} catch(InvalidPortAttributeException e)
				{
					  log.warn("The SURL '"+comingSURL+"' has ha not valid port. Unable to create the Port : " + e);
					  
				} 
        	}
        }
        return result;
    }


	/**
     * Encode TSURL for FE communication.
     */
    public void encode(Map<String,String> param, String name) {
        param.put(name, toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TSURL)) {
            return false;
        }
        TSURL surlo = (TSURL) o;
        if (empty && surlo.empty) {
            return true;
        }
    //  TODO MICHELE USER_SURL moved equality on surlManaged verification and on stfn equality
		return (!empty) && (!surlo.empty) && sp.equals(surlo.sp)
			&& this.sfn.stfn().equals(surlo.sfn.stfn()) && isManaged(this, TSURL.tsurlManaged)
			&& isManaged(surlo, TSURL.tsurlManaged);
    }

    /**
     * Returns a string representation of the SURL.
     * @return String
     */
    public String getSURLString() {
        if (empty) {
            return "";
        }
        return sp + "://" + sfn;
    }

    @Override
    public int hashCode() {
        if (empty) {
            return 0;
        }
        int hash = 17;
        hash = 37 * hash + sp.hashCode();
        hash = 37 * hash + sfn.hashCode();
        return hash;
    }

    public boolean isEmpty() {
        return empty;
    }

    /**
     * Method that returns the SiteProtocol of this TSURL. If this is empty,
     * then an empty SiteProtocol is returned.
     */
    public SiteProtocol protocol() {
        if (empty) {
            return SiteProtocol.EMPTY;
        }
        return sp;
    }
    
	/**
	 * @return the rawSurl
	 */
	public String rawSurl() {

		return rawSurl;
	}

    /**
     * Method that returns the SFN of this SURL. If this is empty, then
     * an empty SFN is returned.
     */
    public SFN sfn() {
        if (empty) {
            return SFN.makeEmpty();
        }
        return sfn;
    }

//  TODO MICHELE USER_SURL
    /**
     * @return
     */
    public String normalizedStFN()
    {
    	if(this.normalizedStFN == null)
    	{
    		this.normalizedStFN = this.sfn.stfn().toString();
    	}
    	return this.normalizedStFN;
    }
    
    /**
     * @param normalizedStFN the normalizedStFN to set
     */
    public void setNormalizedStFN(String normalizedStFN)
    {
   		this.normalizedStFN = normalizedStFN;
    }
    
    /**
	 * @param uniqueID the uniqueID to set
	 */
//  TODO MICHELE USER_SURL
	public void setUniqueID(int uniqueID) 
	{
		this.uniqueID = uniqueID;
	}
    
//  TODO MICHELE USER_SURL
    /**
     * @return
     */
    public int uniqueId()
    {
    	if(this.uniqueID == 0)
    	{
    		this.uniqueID = this.sfn.stfn().hashCode();
    	}
    	return this.uniqueID;
    }

    
    @Override
    public String toString() {
        if (empty) {
            return "Empty TSURL";
        }
        return sp + "://" + sfn;
    }



    /*
    public static void main(String[] args) {
        //testing empty TTURL
        System.out.println("Testing empty TSURL objects...");
        TSURL se1 = TSURL.makeEmpty(); System.out.println("se1 is an empty TSURL; should see Empty TSURL:"+se1+"; should see hashCode 0:"+se1.hashCode()+"; it is empty so should see true:"+se1.isEmpty());
        TSURL se2 = TSURL.makeEmpty(); System.out.println("se2 is an empty TSURL; should see Empty TSURL:"+se2+"; should see hashCode 0:"+se2.hashCode()+"; it is empty so should see true:"+se2.isEmpty());
        System.out.println("se1.equals(se2) should see true:"+ se1.equals(se2)+"; se2.equals(se1) should see true:"+se2.equals(se1));
        System.out.println("se1.equals(null) should be false:"+se1.equals(null)+"; se1.equals(Object) should be false: "+se1.equals(new Object()));
        System.out.println("se1 should have all empty parts - SiteProtocol: "+se1.protocol()+", StorageFileName: "+se1.sfn());
        //
        //Testing correct TSURL creation
        System.out.println("\n\nTesting correct creation of TSURL objects...");
        try {
            String m1s = "www.egrid.it";
            Machine m1 = Machine.make(m1s);
            int p1i = 1;
            Port p1 = Port.make(p1i);
            String stfn1s = "/home/user1";
            StFN stfn1 = StFN.make(stfn1s);
            String sfn1s = m1s+":"+p1i+stfn1s;
            SFN sfn1 = SFN.make(m1,p1,stfn1);

            String m2s = "www.infn.it";
            Machine m2 = Machine.make(m2s);
            int p2i = 2;
            Port p2 = Port.make(p2i);
            String stfn2s = "/home/user2";
            StFN stfn2 = StFN.make(stfn2s);
            String sfn2s = m2s+":"+p2i+stfn2s;
            SFN sfn2 = SFN.make(m2,p2,stfn2);

            TSURL ts1 = TSURL.make(SiteProtocol.SRM,sfn1); System.out.println("TSURL 1 - should see "+ SiteProtocol.SRM + "://"+ sfn1 + ":     "+ts1+"; hashCode:    "+ts1.hashCode()+"; isEmpty should be false:   "+ts1.isEmpty());
            TSURL ts2 = TSURL.make(SiteProtocol.SRM,sfn2); System.out.println("TSURL 2 - should see "+ SiteProtocol.SRM + "://"+ sfn2 + ":     "+ts2+"; hashCode:    "+ts2.hashCode()+"; isEmpty should be false:   "+ts2.isEmpty());
            System.out.println("ts1.equals(ts2) false: "+ts1.equals(ts2));
            System.out.println("ts2.equals(ts1) false: "+ts2.equals(ts1));
            System.out.println("ts1.equals(ts1) true: "+ts1.equals(ts1));
            System.out.println("ts1.equals(null) false: "+ts1.equals(null));
            System.out.println("ts1.equals(Object) false: "+ts1.equals(new Object()));
            System.out.println("ts1.equals(empty) false: "+ts1.equals(TSURL.makeEmpty()));
            System.out.println("empty.equals(ts1) false: "+TSURL.makeEmpty().equals(ts1));
            System.out.println("ts1 is "+ts1+" - TransferProtocol: "+ts1.protocol()+", SFN: "+ts1.sfn());
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
        //
        //TEsting Exception handling
        System.out.println("\n\nTesting object creation with invalid attribute...");
        try {
            String m1s = "www.egrid.it";
            Machine m1 = Machine.make(m1s);
            int p1i = 1;
            Port p1 = Port.make(p1i);
            String stfn1s = "/home/user1";
            StFN stfn1 = StFN.make(stfn1s);
            String sfn1s = m1s+":"+p1i+stfn1s;
            SFN sfn1 = SFN.make(m1,p1,stfn1);
            TSURL ss = TSURL.make(SiteProtocol.SRM,sfn1);
            System.out.println("Successfully created "+ss);

            System.out.print("Now attempting creation with null SiteProtocol... ");
            try {
                TSURL.make(null,sfn1);
                System.out.println("Should not see this!");
            } catch (InvalidTSURLAttributesException e) {
                System.out.println("OK creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with empty SiteProtocol... ");
            try {
                TSURL.make(SiteProtocol.EMPTY,sfn1);
                System.out.println("Should not see this!");
            } catch (InvalidTSURLAttributesException e) {
                System.out.println("OK creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with null SFN... ");
            try {
                TSURL.make(SiteProtocol.SRM,null);
                System.out.println("Should not see this!");
            } catch (InvalidTSURLAttributesException e) {
                System.out.println("OK creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with empty SFN... ");
            try {
                TSURL.make(SiteProtocol.SRM,SFN.makeEmpty());
                System.out.println("Should not see this!");
            } catch (InvalidTSURLAttributesException e) {
                System.out.println("OK creation failed as expected. " + e);
            }
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
        //
        //Testing creation from String
        String s = "srm://testbed006.cnaf.infn.it:8444/tmp/file.txt";
        System.out.print("\n\nTesting TSURL creation from String "+s+"; ");
        try {
            System.out.println("OK: "+TSURL.makeFromString(s));
        } catch (Exception e) {
            System.out.println("Should not see this! "+e);
        }
        s = "srm://testbed006.cnaf.infn.it:8444//tmp/file.txt";
        System.out.print("Testing TSURL creation from String "+s+"; ");
        try {
            System.out.println("OK! StFN with // is _OK_: "+TSURL.makeFromString(s));
        } catch (Exception e) {
            System.out.println("Should not see this! "+e);
        }
    }*/
}
