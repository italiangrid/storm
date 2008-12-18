package it.grid.storm.authz.sa.conf;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.logging.Log;
import it.grid.storm.authz.AuthzDirector;

public class FileAuthzDBListener implements ConfigurationListener {

    private final Log log = AuthzDirector.getLogger();
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
