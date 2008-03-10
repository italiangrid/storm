package it.grid.storm.common.types;

import java.util.Map;
import java.util.HashMap;

/**
 * This class represents the possible site protocols of StoRM.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 26th, 2005
 * @version 1.0
 */
public class SiteProtocol {
	
	private String protocol = null;
    private static Map m = new HashMap();

	public static final SiteProtocol SRM = new SiteProtocol("srm") {
        public int hashCode() {
            return 1;
        }
    };

	public static final SiteProtocol EMPTY = new SiteProtocol("empty") {
        public int hashCode() {
            return 0;
        }
    };
	
   	private SiteProtocol(String protocol) {
		this.protocol = protocol;
        this.m.put(protocol,this);
	}


    /**
     * Facility method to obtain a SiteProtocol object from its String representation.
     * An IllegalArgumentExceptin is thrown if the supplied String does not have a
     * SiteProtocol counterpart. The supplied String may contain white spaces and be in
     * a mixture of upper and lower case characters.
     */
    public static SiteProtocol fromString(String value) throws IllegalArgumentException {
      value = value.toLowerCase().replaceAll(" ","");
      SiteProtocol aux = (SiteProtocol) m.get(value);
      if (aux==null) throw new IllegalArgumentException();
      return aux;
    }

    public String toString() {
        return protocol;
    }


    //Maybe should be removed!
	 public String getValue() {
		 return protocol;
	 }


/*
    public static SiteProtocol makeEmpty() {
	   return EMPTY;
	}

	 //INUTILE
	 public boolean isEmpty() {
		 if( protocol == EMPTY.getValue())
			 return true;
		 return false;
	 }
*/

/*
    public static void main(String[] args) {
        //Testing types...
        System.out.println("Testing types...");
        System.out.println("The empty protocol: "+SiteProtocol.EMPTY+"; hashCode:"+SiteProtocol.EMPTY.hashCode());
        System.out.println("The srm protocol: "+SiteProtocol.SRM+"; hashCode:"+SiteProtocol.SRM.hashCode());
        //
        //Testing facility method
        System.out.println("\nTesting facility method...");
        System.out.println("Should see the empty protocol: "+SiteProtocol.fromString(SiteProtocol.EMPTY.toString()));
        System.out.println("Should see the srm protocol: "+SiteProtocol.fromString(SiteProtocol.SRM.toString()));
    }
*/
}
