package it.grid.storm.common.types;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an EndPoint of a SURL: it must begin with a /.
 *
 * @author  EGRID ICTP
 * @version 1.0
 * @date    August 2006
 */
public class EndPoint {

    private ArrayList name = new ArrayList();
    private boolean empty = true;
    private boolean directory = false;

    private EndPoint(ArrayList name,boolean empty) {
	    this.name = name; //WARNING!!! A defensive copy of the ArrayList is _NOT_ made!
	    this.empty = empty;
    }

    /**
     * Public static method that returns an empty EndPoint.
     */
    public static EndPoint makeEmpty() {
	    return new EndPoint(new ArrayList(),true);
    }

    /**
     * Public static method that requires a String representing the EndPoint:
     * it cannot be null or empty otherwise an InvalidEndPointAttributeException is
     * thrown. Likewise if it does not begin with a slash (/), or if it contains two
     * consecutive dots (..).
     */
    public static EndPoint make(String name) throws InvalidEndPointAttributeException {
        if (invalid(name)) throw new InvalidEndPointAttributeException(name);
        return new EndPoint(normalize(name),false);
    }

    /**
     * Private method that returns true if the supplied string is null, or is empty, or
     * does not begin with a slash (/), or contains two consecutive dots (..).
     */
    static private boolean invalid(String name) {
        boolean wrong = (name==null) ||
            (name.equals("")) ||
            (name.charAt(0)!='/') ||
            (name.indexOf("..")!=-1);
        return wrong;
    }

    /**
     * Private method that accepts a valid String as defined by the private valid method,
     * and returns an ordered ArrayList of all slash-separated elemets, trimmed of leading and
     * trailing white spaces. Multiple consecutive slashes are treated as a single slash.
     *
     * Example1:   /a/   b   /c/d
     * Result:  a b c d
     *
     * Example2:  /////a///b////////////   c/d/////////
     * Result:  a b c d
     *
     * Example3: /
     * Result:  empty ArrayList!
     *
     */
    static private ArrayList normalize(String s) {
        //split around slash!
        String[] pieces = s.split("/");
        //remove all empty Strings which may have been produced because of consecutive slashes!
        ArrayList auxList = new ArrayList();
        int pos = 0;
        String aux = null;
        for (int k=0; k<pieces.length; k++) {
            aux = pieces[k]; //get the element
            aux = aux.trim(); //remove all leading and trailing white spaces
            if (!aux.equals("")) auxList.add(pos++,aux);
        }
        return auxList;
    }






    /**
     * Method that returns true if this StFN is empty.
     */
    public boolean isEmpty() {
		return empty;
	}

    public String toString() {
        if (empty) return "Empty EndPoint";
        int size = this.name.size();
        if (size==0) return "/"; //This is not an Empty EndPoint because empty==false; yet its elements List is empty: it can only be the root EndPoint! That is the "/"!!!
        StringBuffer sb = new StringBuffer();
        for (Iterator i = this.name.iterator(); i.hasNext(); ) {
            sb.append("/"); sb.append(i.next());
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof EndPoint)) return false;
        EndPoint po = (EndPoint) o;
        if (po.empty && empty) return true;
        if ((!empty) && (!po.empty) && (name.size()==0) && (po.name.size()==0)) return true;
        return (!empty) && (!po.empty) && name.equals(po.name);
    }

    public int hashCode() {
        if (empty) return 0;
        int hash = 17;
        if (name.size()==0) return hash;
        return 37*hash + name.hashCode();
    }

/*
    public static void main(String[] args) {
        //
        //TEsting empty StFN
        System.out.println("Testing Empty StFN...");
        System.out.println("Creating two empty StFN and printing to st.out - should see: Empty StFN and its hash is 0 and its value is Empty StFN, twice: ");
        StFN e1 = StFN.makeEmpty(); System.out.println(e1+" and its hash is "+e1.hashCode()+" and its value is "+e1.getValue()+". isEmpty should be true: "+e1.isEmpty());
        StFN e2 = StFN.makeEmpty(); System.out.println(e2+" and its hash is "+e2.hashCode()+" and its value is "+e2.getValue()+". isEmpty should be true: "+e2.isEmpty());
        System.out.println("Comparing e1 to e1 - should be true: "+e1.equals(e1));
        System.out.println("Comparing e1.equals(e2) - should be true: "+e1.equals(e2));
        System.out.println("Comparing e2.equals(e1) - should be true: "+e2.equals(e1));
        System.out.println("Comparing e1 to Object - should be false: "+e1.equals(new Object()));
        System.out.println("Comparing e1 to null - should be false: "+e1.equals(null));
        //
        //TEsting non empty StFN creation
        System.out.println("\n\nTesting correct StFN creation...");
        String s1 = "/prova1.com/sdf/1";
        String s2 = "/prova2.com/srm/2";
        String s3 = "/prova1.com/sdf/1";
        try {
            StFN p1 = StFN.make(s1); System.out.println("Should see "+s1+"; p1="+p1+". HashCode: "+p1.hashCode()+". getValue(): "+p1.getValue()+". isEmpty should be false: "+p1.isEmpty());
            StFN p2 = StFN.make(s2); System.out.println("Should see "+s2+"; p1="+p2+". HashCode: "+p2.hashCode()+". getValue(): "+p2.getValue()+". isEmpty should be false: "+p2.isEmpty());
            StFN p3 = StFN.make(s3); System.out.println("Should see "+s3+"; p1="+p3+". HashCode: "+p3.hashCode()+". getValue(): "+p3.getValue()+". isEmpty should be false: "+p3.isEmpty());
            System.out.println("p1 equals p1 (true): "+p1.equals(p1));
            System.out.println("p1 equals null (false): "+p1.equals(null));
            System.out.println("p1 equals Object (false): "+p1.equals(new Object()));
            System.out.println("p1 equals p2 (false): "+p1.equals(p2));
            System.out.println("p2 equals p1 (false): "+p2.equals(p1));
            System.out.println("p1 equals p3 (true): "+p1.equals(p3));
            System.out.println("p3 equals p1 (true): "+p3.equals(p1));
            System.out.println("p3 equals empty (false): "+p3.equals(StFN.makeEmpty()));
            System.out.println("empty equals p3 (false): "+StFN.makeEmpty().equals(p3));
        } catch (Exception e) {
            System.out.println("Shouldnt see this! "+e);
        }
        //
        //Testing Exceptions!
        System.out.println("\n\nTesting Exception throwing with invalid StFN attributes...");
        System.out.print("Creating StFN without initial /:");
        String s = "srm.infn.it";
        try {
            StFN.make(s);
            System.out.println("Should not see this!");
        } catch (InvalidStFNAttributeException e) {
            System.out.println(" OK, exception thrown as expected (false false true) - " + e);
        }
        System.out.print("Creating StFN with null:");
        s = null;
        try {
            StFN.make(s);
            System.out.println("Should not see this!");
        } catch (InvalidStFNAttributeException e) {
            System.out.println("OK: exception thrown as expected (true false false) - " + e);
        }
        System.out.print("Creating StFN with empty String:");
        s = "";
        try {
            StFN.make(s);
            System.out.println("Should not see this!");
        } catch (InvalidStFNAttributeException e) {
            System.out.println("OK: exception thrown (false true false) - " + e);
        }
    }
*/
}
