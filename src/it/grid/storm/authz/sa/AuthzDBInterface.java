package it.grid.storm.authz.sa;

import java.util.List;
import it.grid.storm.authz.sa.model.*;


public interface AuthzDBInterface {

    public int getMajorVersion();

    public int getMinorVersion();

    public String getVersionDescription();

    public String getAuthzDBType();

    public String getHeader();

    public List<SpaceACE> getOrderedListOfACE();

}
