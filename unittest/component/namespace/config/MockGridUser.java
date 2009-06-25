package component.namespace.config;



/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */

import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;

public class MockGridUser {


    public static GridUserInterface buildMockGridUser() {
        String userDN = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Luca Magnoni";
        FQAN fqan0 = new FQAN("/dteam/Role=NULL/Capability=NULL");
        FQAN fqan1 = new FQAN("/dteam/italy/Role=NULL/Capability=NULL");
        FQAN fqan2 = new FQAN("/dteam/italy/INFN-CNAF/Role=NULL/Capability=NULL");
        FQAN[] fqans = {fqan0, fqan1, fqan2};
        return GridUserManager.makeVOMSGridUser(userDN, fqans);
    }

}
