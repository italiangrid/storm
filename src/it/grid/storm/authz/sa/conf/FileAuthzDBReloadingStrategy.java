package it.grid.storm.authz.sa.conf;

import java.io.*;

import org.apache.commons.configuration.reloading.*;
import org.apache.commons.logging.*;
import it.grid.storm.authz.*;

public class FileAuthzDBReloadingStrategy extends FileChangedReloadingStrategy {

    private final Log log = AuthzDirector.getLogger();
    private long reloadingTime;
    private boolean notifing = false;

    protected void notifingPerformed() {
        //log.debug(" NOTIFING set to FALSE");
        this.notifing = false;
    }

    protected boolean notifingRequired() {
        //log.debug(" NOTIFING is "+notifing);
        return notifing;
    }

    protected void notifyNeeded() {
        //log.debug(" NOTIFING set to TRUE");
        this.notifing = true;

    }

    public boolean reloadingRequired() {

        //log.debug("Reloading is required?");
        boolean reloading = false;
        long now = System.currentTimeMillis();

        if (now > lastChecked + refreshDelay) {
            lastChecked = now;
            if (hasChanged()) {
                reloading = true;
            }
        }
        return reloading;
    }


    public void reloadingPerformed() {
        updateLastModified();
        this.reloadingTime = System.currentTimeMillis();
        //@todo REFRESH DELLA CACHE!!
        log.debug("REFRESHING the AUTHZDB CACHE!!!");
    }

    /**
     * Check if the configuration has changed since the last time it was loaded.
     *
     * @return a flag whether the configuration has changed
     */
    protected boolean hasChanged() {
        File file = getConfigurationFile();
        if (file == null || !file.exists()) {
            return false;
        }
        boolean result = file.lastModified() > lastModified;
        if (result) {
            notifyNeeded();
            log.debug(" AuthZ DB "+file.getName()+" is CHANGED ---> Notify needed..");
        }
        return result;
    }


    public long getLastReload() {
        return this.reloadingTime;
    }


    public File getConfigurationFile() {
        return this.configuration.getFile();
    }

}



