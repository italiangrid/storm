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

/**
 * This class represents a Space Token
 *
 * @author  EGRID ICTP Trieste / CNAF Bologna
 * @date    March 23rd, 2005
 * @version 2.0
 */
package it.grid.storm.srm.types;

import it.grid.storm.common.types.PFN;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import it.grid.storm.common.*;

public class TSpaceToken implements Serializable {
    
    public static String PNAME_SPACETOKEN = "spaceToken";

    private String token=""; //string representing the token!
    private boolean empty=true;

    static private TSpaceToken emptyToken; //only instance of empty spaceToken

    private TSpaceToken(String token, boolean empty) {
        this.token = token;
        this.empty = empty;
    }

    /**
     * Factory method thta requires a String; if it is null, an
     * InvalidTSpaceTokenAttributeException is thrown.
     */
    public static TSpaceToken make(String s) throws InvalidTSpaceTokenAttributesException {
        if (s==null) throw new InvalidTSpaceTokenAttributesException();
        return new TSpaceToken(s,false);
    }

    /**
     * Factory method that requires a String; if it is null, an
     * InvalidTSpaceTokenAttributeException is thrown.
     */
    public static TSpaceToken makeFromPFN(PFN PFNToken) throws InvalidTSpaceTokenAttributesException {
        if (PFNToken==null) throw new InvalidTSpaceTokenAttributesException();
        return new TSpaceToken(PFNToken.getValue(),false);
    }

    /**
     * Factory method that returns an Empty TSpaceToken
     */
    public static TSpaceToken makeEmpty() {
        if (emptyToken!=null) return emptyToken;
        emptyToken = new TSpaceToken("",true);
        return emptyToken;
    }


    /**
     * Retrieve an unique Space Token
     *
     * @return TSpaceToken
     */
    public static TSpaceToken makeGUID_Token() throws InvalidTSpaceTokenAttributesException {
      TSpaceToken result = null;
      String guid = (new it.grid.storm.common.GUID()).toString();
      result = result.make(guid);
      return result;
    }


    public String getValue()
    {
      return token;
    }

    public boolean isEmpty() {
        return empty;
    }

    public String toString() {
        if (empty) return "Empty";
        return token;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof TSpaceToken)) return false;
        TSpaceToken st = (TSpaceToken) o;
        if ((empty) && (st.empty)) return true;
        return (!empty) && (!st.empty) && token.equals(st.token);
    }

    public int hashCode() {
        if (empty) return 0;
        int hash = 17;
        return 37*hash + token.hashCode();
    }
    
    /**
     * Decode method, used to represnts this object into a structured
     * paramter for FE communication.
     * @param vector
     */
    public final static TSpaceToken decode (Map param, String name) {
        String tokenString = (String)param.get(name);
        TSpaceToken spaceToken = TSpaceToken.makeEmpty();
        if (tokenString!=null) {
            //Creation of srm TSpaceToken
            try {
                spaceToken = TSpaceToken.make(tokenString);
            }
            catch (InvalidTSpaceTokenAttributesException e) {
                ;//log.warn("Error creating TSpaceToken:"+e);
            }
        }
        return spaceToken;
    }
        
    /**
     * Encode method, used to represnts this object into a structured
     * paramter for FE communication.
     * @param vector
     */
    public void encode(List list) {
        list.add(this.toString());
     }
    
    public void encode(Map outputParam, String fieldName) {
        outputParam.put(fieldName, (String) token);
    }

/*    public static void main(String[] args) {
        //Testing constructors
        System.out.println("Testing Constructor...");
        try {
            String s = "/home/user/file.txt";
            PFN pfn = PFN.make(s);
            TSpaceToken t1 = new TSpaceToken(s);
            TSpaceToken t2 = new TSpaceToken(pfn);
            TSpaceToken t3 = new TSpaceToken("qwerty");
            System.out.println("t1 TSpaceToken: "+t1+"; hashCode:"+t1.hashCode()+"; getValue:"+t1.getValue());
            System.out.println("t2 TSpaceToken: "+t2+"; hashCode:"+t2.hashCode()+"; getValue:"+t2.getValue());
            System.out.println("t3 TSpaceToken: "+t3+"; hashCode:"+t3.hashCode()+"; getValue:"+t3.getValue());
            System.out.println("t1.equals(t3) false: "+t1.equals(t3));
            System.out.println("t3.equals(t1) false: "+t3.equals(t1));
            System.out.println("t1.equals(t1) true: "+t1.equals(t1));
            System.out.println("t1.equals(null) false: "+t1.equals(null));
            System.out.println("t1.equals(Object) false: "+t1.equals(new Object()));
            System.out.println("t1.equals(t2) true: "+t1.equals(t2));
            System.out.println("t2.equals(t1) true: "+t2.equals(t1));
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
        //TEsting Exception throwing
        System.out.println("\n\nTesting Exception throwing");
        System.out.print("Creating with null String... ");
        try {
            String ss=null;
            new TSpaceToken(ss);
            System.out.println("Should not see this!");
        } catch (InvalidTSpaceTokenAttributesException e) {
            System.out.println("OK: creation failed as expected. "+e);
        }
        System.out.print("Creating with null PFN... ");
        try {
            PFN ss=null;
            new TSpaceToken(ss);
            System.out.println("Should not see this!");
        } catch (InvalidTSpaceTokenAttributesException e) {
            System.out.println("OK: creation failed as expected. "+e);
        }
    }*/
}
