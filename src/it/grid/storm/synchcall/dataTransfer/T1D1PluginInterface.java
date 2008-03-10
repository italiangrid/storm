package it.grid.storm.synchcall.dataTransfer;

import it.grid.storm.namespace.StoRI;

/**
 * Interface for the variuos Plugin that can be used to manage the T1D1 migration
 * (different user, hidden file etc)
 * @author lucamag
 *
 */
public interface T1D1PluginInterface {
    
    public int startMigration(StoRI stori, String prefix);

}
