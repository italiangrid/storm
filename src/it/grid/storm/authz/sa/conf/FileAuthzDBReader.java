package it.grid.storm.authz.sa.conf;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.sa.AuthzDBReaderInterface;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;


public class FileAuthzDBReader extends Observable implements AuthzDBReaderInterface {

    private final Log log = AuthzDirector.getLogger();

    int delay = 4000; // delay for 5 sec.
    int period = 2000; // repeat every sec.

    private Timer timer = new Timer();
    private String authzDBPath = "";
    private String authzDBFileName;
    private int refreshInSec = 0;
    private boolean autoReload = false;
    private PropertiesConfiguration authzdb = null;
    private FileAuthzDBReloadingStrategy reloadingStrategy = null;

    private boolean verbose = false;

    /**
     *
     */
    public FileAuthzDBReader(String authzDBFileName, boolean versose) {
        log.debug("Created Anonymous FileAuthzDBReader : " + this.hashCode());
        this.verbose = verbose;
        try {
            init(authzDBFileName);
        } catch (AuthzDBReaderException ex) {
            log.error("Unable to load the AuthzDB file : " + authzDBFileName);
        }
    }


    private void init(String authzDBFileName) throws AuthzDBReaderException {
        Peeper peeper = new Peeper(this);
        timer.schedule(peeper, delay, period);
        log.debug("Timer initialized");
        bindDB(authzDBFileName);
    }

    /**
     * To implement the Observer - Observable pattern
     */
    public synchronized void setChanged() {
        super.setChanged();
    }

    public void setNotifyManaged() {
        this.reloadingStrategy.notifingPerformed();
        this.authzdb.setReloadingStrategy(this.reloadingStrategy);
    }

    public void setObserver(Observer obs) {
        this.addObserver(obs);
    }

    /**
     *
     * @param authzDBPath String
     */
    public void setAuthZDBPath(String authzDBPath) {
        this.authzDBPath = authzDBPath;
    }

    /**
     * bindDB
     *
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBReaderInterface
     *   method
     */
    public void bindDB(String dbFileName) throws AuthzDBReaderException {
        this.authzDBFileName = dbFileName;
        if (authzDBPath.equals("")) {
            authzDBPath = System.getProperty("user.dir") + File.separator + "etc";
        }
        authzDBFileName = authzDBPath + File.separator + dbFileName;

        try {
            authzdb = new PropertiesConfiguration(authzDBFileName);
            log.debug("Bound FileAuthzDBReader with " + authzDBFileName);
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
            throw new AuthzDBReaderException();
        }
        if (autoReload) {
            //create reloading strategy for refresh
            reloadingStrategy = new FileAuthzDBReloadingStrategy();
            reloadingStrategy.setRefreshDelay(refreshInSec);
            authzdb.setReloadingStrategy(reloadingStrategy);
            FileAuthzDBListener authzListener = new FileAuthzDBListener(authzDBFileName);
            authzdb.addConfigurationListener(authzListener);
            log.debug("Configured FileAuthzDBReader " + authzDBFileName + "in AutoReload");
        }
    }

    /**
     * setAutomaticReload
     *
     * @param autoReload boolean
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBReaderInterface
     *   method
     */
    public void setAutomaticReload(boolean autoReload, int timeIntervalInSeconds) {
        this.autoReload = autoReload;
        this.refreshInSec = timeIntervalInSeconds;
    }


    /**
     * loadAuthZDB
     *
     * @throws AuthzDBReaderException
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBReaderInterface
     *   method
     */
    public void loadAuthZDB() throws AuthzDBReaderException {
        try {
            authzdb.clear();
            authzdb.load(authzDBFileName);
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
            throw new AuthzDBReaderException();
        }
    }

    /**
     *
     */
    public void printAuthzDB() {
        log.debug("Properties read from file:");
        String key;
        for (Iterator i = authzdb.getKeys(); i.hasNext(); ) {
            key = (String) i.next();
            log.debug(key + "=" + authzdb.getProperty(key).toString());
        }

    }

    /**
     * getDB
     *
     * @return AuthzDBInterface
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBReaderInterface
     *   method
     */
    public PropertiesConfiguration getAuthzDB() {
        return authzdb;
    }




    /***************************************************************** RELOADER ****************
     */
    private class Peeper extends TimerTask {

        private FileAuthzDBReloadingStrategy reloadingStrategy;

        private boolean signal;
        private FileAuthzDBReader observed;

        public Peeper(FileAuthzDBReader obs) {
            this.observed = obs;
        }

        public void run() {
            //log.debug(" The glange of peeper..");
            reloadingStrategy = (FileAuthzDBReloadingStrategy) authzdb.getReloadingStrategy();
            if (verbose) {
                File authzDBFile = reloadingStrategy.getConfigurationFile();
                log.debug(" Peeper glance FILE : " + authzDBFileName);
                long lastFileModified = authzDBFile.lastModified();
                Date dateFile = new Date(lastFileModified);
                long lastFileModifiedReload = reloadingStrategy.getLastReload();
                reloadingStrategy.reloadingPerformed();
                Date dateReload = new Date(lastFileModifiedReload);
                if (lastFileModifiedReload < lastFileModified) {
                    log.debug("RELOAD NEEDED!");
                    Format formatter = new SimpleDateFormat("HH.mm.ss  dd.MM.yyyy");
                    log.debug(" FILE AuthzDB " + authzDBFileName + " Last Modified : " + formatter.format(dateFile));
                    log.debug(" FILE AuthzDB " + authzDBFileName + " Last RELOAD : " + formatter.format(dateReload));
                }
            }
            boolean changed = reloadingStrategy.reloadingRequired();
            if (changed) {
                log.debug(" FILE AuthzDB " + authzDBFileName + " is changed ! ");
                //log.debug(" ... CHECK of VALIDITY of NAMESPACE Configuration ...");
                log.debug(" ----> RELOADING  ");
                authzdb.reload();
                reloadingStrategy.reloadingPerformed();
            }

            signal = reloadingStrategy.notifingRequired();
            if ((signal)) {
                observed.setChanged();
                observed.notifyObservers(" MSG : Namespace is changed!");
                reloadingStrategy.notifingPerformed();
            }

        }

    }

}
