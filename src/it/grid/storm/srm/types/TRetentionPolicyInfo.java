/**
 * This class represents the TRetentionPolicyInfo additional data associated with the SRM request.
 * @author  Alberto Forti
 * @author  CNAF -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class TRetentionPolicyInfo {
    
    /* Hashtable field names for encode() and decode() methods */
    public static String PNAME_retentionPolicyInfo = "retentionPolicyInfo";
    
    private TRetentionPolicy retentionPolicy;
    private TAccessLatency accessLatency;
    
    public TRetentionPolicyInfo() {
    }
    
    public TRetentionPolicyInfo(TRetentionPolicy retentionPolicy, TAccessLatency accessLatency) {
        this.retentionPolicy = retentionPolicy;
        this.accessLatency = accessLatency;
    }
    
    /**
     * decode() method creates a TRetentionPolicyInfo object from the inforation
     * contained into the structured parameter received from the FE. 
     * @param inputParam hashtable structure
     * @param fieldName  field name
     * @return
     */
    public static TRetentionPolicyInfo decode(Map inputParam, String fieldName) 
    {
        Map param = (Map) inputParam.get(fieldName);
        if (param == null) return null;
        TRetentionPolicy retPol = TRetentionPolicy.decode(param, TRetentionPolicy.PNAME_retentionPolicy);
        TAccessLatency accLat = TAccessLatency.decode(param, TAccessLatency.PNAME_accessLatency);
        
        return new TRetentionPolicyInfo(retPol, accLat);
    }
    
    /**
     * encode() method creates structured parameter representing this ogbject.
     * It is passed to the FE.
     * @param outputParam hashtable structure
     * @param fieldName  field name
     */
    public void encode(Map outputParam, String fieldName) {
        Map param = new HashMap();
        
        retentionPolicy.encode(param, TRetentionPolicy.PNAME_retentionPolicy);
        accessLatency.encode(param, TAccessLatency.PNAME_accessLatency);
        
        outputParam.put(fieldName, param);
    }
    
    /**
     * Get Retention Policy.
     * @return TRetentionPolicy
     */
    public TRetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }
    
    /**
     * Set Retention Policy.
     * @param retentionPolicy TRetentionPolicy
     */
    public void setRetentionPolicy(TRetentionPolicy retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }
    
    /**
     * Get AccessLatency.
     * @return TAccessLatency
     */
    public TAccessLatency getAccessLatency() {
        return accessLatency;
    }
    
    /**
     * Set AccessLatency.
     * @param accessLatency TAccessLatency
     */
    public void setAccessLatency(TAccessLatency accessLatency) {
        this.accessLatency = accessLatency;
    }
}

    