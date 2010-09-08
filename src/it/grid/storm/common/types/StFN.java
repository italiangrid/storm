package it.grid.storm.common.types;

import it.grid.storm.namespace.naming.NamingConst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents the Storage File Name of a SURL.
 * 
 * @author CNAF Bologna - EGRID ICTP
 * @version 2.0
 * @date March 2005
 */
public class StFN {

    private ArrayList<String> name = new ArrayList<String>();
    private boolean directory = false;

    private boolean empty = true;
    public static String PNAME_PATH = "path";

    private StFN(ArrayList<String> name, /* boolean startingSlash, */boolean empty, boolean dir) {
        this.name = name; // WARNING!!! A defensive copy of the ArrayList is _NOT_ made!
        this.empty = empty;
        /* this.startingSlash = startingSlash; */
        directory = dir;
    }

    /**
     * Public static method that returns an empty StFN.
     */
    public static StFN makeEmpty() {
        return new StFN(new ArrayList<String>(),/* false, */true, false);
    }

    /**
     * Public static method that requires a String representing the pathname of the SFN: it cannot be null or empty
     * otherwise an InvalidStFNAttributeException is thrown. Likewise if it contains two consecutive dots (..). or does
     * not begin with a slash (/).
     */
    public static StFN make(String name) throws InvalidStFNAttributeException {
        if (invalid(name)) {
            throw new InvalidStFNAttributeException(name);
        }
        return new StFN(normalize(name),/* name.charAt(0)=='/', */false, checkDirectory(name));
    }

    /**
     * Public static method that returns true if the supplied String ends with the Separator, thereby indicating a
     * directory.
     */
    private static boolean checkDirectory(String path) {
        if (path != null) {
            return path.endsWith(NamingConst.SEPARATOR);
        } else {
            return false;
        }
    }

    /**
     * Private method that returns true if the supplied string is null, or is empty, or contains two consecutive dots
     * (..), or does not begin with a slash (/).
     */
    static private boolean invalid(String name) {
        boolean wrong = (name == null) || (name.equals("")) || (name.charAt(0) != '/');
        return wrong;
    }

    /**
     * Private method that accepts a valid String as defined by the private valid method, and returns an ordered
     * ArrayList of all slash-separated elemets, trimmed of leading and trailing white spaces. Multiple consecutive
     * slashes are treated as a single slash. Example1: /a/ b /c/d Result: a b c d Example2: /////a///b////////////
     * c/d///////// Result: a b c d Example3: / Result: empty ArrayList!
     */
    static private ArrayList<String> normalize(String s) {
        // split around slash!
        String[] pieces = s.split("/");
        // remove all empty Strings which may have been produced because of consecutive slashes!
        ArrayList<String> auxList = new ArrayList<String>();
        int pos = 0;
        String aux = null;
        for (String piece : pieces) {
            aux = piece; // get the element
            aux = aux.trim(); // remove all leading and trailing white spaces
            if (!aux.equals("")) {
                auxList.add(pos++, aux);
            }
        }
        return auxList;
    }

    /**
     * Method that returns a Collection of all parent StFNs, stopping at root parent. The following example clarifies
     * what is meant by parent StFNs, and by stopping at root parent. Original StFN: /EGRID/original/data/nyse/file.txt
     * Parent StFNs: /EGRID/original/data/nyse /EGRID/original/data /EGRID/original /EGRID Second example: /file.txt
     * Parent StFNs: Empty collection! Third example: /EGRID/ Parent StFNs: Empty collection! An empty collection is
     * returned if any error occurs during creation of parent StFNs. Likewise if This is an EmptyStFN.
     */
    public Collection<StFN> getParents() {
        Collection<StFN> aux = new ArrayList<StFN>();
        if (empty) {
            return aux; // empty StFN!
        }
        int size = name.size(); // number of elements in this StFN
        if ((size == 0) || (size == 1)) {
            return aux; // StFN directly on root, or with only _one_ element!
        }
        for (int i = 1; i < size; i++) {
            aux.add(new StFN(new ArrayList<String>(name.subList(0, i))/* ,this.startingSlash */, false, true)); // recall
                                                                                                        // sublist goes
                                                                                                        // from 0
                                                                                                        // inclusive, to
                                                                                                        // i
                                                                                                        // _EXCLUDED_!!!
        }
        return aux;
    }

    /**
     * Method that returns the parent StFN. The following example clarifies what is meant by parent StFN. Beware of the
     * root of the StFN: the parent is calculated from the root! Original StFN: /EGRID/original/data/nyse/file.txt
     * Parent StFN: /EGRID/original/data/nyse Second example, Original StFN: /file.txt Parent StFN: Empty StFN! Third
     * example: /EGRID/ Parent StFN: Empty StFN! An empty StFN is returned if any error occurs during creation of
     * parent. Likewise if This is an EmptyStFN.
     */
    public StFN getParent() {
        if (empty) {
            return makeEmpty(); // empty StFN!
        }
        int size = name.size(); // number of elements in this StFN
        if ((size == 0) || (size == 1)) {
            return makeEmpty(); // either directly on root, or only one element!
        }
        return new StFN(new ArrayList<String>(name.subList(0, size - 1))/* ,this.startingSlash */, false, true);
    }

    /**
     * Method that returns true if this StFN is empty.
     */
    public boolean isEmpty() {
        return empty;
    }

    public String getValue() {
        return toString();
    }

    @Override
    public String toString() {
        if (empty) {
            return "Empty StFN";
        }
        int size = name.size();
        if (size == 0) {
            return "/"; // This is not an Empty StFN because empty==false; yet its elements List is empty: it can only
                        // be the root StFN! That is the "/"!!!
        }
        StringBuffer sb = new StringBuffer();
        /* if (this.startingSlash) */sb.append("/");
        for (Iterator<String> i = name.iterator(); i.hasNext();) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append("/");
            }
        }
        if (directory) {
            sb.append(NamingConst.SEPARATOR);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StFN)) {
            return false;
        }
        StFN po = (StFN) o;
        if (po.empty && empty) {
            return true;
        }
        if ((!empty) && (!po.empty) && (name.size() == 0) && (po.name.size() == 0)) {
            return true;
        }
        return (!empty) && (!po.empty) /* && (startingSlash==po.startingSlash) */
                && (directory == po.directory) && name.equals(po.name);
    }

    @Override
    public int hashCode() {
        if (empty) {
            return 0;
        }
        int hash = 17;
        if (name.size() != 0) {
            hash = 31 * hash + name.hashCode();
        }
        hash = 31 * hash + (directory ? 1 : 0);
        return hash;
    }

    /**
     * Encode StFN for FE communication.
     */
    public void encode(Map<String,String> param, String name) {
        param.put(name, toString());
    }
    /*
     * public static void main(String[] args) { // //TEsting empty StFN System.out.println("Testing Empty StFN...");
     * System.out.println(
     * "Creating two empty StFN and printing to st.out - should see: Empty StFN and its hash is 0 and its value is Empty StFN, twice: "
     * ); StFN e1 = StFN.makeEmpty();
     * System.out.println(e1+" and its hash is "+e1.hashCode()+" and its value is "+e1.getValue
     * ()+". isEmpty should be true: "+e1.isEmpty()); StFN e2 = StFN.makeEmpty();
     * System.out.println(e2+" and its hash is "
     * +e2.hashCode()+" and its value is "+e2.getValue()+". isEmpty should be true: "+e2.isEmpty());
     * System.out.println("Comparing e1 to e1 - should be true: "+e1.equals(e1));
     * System.out.println("Comparing e1.equals(e2) - should be true: "+e1.equals(e2));
     * System.out.println("Comparing e2.equals(e1) - should be true: "+e2.equals(e1));
     * System.out.println("Comparing e1 to Object - should be false: "+e1.equals(new Object()));
     * System.out.println("Comparing e1 to null - should be false: "+e1.equals(null)); // //TEsting non empty StFN
     * creation System.out.println("\n\nTesting correct StFN creation..."); String s1 = "/prova1.com/sdf/1"; String s2 =
     * "/prova2.com/srm/2"; String s3 = "/prova1.com/sdf/1"; try { StFN p1 = StFN.make(s1);
     * System.out.println("Should see "
     * +s1+"; p1="+p1+". HashCode: "+p1.hashCode()+". getValue(): "+p1.getValue()+". isEmpty should be false: "
     * +p1.isEmpty()); StFN p2 = StFN.make(s2);
     * System.out.println("Should see "+s2+"; p1="+p2+". HashCode: "+p2.hashCode
     * ()+". getValue(): "+p2.getValue()+". isEmpty should be false: "+p2.isEmpty()); StFN p3 = StFN.make(s3);
     * System.out.println("Should see "+s3+"; p1="+p3+". HashCode: "+p3.hashCode()+". getValue(): "+p3.getValue()+
     * ". isEmpty should be false: "+p3.isEmpty()); System.out.println("p1 equals p1 (true): "+p1.equals(p1));
     * System.out.println("p1 equals null (false): "+p1.equals(null));
     * System.out.println("p1 equals Object (false): "+p1.equals(new Object()));
     * System.out.println("p1 equals p2 (false): "+p1.equals(p2));
     * System.out.println("p2 equals p1 (false): "+p2.equals(p1));
     * System.out.println("p1 equals p3 (true): "+p1.equals(p3));
     * System.out.println("p3 equals p1 (true): "+p3.equals(p1));
     * System.out.println("p3 equals empty (false): "+p3.equals(StFN.makeEmpty()));
     * System.out.println("empty equals p3 (false): "+StFN.makeEmpty().equals(p3)); } catch (Exception e) {
     * System.out.println("Shouldnt see this! "+e); } // //Testing Exceptions!
     * System.out.println("\n\nTesting Exception throwing with invalid StFN attributes...");
     * System.out.print("Creating StFN without initial /:"); String s = "srm.infn.it"; try { StFN.make(s);
     * System.out.println("Should not see this!"); } catch (InvalidStFNAttributeException e) {
     * System.out.println(" OK, exception thrown as expected (false false true) - " + e); }
     * System.out.print("Creating StFN with null:"); s = null; try { StFN.make(s);
     * System.out.println("Should not see this!"); } catch (InvalidStFNAttributeException e) {
     * System.out.println("OK: exception thrown as expected (true false false) - " + e); }
     * System.out.print("Creating StFN with empty String:"); s = ""; try { StFN.make(s);
     * System.out.println("Should not see this!"); } catch (InvalidStFNAttributeException e) {
     * System.out.println("OK: exception thrown (false true false) - " + e); } }
     */
}
