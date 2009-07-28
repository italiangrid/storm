package it.grid.storm.srm.types;

import it.grid.storm.filesystem.Checksum;

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
public class TCheckSumType {

    public static String PNAME_CHECKSUMTYPE = "checkSumType";
    
    private Checksum.ChecksumType chkType = null;

    public TCheckSumType(Checksum.ChecksumType chkType) {
        this.chkType = chkType;
    }

    @Override
    public String toString() {
        return chkType.toString();
    }

    public String getValue() {
        return chkType.toString();
    }

    public void encode(Map param, String name) {
        param.put(name, this.toString());
    }
};
