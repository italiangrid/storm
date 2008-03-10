package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;

/**
 * This class represents the TCheckSumType of a Permission Area managed by Srm. 
 *
 * @author  Magnoni Luca
 * @author  CNAF - INFN  Bologna
 * @date    Avril, 2005
 * @version 1.0
 */

/**
 * Class that represent CheckSum for file. 
 */
public class TCheckSumType
{

    private String       type               = null;

    public static String PNAME_CHECKSUMTYPE = "checkSumType";

    //TO Complete wut Exception if Strin specified == null
    public TCheckSumType(String type) {
        this.type = type;
    }

    public String toString()
    {
        return type;
    }

    public String getValue()
    {
        return type;
    }

    public void encode(Map param, String name)
    {
        param.put(name, this.toString());
    }
};
