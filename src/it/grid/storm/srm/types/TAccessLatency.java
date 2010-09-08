/**
 * This class represents the TAccessLatency SRM type.
 *
 * @author  Alberto Forti
 * @author  CNAF - INFN  Bologna
 * @date    Luglio, 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.Map;

public class TAccessLatency {
    public static String PNAME_accessLatency = "accessLatency";
    
    private String accessLatency = null;
    
    public static final TAccessLatency
        ONLINE = new TAccessLatency("ONLINE"),
        NEARLINE = new TAccessLatency("NEARLINE"),
        EMPTY = new TAccessLatency("EMPTY");
    
    private TAccessLatency(String accessLatency) {
        this.accessLatency = accessLatency;
    }
    
    public final static TAccessLatency getTAccessLatency(int idx) {
        switch (idx) {
        case 0: return ONLINE;
        case 1: return NEARLINE;
        default: return EMPTY;
        }
    }
    
    /**
     * decode() method creates a TAccessLatency object from the information
     * contained into the structured parameter received from the FE. 
     * @param inputParam map structure
     * @param fieldName  field name
     * @return
     */
    public final static TAccessLatency decode(Map inputParam, String fieldName) {
        Integer val;
        
        val = (Integer) inputParam.get(fieldName);
        if (val == null) return EMPTY;
        
        return TAccessLatency.getTAccessLatency(val.intValue());
    }
    
    /**
     * encode() method creates structured parameter representing this ogbject.
     * It is passed to the FE.
     * @param outputParam hashtable structure
     * @param fieldName  field name
     */
    public void encode(Map<String, Integer> outputParam, String fieldName) {
        Integer value = null;
        
        if (this.equals(TAccessLatency.ONLINE)) value = new Integer(0);
        if (this.equals(TAccessLatency.NEARLINE)) value = new Integer(1);
        
        outputParam.put(fieldName, value);
    }
    
    public String toString()
    {
        return accessLatency;
    }
    
    public String getValue()
    {
        return accessLatency;
    }
}
