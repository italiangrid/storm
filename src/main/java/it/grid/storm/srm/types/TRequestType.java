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

/**
 * This class represents the ReqType of an SRM request. It is a simple application of
 * the TypeSafe Enum Pattern.
 *
 * @author  EGRID ICTP Trieste / CNAF Bologna
 * @date    March 18th, 2005
 * @version 3.0
 */
//public class TRequestType {
public enum TRequestType {
    
//    private static HashMap<String, TRequestType> m = new HashMap<String, TRequestType>(5);
//
//    public static TRequestType PREPARE_TO_GET = new TRequestType("PrepareToGet") {
//        public int hashCode() {
//            return 1;
//        }
//    };
//
//    public static TRequestType PREPARE_TO_PUT = new TRequestType("PrepareToPut") {
//        public int hashCode() {
//            return 2;
//        }
//    };
//
//    public static TRequestType COPY = new	TRequestType("Copy") {
//        public int hashCode() {
//            return 3;
//        }
//    };
//
//    public static TRequestType BRING_ON_LINE = new TRequestType("BringOnLine") {
//        public int hashCode() {
//            return 4;
//        }
//    };
//
//    public static TRequestType EMPTY = new TRequestType("Empty") {
//        public int hashCode() {
//            return 0;
//        }
//    };
//
//    private TRequestType(String reqType) {
//        this.reqType = reqType;
//        m.put(reqType,this);
//    }

    
    PREPARE_TO_GET("PrepareToGet"), PREPARE_TO_PUT("PrepareToPut"), COPY("Copy"), BRING_ON_LINE("BringOnLine"), 
    EMPTY("Empty"), UNKNOWN("Unknown");
    
    private final String value;
    
    private TRequestType(String value)
    {
        this.value = value;
    }
    
	public String getValue() {
		return value;
	}

    /**
     * Facility method that returns a TRequestType object given
     * its String representation. If no TRequestType is found for
     * the given String, an IllegalArgumentException is thrown.
     */
	public static TRequestType getTRequestType(String type) throws IllegalArgumentException {
	    for(TRequestType requestType : TRequestType.values())
	    {
	        if(requestType.getValue().equals(type))
	        {
	            return requestType;
	        }
	    }
        return UNKNOWN;
	}

	 public boolean isEmpty()
    {
        return this.equals(EMPTY);
    }

    public String toString() {
        return value;
    }

/*    public static void main(String[] args) {
        //Testing type-safe enum objects, toString, and ceration from String.
        System.out.println("Testing Objects and getTRequestType...");
        try {
            String s1 = "PrepareToGet"; System.out.println("Should see "+s1+": "+TRequestType.PREPARE_TO_GET.getTRequestType(s1)+"; hashCode="+TRequestType.PREPARE_TO_GET.hashCode());
            String s2 = "PrepareToPut"; System.out.println("Should see "+s2+": "+TRequestType.PREPARE_TO_PUT.getTRequestType(s2) + "; hashCode="+TRequestType.PREPARE_TO_PUT.hashCode());
            String s3 = "Copy"; System.out.println("Should see "+s3+": "+TRequestType.COPY.getTRequestType(s3) + "; hashCode="+TRequestType.COPY.hashCode());
            String s4 = "Empty"; System.out.println("Should see "+s4+": "+TRequestType.EMPTY.getTRequestType(s4) + "; hashCode="+TRequestType.EMPTY.hashCode());
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
        //
        //Testing creation from malformed String
        System.out.println("\n\nTesting creation from a wrong String representation...");
        String sa = "wrongwrong"; System.out.print("String: "+sa);
        try {
            TRequestType.getTRequestType(sa);
            System.out.println("Should not see this!");
        } catch (IllegalArgumentException e) {
            System.out.println("; OK creation failed as expected - "+e);
        }
        String sb = null; System.out.print("String: "+sb);
        try {
            TRequestType.getTRequestType(sb);
            System.out.println("Should not see this!");
        } catch (IllegalArgumentException e) {
            System.out.println("; OK creation failed as expected - "+e);
        }
    }*/
}
