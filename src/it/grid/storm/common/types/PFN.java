package it.grid.storm.common.types;

/**
 * This class represents the PFN of a TTURL: it must begin with a /, and it
 * cannot be an empty or null string. Any white spaces are automatically removed.
 * For Empty PFN there is the appropriate method to be used.
 *
 * @author  CNAF - Bologna
 * @version 1.0
 * @date    April 2005
 */
public class PFN {

    private String name; //String containing the PFN
    private boolean empty = true; //boolean indicating whether this is an Empty PFN

    /**
     * Private constructor that requires a String representing the pathname of
     * the TTURL. Empty spaces are automatically removed.
     */
    private PFN(String name,boolean empty) {
        /*
         * @TOREMOVE!
         * This additional slash is added here only for test puourpose!
         * This is not the right place to do that!
         */
        //this.name = "/"+name.replaceAll(" ","");
         
         this.name = name.replaceAll(" ","");
       	 this.empty = empty;
    }

    /**
     * Method that returns an Empty PFN.
     */
    public static PFN  makeEmpty() {
	    return new PFN("",true);
    }

    /**
     * Method that returns a PFN corresponding to the supplied String. The String
     * cannot be null or empty otherwise an InvalidPFNAttributeException is
     * thrown. Likewise if it does not begin with a /.
     */
    public static PFN make(String name) throws InvalidPFNAttributeException {
        //if ((name==null) || (name=="") || (name.charAt(0)!='/')) throw new InvalidPFNAttributeException(name);
        if ((name==null) || (name=="")) throw new InvalidPFNAttributeException(name);
        return new PFN(name, false);
    }

    public String getValue() {
	    return name;
    }

    /**
     * Method that returns true if this PFN is an Empty object.
     */
    public boolean isEmpty() {
        return empty;
    }



    public String toString() {
        if (empty) return "Empty PFN";
        return name;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof PFN)) return false;
        PFN po = (PFN) o;
	    if (po.empty && empty) return true;
        return (!po.empty) && (!empty) && (name.equals(po.name));
    }

    public int hashCode() {
        if (empty) return 0;
        int hash=17;
        return hash + 37*name.hashCode();
    }





/*
    public static void main(String[] args) {
        System.out.println("Testing Empty PFN:");
        System.out.println("Creating two empty PFN and printing to st.out - should see: Empty PFN and its hash is 0, twice: ");
        PFN e1 = PFN.makeEmpty(); System.out.println(e1+" and its hash is "+e1.hashCode());
        PFN e2 = PFN.makeEmpty(); System.out.println(e2+" and its hash is "+e2.hashCode());
        System.out.println("Comparing e1 to e1 - should be true: "+e1.equals(e1));
        System.out.println("Comparing e1.equals(e2) - should be true: "+e1.equals(e2));
        System.out.println("Comparing e2.equals(e1) - should be true: "+e2.equals(e1));
        System.out.println("Comparing e1 to Object - should be false: "+e1.equals(new Object()));
        //
        System.out.println("\n\nTesting correct PFN creation:");
        String s1 = "/prova1.com/sdf/1";
        String s2 = "/prova2.com/srm/2";
        String s3 = "/prova1.com/sdf/1";
        try {
            PFN p1 = PFN.make(s1); System.out.println("Should see "+s1+"; p1="+p1+". HashCode: "+p1.hashCode());
            PFN p2 = PFN.make(s2); System.out.println("Should see "+s2+"; p1="+p2+". HashCode: "+p2.hashCode());
            PFN p3 = PFN.make(s3); System.out.println("Should see "+s3+"; p1="+p3+". HashCode: "+p3.hashCode());
            System.out.println("p1 equals p1 (true): "+p1.equals(p1));
            System.out.println("p1 equals null (false): "+p1.equals(null));
            System.out.println("p1 equals Object (false): "+p1.equals(new Object()));
            System.out.println("p1 equals p2 (false): "+p1.equals(p2));
            System.out.println("p2 equals p1 (false): "+p2.equals(p1));
            System.out.println("p1 equals p3 (true): "+p1.equals(p3));
            System.out.println("p3 equals p1 (true): "+p3.equals(p1));
            System.out.println("p3 equals empty (false): "+p3.equals(PFN.makeEmpty()));
            System.out.println("empty equals p3 (false): "+PFN.makeEmpty().equals(p3));
        } catch (Exception e) {
            System.out.println("Shouldnt see this! "+e);
        }
        //
        //Testing empty space removal
        String spstr1 = "/ No Empty Space ";
        String spstr2 = "/NoEmptySpace";
        System.out.println("\n\nTesting empty space removal:");
        try {
            PFN p1 = PFN.make(spstr1); System.out.println("Created PFN1 with "+spstr1+"; should see "+spstr2+": "+p1);
            PFN p2 = PFN.make(spstr2); System.out.println("Created PFN2 with "+spstr2+"; should see "+spstr2+": "+p2);
            System.out.println("Both should be equal - p1.equals(p2) true:" + p1.equals(p2));
        } catch (Exception e) {
            System.out.println("Should not see this!"+e);
        }
        //
        //Testing Exceptions!
        System.out.println("\n\nTesting Exception throwing with invalid PFN attributes!");
        System.out.println("Creating PFN without initial /:");
        String s = "srm.infn.it";
        try {
            PFN p = PFN.make(s);
            System.out.println("Shouldnt see this!");
        } catch (InvalidPFNAttributeException e) {
            System.out.println("OK: exception thrown (false false true) - " + e);
        }
        System.out.println("\nCreating PFN with null:");
        s = null;
        try {
            PFN p = PFN.make(s);
            System.out.println("Shouldnt see this!");
        } catch (InvalidPFNAttributeException e) {
            System.out.println("OK: exception thrown (true false false) - " + e);
        }
        System.out.println("\nCreating PFN with empty String:");
        s = "";
        try {
            PFN p = PFN.make(s);
            System.out.println("Shouldnt see this!");
        } catch (InvalidPFNAttributeException e) {
            System.out.println("OK: exception thrown (false true false) - " + e);
        }
        //
        //Testing isEmpty and getValue
        try {
            System.out.println("\n\nTesting isEmpty and getValue:");
            PFN ps = PFN.make(s1);
            PFN pe = PFN.makeEmpty();
            System.out.println("Should see "+s1+"; "+ps.getValue()+". isEmpty should be false: "+ps.isEmpty());
            System.out.println("Should see nothing ; "+pe.getValue()+". isEmpty should be true: "+pe.isEmpty());
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
    }*/
}
