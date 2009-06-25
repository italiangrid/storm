package it.grid.storm.authz.sa;

import it.grid.storm.authz.sa.model.SpaceACE;
import it.grid.storm.namespace.model.SAAuthzType;

import java.util.List;


public interface AuthzDBInterface {

    public SAAuthzType getAuthzDBType();

    public List<SpaceACE> getOrderedListOfACE();

}
