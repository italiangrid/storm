/**
 * 
 */
package it.grid.storm.tape.recalltable.model;

import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;

/**
 * @author zappi
 * 
 *         This class is the representation of a Recall Task data. Only
 *         user-settable data are writable. The rest data are only read.
 * 
 */
public class RecallTaskData {

    /**
     * { "stfn":"<file-name>", "dn":"<DN>", "fqans":["fqan":"<FQAN>",
     * "fqan":"<FQAN>"], "vo-name":"<vo-name>" }
     **/   
    
    private String fileName = null;
    private String userDN = null;
    private String[] fqansString = null;
    private String voName = null;
    public static String UNSPECIFIED = "unspecified-VO";

    /**
     * Constructor with x.509 certificate
     * 
     * @param filename
     * @param user
     */
    public RecallTaskData(String filename, GridUserInterface user) {
        fileName = filename;
        if (user instanceof VomsGridUser) {
            VomsGridUser vu = (VomsGridUser) user;
            fqansString = vu.getFQANsString();
            voName = vu.getVO().getValue();
        } else {
            voName = RecallTaskData.UNSPECIFIED;
        }
        userDN = user.getDistinguishedName().getX500DN_rfc2253();
    }
    
    public RecallTaskData(String filename, String dn, String[] fqans, String voName) {
        fileName = filename;
        DistinguishedName dn500 = (new DistinguishedName(dn));
        userDN = dn500.getX500DN_rfc2253();
        fqansString = fqans;
        this.voName = voName;
    }
    
    
    
}
