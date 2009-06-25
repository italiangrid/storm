package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.AuthzDirector;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.slf4j.Logger;

public class FileAuthzDBListener implements ConfigurationListener {

    private final Logger log = AuthzDirector.getLogger();
    private String authzFileName;

    public FileAuthzDBListener(String authzFileName) {
        this.authzFileName = authzFileName;
    }

    /**
     * configurationChanged
     *
     * @param configurationEvent ConfigurationEvent
     */
    public void configurationChanged(ConfigurationEvent configurationEvent) {
        if (!configurationEvent.isBeforeUpdate())
        {
            // only display events after the modification was done
            log.debug("Authz DB File "+this.authzFileName+" is changed!");
            log.debug("  - Type = " + configurationEvent.getType());
            if (configurationEvent.getPropertyName() != null)
            {
                log.debug("Property name = " + configurationEvent.getPropertyName());
            }
            if (configurationEvent.getPropertyValue() != null)
            {
                log.debug("Property value = " + configurationEvent.getPropertyValue());
            }
        }
    }

}
