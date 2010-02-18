package it.grid.storm.griduser;

import it.grid.storm.jna.StormLcmapsJna;

public class LcmapsJnaMapper implements MapperInterface {

    public LocalUser map(String dn, String[] fqans) throws CannotMapUserException {
        return StormLcmapsJna.map(dn, fqans);
    }

}
