/**
 * This class represents the TUserID managed by Srm. 
 *
 * @author  Magnoni Luca
 * @author  CNAF - INFN  Bologna
 * @date    Avril, 2005
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

public class TUserID implements Serializable
{

    public static String PNAME_USERID = "userID";
    public static String PNAME_OWNER  = "owner";

    private String       userID       = new String();

    //TO Complete with  Exception if null string speified
    public TUserID(String id) throws InvalidTUserIDAttributeException {
        if ((id == null) || (id == ""))
            throw new InvalidTUserIDAttributeException(id);
        userID = id;
    }

    public static TUserID makeEmpty()
    {
        try {
            return new TUserID("Unknown.");
        } catch (InvalidTUserIDAttributeException e) {}
        ;
        return null;
    }

    public String toString()
    {
        return userID;
    }

    public String getValue()
    {
        return userID;
    }

    public void encode(Map param, String name)
    {
        param.put(name, userID);
    }
};
