/**
 * 
 */
package it.grid.storm.authz.util;

import it.grid.storm.authz.AuthzDirector;

import java.io.File;
import java.util.TimerTask;

import org.slf4j.Logger;

/**
 * @author ritz
 */
public abstract class ConfigurationWatcher extends TimerTask {

    private long timeStamp;
    private final File file;
    Logger log;

    public ConfigurationWatcher(File file) {
        this.file = file;
        timeStamp = file.lastModified();
        log = AuthzDirector.getLogger();
    }

    @Override
    public final void run() {
        long timeStamp = file.lastModified();

        if (this.timeStamp != timeStamp) {
            this.timeStamp = timeStamp;
            onChange();
        }
    }

    // Take some actions on file changed
    protected abstract void onChange();

}
