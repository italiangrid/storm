package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * This class represents a Request Token
 *
 * @author  Magnoni Luca
 * @author  CNAF INFN Bologna
 * @date   Avril, 2005
 * @version 1.0
 */
public class TRequestToken
{
    public static String PNAME_REQUESTOKEN = "requestToken";

    String token; //string representing the token!

    /**
     * Constructor that requires a String representing the token. If it is null,
     * then an InvalidAttributeException is thrown.
     */
    public TRequestToken(String token) throws InvalidTRequestTokenAttributesException
    {
        if (token == null)
            throw new InvalidTRequestTokenAttributesException(token);
        this.token = token;
    }

    
    public static TRequestToken decode(Map inputParam, String fieldName)
                    throws InvalidTRequestTokenAttributesException
    {
        String requestToken = (String) inputParam.get(fieldName);
        return new TRequestToken(requestToken);
    }

    public String getValue()
    {
        return token;
    }

    public String toString()
    {
        return token;
    }

    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof TRequestToken))
            return false;
        TRequestToken st = (TRequestToken) o;
        return token.equals(st.token);
    }

    public int hashCode()
    {
        int hash = 17;
        return 37 * hash + token.hashCode();
    }

    /*
     public static void main(String[] args) {
     //Testing TRequestToken Creation
     System.out.println("Testing TRequestToken creation...");
     try {
     String s1 = "prova-id-1";
     String s2 = "prova-is-2";
     String s3 = "prova-id-1";
     TRequestToken r1 = new TRequestToken(s1);
     TRequestToken r2 = new TRequestToken(s2);
     TRequestToken r3 = new TRequestToken(s3);
     System.out.println("r1 should be "+s1+": "+r1+"; hash="+r1.hashCode()+"; getValue="+r1.getValue());
     System.out.println("r2 should be "+s2+": "+r2+"; hash="+r2.hashCode()+"; getValue="+r2.getValue());
     System.out.println("r3 should be "+s3+": "+r3+"; hash="+r3.hashCode()+"; getValue="+r3.getValue());
     System.out.println("r1.equals(r2) false:"+r1.equals(r2)+"; r2.equals(r1) false:"+r2.equals(r1));
     System.out.println("r1.equals(null) false:"+r1.equals(null));
     System.out.println("r1.equals(Object) false:"+r1.equals(new Object()));
     System.out.println("r1.equals(r3) true:"+r1.equals(r3));
     } catch (Exception e) {
     System.out.println("Should not see this!");
     }
     //TEsting Exceptions
     System.out.println("\n\nTesting Exception throwing...");
     try {
     String s = "prova-id-1";
     TRequestToken r = new TRequestToken(s);
     System.out.println("Successfully created "+r);
     System.out.print("Now attempting with null String... ");
     try {
     new TRequestToken(null);
     System.out.println("Should not see this!");
     } catch (InvalidTRequestTokenAttributesException e) {
     System.out.println("OK creation failed as expected."+e);
     }
     } catch (Exception e) {
     System.out.println("Should not see this!");
     }
     }*/

}
