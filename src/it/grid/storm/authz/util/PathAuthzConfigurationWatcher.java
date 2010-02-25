/**
 * 
 */
package it.grid.storm.authz.util;

import java.io.File;

/**
 * @author ritz
 */
public class PathAuthzConfigurationWatcher extends ConfigurationWatcher {

    /**
     * @param file
     */
    public PathAuthzConfigurationWatcher(File file) {
        super(file);
        log.debug("Watcher manages the configuration file :" + file);
    }

    /*
     * (non-Javadoc)
     * @see it.grid.storm.authz.util.ConfigurationWatcher#onChange()
     */
    @Override
    protected void onChange() {
        log.info("Path Authorization DB is changed! Going to reload it");
        // Force the reload of the configuration file

    }

}
