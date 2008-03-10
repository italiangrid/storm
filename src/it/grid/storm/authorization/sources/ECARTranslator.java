package it.grid.storm.authorization.sources;

import it.grid.storm.srm.types.TSURL;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.filesystem.FilesystemPermission;

/**
 * Class that translates between StoRM Object Model and ECAR data structures.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    June 2006
 */
public class ECARTranslator {

    /**
     * Method that translates the given StoRI into a String representing the logical
     * file name. The convention used simply returns the StFN of the corresponding SURL!
     * 
     */
    public String logicalName(TSURL file) {
        return "/grid"+file.sfn().stfn().toString();
    }

    /**
     * Method that translates the given VomsGridUser into a String representing the
     * Distinguished Name, as per ECAR web service requirement.
     */
    public String user(VomsGridUser gu) {
        return gu.getDn();
    }

    /**
     * Method that returns an int representing the kind of permission being
     * queried. As per contract of ECAR, the int are the _SAME_ as those used
     * by the FilesystemPermission classes in their integer representation!
     */
    public int permission(FilesystemPermission p) {
        return p.getInt();
    }
}
