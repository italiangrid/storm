package it.grid.storm.namespace.config.xml;

import java.io.File;

import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class XMLReloadingStrategy extends FileChangedReloadingStrategy {

    private boolean notifing = false;
    private Logger log = LoggerFactory.getLogger(XMLReloadingStrategy.class);

    private boolean verbosity;
    private long reloadingTime;

    public void setVerbosity(boolean verbosity) {
        this.verbosity = verbosity;
    }

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

    @Override
    public boolean reloadingRequired() {

        boolean reloading = false;

        long now = System.currentTimeMillis();

        if (now > lastChecked + refreshDelay) {
            lastChecked = now;
            if (hasChanged()) {
                reloading = true;
            }
        }
        if (verbosity) {
            log.debug(" ...RELOADING REQUIRED? " + reloading);
        }

        return reloading;
    }

    @Override
    public void reloadingPerformed() {
        updateLastModified();
        this.reloadingTime = System.currentTimeMillis();

    }

    /**
     * Check if the configuration has changed since the last time it was loaded.
     *
     * @return a flag whether the configuration has changed
     */
    @Override
    protected boolean hasChanged() {
        //log.debug("Checking if Namespace Configuration is changed..");
        File file = getConfigurationFile();
        //File file = thigetFile();
        if (file == null || !file.exists()) {
            return false;
        }
        boolean result = file.lastModified() > lastModified;
        if (result) {
            notifyNeeded();
            log.debug(" <<<<<  Namespace Configuration is CHANGED ---> Notify needed..");
        }
        return result;
    }

    public File getConfigurationFile() {
        return this.configuration.getFile();
    }

    public long getLastReload() {
        return this.reloadingTime;
    }

}
