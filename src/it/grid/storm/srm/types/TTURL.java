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

package it.grid.storm.srm.types;

import java.util.Map;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.InvalidTFNAttributesException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.TFN;
import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.namespace.NamespaceDirector;

import org.slf4j.Logger;

/**
 * This class represents a TURL, that is a Transfer URL. It is made up of a TransferProtocol and
 * a TFN.
 *
 * @author  EGRID ICTP Trieste - CNAF Bologna
 * @date    March 26th, 2005
 * @version 2.0
 */
public class TTURL {

    private static Logger log = NamespaceDirector.getLogger();
    private TransferProtocol tp;
    private TFN tfn;
    private boolean empty = true; //boolean true if this is an empty object

    public static final String PNAME_TURL = "turl";
    
    private TTURL(TransferProtocol tp, TFN tfn, boolean empty) {
        this.tp = tp;
        this.tfn = tfn;
        this.empty = empty ;
    }

    /**
     * Static method that returns an empty TTURL.
     */
    public static TTURL makeEmpty() {
        return new TTURL(TransferProtocol.EMPTY, TFN.makeEmpty(), true);
    }

    /**
     * Static method that requires the TransferProtocol and the TFN of this TURL: if any is null
     * or empty, an InvalidTURLAttributesException is thrown.
     */
    public static TTURL make(TransferProtocol tp,TFN tfn) throws InvalidTTURLAttributesException {
        if ((tp==null) || (tfn==null) || (tp==TransferProtocol.EMPTY) || (tfn.isEmpty())) {
            throw new InvalidTTURLAttributesException(tp,tfn);
        }
        return new TTURL(tp, tfn, false);
    }

    /**
     * Static factory method that returns a TTURL from a String representation: if it is null
     * or malformed then an InvalidTTURLAttributesException is thrown.
     */
    public static TTURL makeFromString(String s) throws InvalidTTURLAttributesException {
        if (s==null) {
            throw new InvalidTTURLAttributesException(null,null);
        }
        int separator = s.indexOf("://"); //first occurence of ://
        if ((separator==-1) || (separator==0)) {
            throw new InvalidTTURLAttributesException(null,null); //separator not found or right at the beginning!
        }
        String tpString = s.substring(0,separator);
        TransferProtocol tp = null;
        try {
            tp = TransferProtocol.getTransferProtocol(tpString);
        } catch (IllegalArgumentException e) {
            //do nothing - tp remains null and that is fine!
            log.warn("TTURL: Transfer protocol by '"+tpString+"' is empty, but that's fine.");
        }
        if ((separator+3) > (s.length())) {
            throw new InvalidTTURLAttributesException(tp,null); //separator found at the end!
        }
        String tfnString = s.substring(separator+3,s.length());
        TFN tfn = null;
        if (tfnString.startsWith("/")) {
            //TFN without hostname and port
            try {
                tfn = TFN.makeByPFN(PFN.make(tfnString));
            } catch (InvalidTFNAttributesException e) {
                //do nothing - tfn remains null and that is fine: an exception will be thrown later on!
                log.warn("TFN by '"+tfnString+"' is empty, but that's fine.");
            } catch (InvalidPFNAttributeException ex) {
                /** @todo Handle this exception */
                log.error("PFN by '"+tfnString+"' cause an error : "+ex);
            }
        } else {
            //TFN does have host and port!
            try {
                tfn = TFN.makeFromString(tfnString);
            } catch (InvalidTFNAttributesException e) {
                //do nothing - tfn remains null and that is fine: an exception will be thrown later on!
                log.warn("TFN by '"+tfnString+"' is empty, but that's fine.");
            }
        }
        return TTURL.make(tp,tfn);
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
    public void encode(Map<String,String> param, String name) {
        param.put(name, toString());
    }



    @Override
    public String toString() {
        if (empty) {
            return "Empty TTURL";
        }
        return tp+"://"+tfn;
    }

    @Override
    public boolean equals(Object o) {
        if (o==this) {
            return true;
        }
        if (!(o instanceof TTURL)) {
            return false;
        }
        TTURL turlo = (TTURL) o;
        if (empty && turlo.empty) {
            return true;
        }
        return (!empty) && (!turlo.empty) && tp.equals(turlo.tp) && tfn.equals(turlo.tfn);
    }

    @Override
    public int hashCode() {
        if (empty) {
            return 0;
        }
        int hash = 17;
        hash = 37*hash + tp.hashCode();
        hash = 37*hash + tfn.hashCode();
        return hash;
    }
    /*
    public static void main(String[] args) {
        //testing empty TTURL
        System.out.println("Testing empty TTURL objects...");
        TTURL te1 = TTURL.makeEmpty(); System.out.println("te1 is an empty TTURL; should see Empty TTURL:"+te1+"; should see hashCode 0:"+te1.hashCode()+"; it is empty so should see true:"+te1.isEmpty());
        TTURL te2 = TTURL.makeEmpty(); System.out.println("te2 is an empty TTURL; should see Empty TTURL:"+te2+"; should see hashCode 0:"+te2.hashCode()+"; it is empty so should see true:"+te2.isEmpty());
        System.out.println("te1.equals(te2) should see true:"+ te1.equals(te2)+"; te2.equals(te1) should see true:"+te2.equals(te1));
        System.out.println("te1 should have all empty parts - TransferProtocol: "+te1.protocol()+", TransferFileName: "+te1.tfn());
        //
        //Testing correct TFN creation
        System.out.println("\n\nTesting correct creation of TTURL objects...");
        try {
            String m1s = "www.egrid.it";
            Machine m1 = Machine.make(m1s);
            int p1i = 1;
            Port p1 = Port.make(p1i);
            String pfn1s = "/home/user1";
            PFN pfn1 = PFN.make(pfn1s);
            String tfn1s = m1s+":"+p1i+pfn1s;
            TFN tfn1 = TFN.make(m1,p1,pfn1);

            String m2s = "www.infn.it";
            Machine m2 = Machine.make(m2s);
            int p2i = 2;
            Port p2 = Port.make(p2i);
            String pfn2s = "/home/user2";
            PFN pfn2 = PFN.make(pfn2s);
            String tfn2s = m2s+":"+p2i+pfn2s;
            TFN tfn2 = TFN.make(m2,p2,pfn2);

            TTURL tt1 = TTURL.make(TransferProtocol.FILE,tfn1); System.out.println("TTURL 1 - should see "+ TransferProtocol.FILE + "://"+ tfn1 +":     "+tt1+"; hashCode:"+tt1.hashCode()+"; isEmpty should be false:"+tt1.isEmpty());
            TTURL tt2 = TTURL.make(TransferProtocol.GSIFTP,tfn2); System.out.println("TTURL 2 - should see "+ TransferProtocol.GSIFTP + "://"+ tfn2 +":     "+tt2+"; hashCode:"+tt2.hashCode()+"; isEmpty should be false:"+tt2.isEmpty());
            System.out.println("tt1.equals(tt2) false: "+tt1.equals(tt2));
            System.out.println("tt2.equals(tt1) false: "+tt2.equals(tt1));
            System.out.println("tt1.equals(tt1) true: "+tt1.equals(tt1));
            System.out.println("tt1.equals(null) false: "+tt1.equals(null));
            System.out.println("tt1.equals(Object) false: "+tt1.equals(new Object()));
            System.out.println("tt1.equals(empty) false: "+tt1.equals(TTURL.makeEmpty()));
            System.out.println("empty.equals(tt1) false: "+TTURL.makeEmpty().equals(tt1));
            System.out.println("tt1 is "+tt1+" - TransferProtocol: "+tt1.protocol()+", PFN: "+tt1.tfn());

            String pfn3s = "/home/user3";
            PFN pfn3 = PFN.make(pfn3s);
            String tfn3s = pfn3s;
            TFN tfn3 = TFN.makeByPFN(pfn3);
            TTURL tt3 = TTURL.make(TransferProtocol.FILE,tfn3); System.out.println("\nTTURL 3 - should see "+ TransferProtocol.FILE + "://"+ tfn3 +":     "+tt3+"; hashCode:"+tt3.hashCode()+"; isEmpty should be false:"+tt3.isEmpty());
            System.out.println("tt3.equals(tt2) false: "+tt3.equals(tt2));
            System.out.println("tt2.equals(tt3) false: "+tt2.equals(tt3));
            System.out.println("tt3.equals(tt3) true: "+tt3.equals(tt3));
            System.out.println("tt3.equals(null) false: "+tt3.equals(null));
            System.out.println("tt3.equals(Object) false: "+tt3.equals(new Object()));
            System.out.println("tt3.equals(empty) false: "+tt3.equals(TTURL.makeEmpty()));
            System.out.println("empty.equals(tt3) false: "+TTURL.makeEmpty().equals(tt3));
            System.out.println("tt3 is "+tt3+" - TransferProtocol: "+tt3.protocol()+", PFN: "+tt3.tfn());

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
            String pfn1s = "/home/user1";
            PFN pfn1 = PFN.make(pfn1s);
            String tfn1s = m1s+":"+p1i+pfn1s;
            TFN tfn1 = TFN.make(m1,p1,pfn1);
            TTURL tt = TTURL.make(TransferProtocol.FILE,tfn1);
            System.out.println("Successfully created "+tt);

            System.out.print("Now attempting creation with null TransferProtocol: ");
            try {
                TTURL.make(null,tfn1);
                System.out.println("Should not see this!");
            } catch (InvalidTTURLAttributesException e) {
                System.out.println(" creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with empty TransferProtocol: ");
            try {
                TTURL.make(TransferProtocol.EMPTY,tfn1);
                System.out.println("Should not see this!");
            } catch (InvalidTTURLAttributesException e) {
                System.out.println(" creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with null TFN: ");
            try {
                TTURL.make(TransferProtocol.FILE,null);
                System.out.println("Should not see this!");
            } catch (InvalidTTURLAttributesException e) {
                System.out.println(" creation failed as expected. " + e);
            }
            System.out.print("Now attempting creation with empty TFN: ");
            try {
                TTURL.make(TransferProtocol.FILE,TFN.makeEmpty());
                System.out.println("Should not see this!");
            } catch (InvalidTTURLAttributesException e) {
                System.out.println(" creation failed as expected. " + e);
            }
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
        //
        //Testing creation from String
        String s = "gsiftp://testbed006.cnaf.infn.it:8444/tmp/file.txt";
        System.out.print("\n\nTesting TTURL creation from String "+s+"; ");
        try {
            System.out.println("OK: "+TTURL.makeFromString(s));
        } catch (Exception e) {
            System.out.println("Should not see this! "+e);
        }
        s = "gsiftp://testbed006.cnaf.infn.it:8444//tmp/file.txt";
        System.out.print("Testing TTURL creation from String "+s+"; ");
        try {
            System.out.println("OK a pfn with // is _OK_: "+TTURL.makeFromString(s));
        } catch (Exception e) {
            System.out.println("Should not see this! "+e);
        }
    }*/
}
