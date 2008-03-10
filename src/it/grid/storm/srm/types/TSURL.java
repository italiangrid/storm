package it.grid.storm.srm.types;

import it.grid.storm.common.types.*;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;

import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.namespace.*;
import org.apache.commons.logging.Log;

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

    private SiteProtocol sp;
    private SFN sfn;
    private boolean empty = true;
    private static Log log = NamespaceDirector.getLogger();
    
    public static String PNAME_SURL = "surl";
    public static String PNAME_FROMSURL = "fromSURL";
    public static String PNAME_TOSURL = "toSURL";

    private TSURL(SiteProtocol sp, SFN sfn, boolean empty) {
        this.sp = sp;
        this.sfn = sfn;
        this.empty = empty;
    }

    /**
     * Static factory method that returns an empty TSURL.
     */
    public static TSURL makeEmpty() {
	    return new TSURL(SiteProtocol.EMPTY, SFN.makeEmpty(), true );
    }

    /**
     * Static factory method that returns a TSURL and that requires the SiteProtocol
     * and the SFN of this TSURL: if any is null or empty an InvalidTSURLAttributesException
     * is thrown.
     * Check for ".." in Storage File Name for security issues.
     */
    public static TSURL make(SiteProtocol sp, SFN sfn) throws InvalidTSURLAttributesException {
	if ((sp==null) || (sfn==null) || (sp==SiteProtocol.EMPTY) || sfn.isEmpty() ) throw new InvalidTSURLAttributesException(sp,sfn);
        return new TSURL(sp, sfn, false);
    }

    /**
     * Static factory method that returns a TSURL from a String representation: if it is null
     * or malformed then an Invalid TSURLAttributesException is thrown.
     */
    public static TSURL makeFromString(String s) throws InvalidTSURLAttributesException {
        if (s==null) throw new InvalidTSURLAttributesException(null,null);
        int separator = s.indexOf("://"); //first occurence of ://
        if ((separator==-1) || (separator==0)) throw new InvalidTSURLAttributesException(null,null); //separator not found or right at the beginning!
        String spString = s.substring(0,separator);
        SiteProtocol sp = null;
        try {
            sp = SiteProtocol.fromString(spString);
        } catch (IllegalArgumentException e) {
            //do nothing - sp remains null and that is fine!
        }
        if ((separator+3) > (s.length())) throw new InvalidTSURLAttributesException(sp,null); //separator found at the end!
        String sfnString = s.substring(separator+3,s.length());
        SFN sfn = null;
        try {
            sfn = SFN.makeFromString(sfnString);
        } catch (InvalidSFNAttributesException e) {
            //do nothing - sfn remains null and that is fine!
        }
        return TSURL.make(sp,sfn);
    }


    /**
     * Method that returns a Collection of all parent TSURLs. The following example clarifies
     * what is meant by parent TSURLs.
     *
     * Original TSURL:
     *       srm://storage.egrid.it:8444/EGRID/original/data/nyse/file.txt
     *
     * Parent TSURLs:
     *       srm://storage.egrid.it:8444/EGRID/original/data/nyse
     *       srm://storage.egrid.it:8444/EGRID/original/data
     *       srm://storage.egrid.it:8444/EGRID/original
     *       srm://storage.egrid.it:8444/EGRID
     *
     * An empty collection is returned if any error occurs during creation of parent SURLs.
     * Likewise if This is an EmptySURL.
     */
    public Collection getParents() {
        if (empty) return new ArrayList();
        try {
            Collection aux = new ArrayList();
            Collection auxSFN = this.sfn.getParents();
            for (Iterator i = auxSFN.iterator(); i.hasNext(); ) {
                aux.add(TSURL.make(this.sp , (SFN)i.next()));
            }
            return aux;
        } catch (InvalidTSURLAttributesException e) {
            return new ArrayList();
        }
    }

    /**
     * Method that return the parent TSURL. The following example clarifies
     * what is meant by parent TSURL.
     *
     * Original TSURL:
     *       srm://storage.egrid.it:8444/EGRID/original/data/nyse/file.txt
     *
     * Parent TSURL:
     *       srm://storage.egrid.it:8444/EGRID/original/data/nyse
     *
     * An empty TSURL is returned if any error occurs during creation of parent TSURL.
     * Likewise if This is an EmptyTSURL.
     */
    public TSURL getParent() {
        if (empty) return makeEmpty();
        try {
            return TSURL.make(this.sp,this.sfn.getParent());
        } catch (InvalidTSURLAttributesException e) {
            return makeEmpty();
        }
    }

    public boolean isEmpty() {
	    return empty;
    }
    
    /**
     * Returns a string representation of the SURL.
     * @return String
     */
    public String getSURLString() {
        if (empty) {
            return new String();
        }
        return sp + "://" + sfn;
    }

    /**
     * Method that returns the SiteProtocol of this TSURL. If this is empty,
     * then an empty SiteProtocol is returned.
     */
    public SiteProtocol protocol() {
        if (empty) return SiteProtocol.EMPTY;
        return sp;
    }

    /**
     * Method that returns the SFN of this SURL. If this is empty, then
     * an empty SFN is returned.
     */
    public SFN sfn() {
        if (empty) return SFN.makeEmpty();
        return sfn;
    }
    
    
    

    
/*
    /**
     * Method that returns the StoRI, a StoRM object/concept that handles file names!
     * See docs!

    public StoRI stori() {
        return StoRI.make(this);
    }
*/
    /**
     * Static factory method that returns a TSURL from a String representation: if it is null
     * or malformed then an Invalid TSURLAttributesException is thrown.
     */
    public static TSURL makeFromStringValidate(String surlString) throws InvalidTSURLAttributesException {

        log.debug("MAKE SURL in Validating mode: '"+surlString+"'");
        SURL surl;
        try {
            surl = SURL.makeSURLfromString(surlString);
        }
        catch (NamespaceException ex) {
            log.error("SURL '" + surlString + "' is invalid! ");
            throw new InvalidTSURLAttributesException(null, null);
        }
        TSURL result;

        SFN sfn;
        Machine m = null;
        try {
            m = Machine.make(surl.getServiceHost());
            log.debug("Machine built : '"+m+"'");
        }
        catch (InvalidMachineAttributeException ex1) {
            log.error("MACHINE '" + surl.getServiceHost() + "' is invalid! ");
            throw new InvalidTSURLAttributesException(null, null);
        }

        String stfn = surl.getStFN();
        StFN stfnClass = null;
        try {
            stfnClass = StFN.make(stfn);
            log.debug("StFN Class built : '"+stfnClass+"'");
        }
        catch (InvalidStFNAttributeException ex2) {
            log.error("StFN '" + stfn + "' is invalid! ");
            throw new InvalidTSURLAttributesException(null, null);
        }

        int port = surl.getServiceHostPort();
        if (port > -1) {
            Port p = null;
            try {
                p = Port.make(port);
                log.debug("PORT built : '"+p+"'");
            }
            catch (InvalidPortAttributeException ex3) {
                log.error("PORT '" + port + "' is invalid! ");
                throw new InvalidTSURLAttributesException(null, null);
            }
            try {
                sfn = SFN.make(m, p, stfnClass);
                log.debug("SFN built : '"+sfn+"'");
            }
            catch (InvalidSFNAttributesException ex4) {
                log.error("SFN building problem");
                throw new InvalidTSURLAttributesException(null, null);
            }
        }
        else {
            try {
                sfn = SFN.make(m, stfnClass);
                log.debug("SFN built : '"+sfn+"'");
            }
            catch (InvalidSFNAttributesException ex5) {
                log.error("SFN building problem");
                throw new InvalidTSURLAttributesException(null, null);
            }
        }
        result = TSURL.make(SiteProtocol.SRM, sfn);
        return result;
    }

    /**
     *
     * @param host String
     * @param port int
     * @param path String
     * @return TSURL
     */
    public static TSURL make(String host, int port, String path) {
        TSURL result = null;

        return result;
    }

    /**
     *
     * @param s String
     * @return TSURL
     * @throws InvalidTSURLAttributesException
     */
    public static TSURL makeFromString2(String s) throws InvalidTSURLAttributesException {
        TSURL result = null;

        return result;
    }

    
    /**
     * Method that create a TSURL from structure received from FE.
     * @throws InvalidTSURLAttributesException 
     */
    public static TSURL decode(Map inputParam, String name) throws InvalidTSURLAttributesException {
       String surlstring =  (String) inputParam.get(name);
       //return TSURL.makeFromStringValidate(surlstring);
       return TSURL.makeFromString(surlstring);
    }
    /**
     * Encode TSURL for FE communication.
     */
    public void encode(Map param, String name) {
        param.put(name, this.toString());
     }





    public String toString() {
        if (empty) return "Empty TSURL";
        return sp+"://"+sfn;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof TSURL)) return false;
        TSURL surlo = (TSURL) o;
        if (empty && surlo.empty) return true;
        return (!empty) && (!surlo.empty) && sp.equals(surlo.sp) && sfn.equals(surlo.sfn);
    }

    public int hashCode() {
        if (empty) return 0;
        int hash = 17;
        hash = 37*hash + sp.hashCode();
        hash = 37*hash + sfn.hashCode();
        return hash;
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
