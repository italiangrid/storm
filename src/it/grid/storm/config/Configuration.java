/**************************************************************************
 * This file is part of the StoRM project. Copyright (c) 2003-2009 INFN. All rights reserved. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 ***********************************************************************/

package it.grid.storm.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Singleton holding all configuration values that any other object in the StoRM backend reads from configuration files,
 * databases, etc. Implements a 'get<something>' method for each value that should be looked up this way. In fact, this
 * is a "read-only" class. If no value is specified in the configuration medium, a default one is used instead; some
 * properties may hold several comma separated values without any white spaces in-between; the name of the property in
 * the configuration medium, default values, as well as the option of holding multiple values, is specified in each
 * method comment.
 */

public class Configuration {

    private static Logger log = LoggerFactory.getLogger(Configuration.class);

    private ConfigReader cr = new ConfigReader(); // set an empty ConfigReader
    // as default
    static Configuration instance = new Configuration(); // only

    // instance of
    // this
    // configuration
    // class

    private Configuration() {
    }

    /**
     * Returns the sole instance of the Configuration class.
     */
    public static Configuration getInstance() {
        return Configuration.instance;
    }

    /**
     * Method used to set the config reader: if a null is supplied then a default empty ConfigReader is used instead.
     */
    public void setConfigReader(ConfigReader cr) {
        if (cr != null) {
            this.cr = cr;
        }
    }

    /*
        ############################################################
        ##
        ##   MANDATORY PROPERTIES
        ##
        ############################################################
        
         NEW KEY                              |   OLD KEY
       ---------------------------------------+--------------------------------------------------------
        storm.service.SURL.hostname           |   storm.service.hostname
        storm.service.SURL.port               |   storm.service.port           fe.port 
        storm.service.SURL.service-path       |   storm.service.endpoint
                                              |
        storm.service.FE-list.hostnames       |   storm.machinenames
        storm.service.FE-list.IPs             |   storm.machineIPs
                                              |
        storm.service.request-db.dbms-vendor  |   asynch.picker.db.driver       asynch.picker.db.protocol
        storm.service.request-db.host         |   asynch.picker.db.host
        storm.service.request-db.db-name      |   asynch.picker.db.name
        storm.service.request-db.username     |   asynch.picker.db.username
        storm.service.request-db.passwd       |   asynch.picker.db.passwd
                                              |
        --------------------------------------+----------------------------------------------------------
    *
    */
    /**
     * 
     * MANDATORY CONFIGURATION PARAMETER!
     * Define the SURL endpoints.
     * 
     * @return String[]
     */
    public String[] getManagedSURLs() {
        String key = "storm.service.SURL.endpoint";
        String[] defaultValue = { "UNDEFINED_SERVICE_ENDPOINT" };
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return defaultValue;
        } else {
            // load from external source
            return cr.getConfiguration().getStringArray(key);
        }
    }

    /**
     * 
     * @return String
     */
    public String getServiceHostname() {
        String key = "storm.service.FE-public.hostname";
        String defaultValue = "UNDEFINED_STORM_HOSTNAME";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return defaultValue;
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by SFN to establish the FE binding port. 
     * 
     * If no value is found in the configuration medium, then the default one is used instead. 
     * key="storm.service.port"; 
     * default value="8444"
     */
    public int getServicePort() {
        String key = "storm.service.port";
        int defaultValue = 8444;
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return defaultValue;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used to get a List of Strings of the names of the machine hosting the FE for _this_ StoRM instance! Used
     * in srmCopy to understand if the fromSURL/toSURL refer to the server itself or to some other foreign server! The
     * List contains Strings in _lower_case_!!! If no value is found in the configuration medium, then the default value
     * is returned instead. key="storm.machinenames"; default value={"testbed006.cnaf.infn.it"};
     */
    public List<String> getListOfMachineNames() {
        String key = "storm.service.FE-list.hostnames";
        if (cr.getConfiguration().containsKey(key)) {
            String[] names = cr.getConfiguration().getStringArray(key);

            for (int i = 0; i < names.length; i++) {
                names[i] = names[i].trim().toLowerCase();
            }
            return Arrays.asList(names);
        } else {
            return Arrays.asList(new String[] { "localhost" });
        }
    }

    /**
     * Method used to get a List of Strings of the IPs of the machine hosting the FE for _this_ StoRM instance! Used in
     * the xmlrcp server configuration, to allow request coming from the specified IP. (Into the xmlrpc server the
     * filter is done by IP, not hostname.) This paramter is mandatory when a distribuited FE-BE installation of StoRM
     * is used togheter with a dynamic DNS on the FE hostname. In that case the properties storm.machinenames is not
     * enough meaningfull. If no value is found in the configuration medium, then the default value is returned instead.
     * key="storm.machineIPs"; default value={"127.0.0.1"};
     */
    public List<String> getListOfMachineIPs() {
        String key = "storm.service.FE-list.IPs";

        if (cr.getConfiguration().containsKey(key)) {

            String[] names = cr.getConfiguration().getString(key).split(";"); // split
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i].trim().toLowerCase(); // for each bit remove
            }
            return Arrays.asList(names);

        } else {
            return Arrays.asList(new String[] { "127.0.0.1" });
        }
    }

    /**
     * Method used by all DAO Objects to get the DataBase Driver. If no value is found in the configuration medium, then
     * the default value is returned instead. key="asynch.picker.db.driver"; default value="com.mysql.jdbc.Driver";
     */
    public String getDBDriver() {
        String key = "storm.service.request-db.dbms-vendor";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "com.mysql.jdbc.Driver";
        } else {
            // load from external source
            String vendor = cr.getConfiguration().getString(key);
            String driver = "";
            if (vendor.toLowerCase().equals("mysql")) {
                driver = "com.mysql.jdbc.Driver";
            } else {
                log.error("CONFIG ERROR 'RDBMS Vendor ('" + vendor + "')unknown.' ");
            }
            return driver;
        }
    }

    /**
     * Method used by all DAO Objects to get DB URL. If no value is found in the configuration medium, then the default
     * value is returned instead. key1="asynch.picker.db.protocol"; default value="jdbc:mysql://";
     * key2="asynch.picker.db.host"; default value="localhost"; key3="asynch.picker.db.name"; default value="storm_db";
     * The returned value is made up of the above default values and whatever is read from the configuration medium,
     * combined in the following way: protocol + host + "/" + name
     */
    public String getDBURL() {
        String prefix = "";
        String key1 = "storm.service.request-db.protocol";
        String host = "";
        String key2 = "storm.service.request-db.host";
        String name = "";
        String key3 = "storm.service.request-db.db-name";
        // get prefix...
        if (!cr.getConfiguration().containsKey(key1)) {
            // use default
            prefix = "jdbc:mysql://";
        } else {
            // load from external source
            prefix = cr.getConfiguration().getString(key1);
        }
        // get host...
        if (!cr.getConfiguration().containsKey(key2)) {
            // use default
            host = "localhost";
        } else {
            // load from external source
            host = cr.getConfiguration().getString(key2);
        }
        // get db name...
        if (!cr.getConfiguration().containsKey(key3)) {
            // use default
            name = "storm_db";
        } else {
            // load from external source
            name = cr.getConfiguration().getString(key3);
        }
        // return value...
        return prefix + host + "/" + name;
    }

    /**
     * Method used by all DAO Objects to get the DB username. If no value is found in the configuration medium, then the
     * default value is returned instead. Default value = "storm"; key searched in medium = "asynch.picker.db.username".
     */
    public String getDBUserName() {
        String key = "storm.service.request-db.username";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "storm";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by all DAO Objects to get the DB password. If no value is found in the configuration medium, then the
     * default value is returned instead. Deafult value = "storm"; key searched in medium = "asynch.picker.db.passwd".
     */
    public String getDBPassword() {
        String key = "storm.service.request-db.passwd";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "storm";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /*
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    ##
    ##   END definition of MANDATORY PROPERTIESs
    ##
    ############################################################
    */

    /**
     * Method used by all DAOs to establish the reconnection period in _seconds_: after such period the DB connection
     * will be closed and re-opened. Beware that after such time expires, the connection is _not_ automatically closed
     * and reopened; rather, it acts as a flag that is considered by the main code and when the most appropriate time
     * comes, the connection is closed and reopened. This is because of MySQL bug that does not allow a connection to
     * remain open for an arbitrary amount of time! Else an Unexpected EOF Exception gets thrown by the JDBC driver! If
     * no value is found in the configuration medium, then the default value is returned instead.
     * key="asynch.db.ReconnectPeriod"; default value=18000; Keep in mind that 18000 seconds = 5 hours.
     */
    public long getDBReconnectPeriod() {
        String key = "asynch.db.ReconnectPeriod";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 18000; // 18000 sec = 5 hours
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by all DAOs to establish the reconnection delay in _seconds_: when StoRM is first launched it will
     * wait for this amount of time before starting the timer. This is because of MySQL bug that does not allow a
     * connection to remain open for an arbitrary amount of time! Else an Unexpected EOF Exception gets thrown by the
     * JDBC driver! If no value is found in the configuration medium, then the default value is returned instead.
     * key="asynch.db.ReconnectDelay"; default value=30;
     */
    public long getDBReconnectDelay() {
        String key = "asynch.db.DelayPeriod";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 30;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by PinnedFilesCatalog to get the initial delay in _seconds_ before starting the cleaning thread. If
     * no value is found in the configuration medium, then the default value is returned instead.
     * key="pinnedfiles.cleaning.delay"; default value=10;
     */
    public long getCleaningInitialDelay() {
        String key = "gc.pinnedfiles.cleaning.delay";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by PinnedFilesCatalog to get the cleaning time interval, in _seconds_. If no value is found in the
     * configuration medium, then the default value is returned instead. key="pinnedfiles.cleaning.interval"; default
     * value=300; Keep in mind that 300 seconds = 5 minutes.
     */
    public long getCleaningTimeInterval() {
        String key = "gc.pinnedfiles.cleaning.interval";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 300;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Get the default file size
     * 
     * @return
     */
    public long getFileDefaultSize() {
        String key = "fileSize.default";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return (1024 * 1024); // 1 MB
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by VolatileAndJiTCatalog to get the default fileLifetime to use when a volatile entry is being
     * added/updated, but the user specified a non positive value. Measured in _seconds_. If no value is found in the
     * configuration medium, then the default value is returned instead. key="fileLifetime.default"; default value=3600;
     */
    public long getFileLifetimeDefault() {
        String key = "fileLifetime.default";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 3600;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by VolatileAndJiTCatalog to get the minimum pinLifetime allowed, when a jit is being added/updated,
     * but the user specified a lower one. This method is also used by the PinLifetimeConverter to translate a
     * NULL/0/negative value to a default one. Measured in _seconds_. If no value is found in the configuration medium,
     * then the default value is returned instead. key="pinLifetime.minimum"; default value=30;
     */
    public long getPinLifetimeMinimum() {
        String key = "pinLifetime.minimum";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 259200;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by VolatileAndJiTCatalog to get the maximum pinLifetime allowed, when a jit is being added/updated,
     * but the user specified a higher one. Measured in _seconds_. If no value is found in the configuration medium,
     * then the default value is returned instead. key="pinLifetime.maximum"; default value=144000 (40 hours);
     */
    public long getPinLifetimeMaximum() {
        String key = "pinLifetime.maximum";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 1814400;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by PtPChunkCatalog to get the initial delay in _seconds_ before starting the transiting thread. If no
     * value is found in the configuration medium, then the default value is returned instead. key="transit.delay";
     * default value=10;
     */
    public long getTransitInitialDelay() {
        String key = "transit.delay";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by PtPChunkCatalog to get the transiting time interval, in _seconds_. If no value is found in the
     * configuration medium, then the default value is returned instead. key="transit.interval"; default value=300; Keep
     * in mind that 300 seconds = 5 minutes.
     */
    public long getTransitTimeInterval() {
        String key = "transit.interval";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 300;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by AdvancedPicker to get the initial delay before starting to pick data from the DB, in _seconds_. If
     * no value is found in the configuration medium, then the default value is returned instead.
     * key="asynch.PickingInitialDelay"; default value=5;
     */
    public long getPickingInitialDelay() {
        String key = "asynch.PickingInitialDelay";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 1;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by AdvancedPicker to get the time interval of successive pickings, in _seconds_. If no value is found
     * in the configuration medium, then the default value is returned instead. key="asynch.PickingTimeInterval";
     * default value=15;
     */
    public long getPickingTimeInterval() {
        String key = "asynch.PickingTimeInterval";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 2;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by RequestSummaryDAO to establish the maximum number of requests to retrieve with each polling. If no
     * value is found in the configuration medium, then the default value is returned instead.
     * key="asynch.PickingMaxBatchSize"; default value=30;
     */
    public int getPickingMaxBatchSize() {
        String key = "asynch.PickingMaxBatchSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by CopyChunk when making a remote srmPrepareToPut (Push Mode). It needs it to estalish the
     * totalRetryTime in seconds to supply to the internal SRMClient. The parameter is passed to the prepareToPut
     * functionality. If no value is found in the configuration medium, then the default value is returned instead.
     * key="asynch.srmclient.retrytime"; default value=60;
     */
    public long getSRMClientPutTotalRetryTime() {
        String key = "asynch.srmclient.retrytime";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 60;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by CopyChunk when making a remote srmPrepareToPut (Push Mode). The CopyChunk will periodically invoke
     * the statusOfPutRequest functionality of the internal SRMClient, for at most the time out interval in seconds
     * returned by this method. If no value is found in the configuration medium, then the default value is returned
     * instead. key="asynch.srmclient.timeout"; default value=180;
     */
    public long getSRMClientPutTimeOut() {
        String key = "asynch.srmclient.timeout";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 180;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by CopyChunk when making a remote srmPrepareToPut (Push Mode). The CopyChunk will wait the amount of
     * time in seconds returned by this method, before invoking again the statusOfPutRequest functionality of the
     * internal SRMClient. That is, it tells the polling interval. If no value is found in the configuration medium,
     * then the default value is returned instead. key="asynch.srmclient.sleeptime"; default value=5;
     */
    public long getSRMClientPutSleepTime() {
        String key = "asynch.srmclient.sleeptime";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 5;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by CopyChunk when making the FileTransfer and finally invoking a remote srmPutDone. The CopyChunk
     * will wait the amount of time in seconds returned by this method, before invoking again the srmPutDone
     * functionality of the internal SRMClient. That is, it tells the time interval between successive invocations: they
     * are necessary when the returned status is SRM_INTERNAL_ERROR which denotes a transient error situation. If no
     * value is found in the configuration medium, then the default value is returned instead.
     * key="asynch.srmclient.putdone.sleeptime"; default value=2;
     */
    public long getSRMClientPutDoneSleepTime() {
        String key = "asynch.srmclient.putdone.sleeptime";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 1;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Method used by CopyChunk when making the FileTransfer and finally invoking a remote srmPutDone. The CopyChunk may
     * have to periodically invoke srmPutDone functionality of the internal SRMClient if the web service returns
     * SRM_INTERNAL_ERROR; in that case invocations will be attempted for at most the time out interval in seconds
     * returned by this method. If no value is found in the configuration medium, then the default value is returned
     * instead. key="asynch.srmclient.putdone.timeout"; default value=60;
     */
    public long getSRMClientPutDoneTimeOut() {
        String key = "asynch.srmclient.putdone.timeout";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 60;
        } else {
            // load from external source
            return cr.getConfiguration().getLong(key);
        }
    }

    /**
     * Get max number of xmlrpc threads into for the XMLRPC server.
     */
    public int getMaxXMLRPCThread() {
        String key = "synchcall.xmlrpc.maxthread";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Get Default Space Tokens
     * 
     * @return
     */
    public List<String> getListOfDefaultSpaceToken() {
        String key = "storm.service.defaultSpaceTokens";

        if (cr.getConfiguration().containsKey(key)) {

            String[] namesArray = cr.getConfiguration().getStringArray(key);
            if (namesArray == null) {
                return new ArrayList<String>();
            }
            return Arrays.asList(namesArray);

        } else {
            return new ArrayList<String>();
        }
    }

    /**
     * Method used by Factory invoked in CopyChunk subclasses, to instantiate a GridFTPTransferClient. The String
     * returned specifies the name of the class to instantiate; for now, there are two classes:
     * NaiveGridFTPTransferClient and StubGridFTPTransferClient. If no value is found in the configuration medium, then
     * the default value is returned instead. key="asynch.gridftpclient"; default
     * value="it.grid.storm.asynch.NaiveGridFTPTransferClient";
     */
    public String getGridFTPTransferClient() {
        String key = "asynch.gridftpclient";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "it.grid.storm.asynch.NaiveGridFTPTransferClient";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by Factory invoked in CopyChunk subclasses, to instantiate an SRMClient. The String returned
     * specifies the name of the class to instantiate; for now, there are two classes: NaiveSRMClient and StubSRMClient.
     * If no value is found in the configuration medium, then the default value is returned instead.
     * key="asynch.srmclient"; default value="it.grid.storm.asynch.SRM22Client";
     */
    public String getSRMClient() {
        String key = "asynch.srmclient";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "it.grid.storm.asynch.SRM22Client";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method that returns the directory holding the configuration file. The methods that make use of it are
     * uncertain... must be found soon!!! Beware that the configuration directory is implicit in the complete pathname
     * to the configuration file supplied in the command line when starting StoRM BE.
     */
    public String configurationDir() {
        return cr.configurationDirectory();
    }

    /**
     * getNamespaceConfigPath
     * 
     * @return String
     */
    public String namespaceConfigPath() {
        String storm_home = System.getProperty("user.dir");
        String configDir = storm_home + File.separator + "etc";
        return configDir;
    }

    /**
     * Method used by Space Reservation component to get the complete pathname for the "namespace.xml" configuration
     * File. This configuration file contains information about the mapping between user, vo, storage area, default
     * lifetime and filetype. This method searches the configuration medium for the configuration directory, and if it
     * does not find it then a default value for such directory is used. The returned String is of the form:
     * storm.configuration.dir + "/" + "namespace.xml" key=storm.configuration.dir; default value="/home/storm/config";
     */
    /*    public String getNamespaceConfigurationFile() {
            String dirValue = "";
            dirValue = cr.configurationDirectory();
            String config_file = dirValue;
            if (!config_file.endsWith(java.io.File.separator)) {
                config_file = config_file + java.io.File.separator;
            }
            config_file = config_file + "namespace.xml";
            return config_file;
        }
    */

    /**
     * Method used in filesystem wrapper to get the directory path for temporary file creation: StoRM crates a temporary
     * file to manage GPFS ACL, and these files are created in the temporary directory specified here. If no value is
     * found in the configuration medium, then the default value is returned instead.
     * key="wrapper.filesystem.acl.tmpdir"; default value="/tmp";
     */
    /*
     * public String getTempDir() { String key = "wrapper.filesystem.acl.tmpdir"; if
     * (!cr.getConfiguration().containsKey(key)) { // return default return "/tmp"; } else { // load from external
     * source return cr.getConfiguration().getString(key); } }
     */

    /**
     * Method used by StoRMCommandServer to establish the listening port to which it should bind. If no value is found
     * in the configuration medium, then the default value is returned instead. key="storm.commandserver.port"; default
     * value=4444;
     */
    public int getCommandServerBindingPort() {
        String key = "storm.commandserver.port";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 4444;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by Dispatcher and Feeder objects to check if a serial scheduler must be used, or not. If no value is
     * found in the configuration medium, then the default value is returned instead. key="scheduler.serial"; default
     * value=false;
     */
    public boolean getSerialScheduler() {
        String key = "scheduler.serial";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return false;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key);
        }
    }

    /**
     * Method used in Persistence Component It returns the DB vendor name. If no value is found in the configuration
     * medium, then the default value is returned instead. key="persistence.db.vendor"; default value="mysql";
     */
    public String getBE_PersistenceDBVendor() {
        String key = "persistence.internal-db.dbms-vendor";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "mysql";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used in Persistence Component: it returns the host where the DB resides. If no value is found in the
     * configuration medium, then the default value is returned instead. key="persistence.db.host"; default
     * value="localhost";
     */
    public String getBE_PersistenceDBMSUrl() {
        String key = "persistence.internal-db.host";
        String alternativeKey = "storm.service.request-db.host";
        if (!cr.getConfiguration().containsKey(key)) {
            // Try with the alternative key
            if (!cr.getConfiguration().containsKey(alternativeKey)) {
                // return default
                return "localhost";
            } else {
                return cr.getConfiguration().getString(alternativeKey);
            }
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used in Persistence Component it returns the name of the DB to use. If no value is found in the
     * configuration medium, then the default value is returned instead. key="persistence.db.name"; default
     * value="storm_be_ISAM";
     */
    public String getBE_PersistenceDBName() {
        String key = "persistence.internal-db.db-name";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "storm_be_ISAM";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used in Persistence Component it returns the name of the DB user that must be used. If no value is found
     * in the configuration medium, then the default value is returned instead. key="persistence.db.username"; default
     * value="storm";
     */
    public String getBE_PersistenceDBUserName() {
        String key = "persistence.internal-db.username";
        String alternativeKey = "storm.service.request-db.username";
        if (!cr.getConfiguration().containsKey(key)) {
            // Try with the alternative key
            if (!cr.getConfiguration().containsKey(alternativeKey)) {
                // return default
                return "storm";
            } else {
                return cr.getConfiguration().getString(alternativeKey);
            }
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used in Persistence Component it returns the password for the DB user that must be used. If no value is
     * found in the configuration medium, then the default value is returned instead. key="persistence.db.passwd";
     * default value="storm";
     */
    public String getBE_PersistenceDBPassword() {
        String key = "persistence.internal-db.passwd";
        String alternativeKey = "storm.service.request-db.username";
        if (!cr.getConfiguration().containsKey(key)) {
            // Try with the alternative key
            if (!cr.getConfiguration().containsKey(alternativeKey)) {
                // return default
                return "storm";
            } else {
                return cr.getConfiguration().getString(alternativeKey);
            }
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used in Persistence Component it returns a boolean indicating whether to use connection pooling or not. If
     * no value is found in the configuration medium, then the default value is returned instead.
     * key="persistence.db.pool"; default value=false;
     */
    public boolean getBE_PersistencePoolDB() {
        String key = "persistence.internal-db.connection-pool";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return false;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key);
        }
    }

    /**
     * Method used in Persistence Component it returns an int indicating the maximum number of active connections in the
     * connection pool. It is the maximum number of active connections that can be allocated from this pool at the same
     * time... 0 (zero) for no limit. If no value is found in the configuration medium, then the default value is
     * returned instead. key="persistence.db.pool.maxActive"; default value=10;
     */
    public int getBE_PersistencePoolDB_MaxActive() {
        String key = "persistence.internal-db.connection-pool.maxActive";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used in Persistence Component it returns an int indicating the maximum waiting time in _milliseconds_ for
     * the connection in the pool. It represents the time that the pool will wait (when there are no available
     * connections) for a connection to be returned before throwing an exception... a value of -1 to wait indefinitely.
     * If no value is found in the configuration medium, then the default value is returned instead.
     * key="persistence.db.pool.maxWait"; default value=50;
     */
    public int getBE_PersistencePoolDB_MaxWait() {
        String key = "persistence.internal-db.connection-pool.maxWait";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 50;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Synch Component to set the binding port for the _unsecure_ xmlrpc server in the BE. If no
     * value is found in the configuration medium, then the default value is returned instead.
     * key="synchcall.xmlrpc.unsecureServerPort"; default value=8080;
     */
    public int getXmlRpcServerPort() {
        String key = "synchcall.xmlrpc.unsecureServerPort";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 8080;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Synch Component to set the maximum number of entries to return for the srmLs functionality. If
     * no value is found in the configuration medium, then the default value is returned instead.
     * key="synchcall.directoryManager.maxLsEntry"; default value=500;
     * 
     * @return int
     */
    public int get_LS_MaxNumberOfEntry() {
        String key = "synchcall.directoryManager.maxLsEntry";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 500;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Default value for the parameter "allLevelRecursive" of the LS request.
     * 
     * @return boolean
     */
    public boolean get_LS_allLevelRecursive() {

        String key = "synchcall.directoryManager.default.AllLevelRecursive";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return false;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key);
        }
    }

    /**
     * Default value for the parameter "numOfLevels" of the LS request.
     * 
     * @return int
     */
    public int get_LS_numOfLevels() {
        String key = "synchcall.directoryManager.default.Levels";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 1;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Default value for the parameter "offset" of the LS request.
     * 
     * @return int
     */
    public int get_LS_offset() {
        String key = "synchcall.directoryManager.default.Offset";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 0;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Core Poolsize for the srmPrepareToPut management.
     * Scheduler component uses a thread pool. Scheduler pool will automatically adjust the pool size according to the
     * bounds set by corePoolSize and maximumPoolSize. When a new task is submitted in method execute, and fewer than
     * corePoolSize threads are running, a new thread is created to handle the request, even if other worker threads are
     * idle. If there are more than corePoolSize but less than maximumPoolSize threads running, a new thread will be
     * created only if the queue is full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size
     * thread pool. corePoolSize - the number of threads to keep in the pool, even if they are idle. If no value is
     * found in the configuration medium, then the default value is returned instead.
     * key="scheduler.chunksched.ptp.workerCorePoolSize"; default value=10;
     */
    public int getPtPCorePoolSize() {
        String key = "scheduler.chunksched.ptp.workerCorePoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Max Pool Size for the srmPrepareToPut management.
     * Scheduler component uses a thread pool. Scheduler pool will automatically adjust the pool size according to the
     * bounds set by corePoolSize and maximumPoolSize. When a new task is submitted in method execute, and fewer than
     * corePoolSize threads are running, a new thread is created to handle the request, even if other worker threads are
     * idle. If there are more than corePoolSize but less than maximumPoolSize threads running, a new thread will be
     * created only if the queue is full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size
     * thread pool. maxPoolSize - the maximum number of threads to allow in the pool. If no value is found in the
     * configuration medium, then the default value is returned instead.
     * key="scheduler.chunksched.ptp.workerMaxPoolSize"; default value=100;
     */
    public int getPtPMaxPoolSize() {
        String key = "scheduler.chunksched.ptp.workerMaxPoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Queue Size for the srmPrepareToPut management. If no value is
     * found in the configuration medium, then the default value is returned instead. Scheduler hold a blocking priority
     * queue used to transfer and hols submitted tasks. The use of this queue interacts with pool sizing: - If fewer
     * than corePoolSize threads are running, the Scheduler always prefers adding a new thread rather than queuing. - If
     * corePoolSize or more threads are running, the Scheduler always prefers queuing a request rather than adding a new
     * thread. - If a request cannot be queued, a new thread is created unless this would exceed maxPoolSize, in which
     * case, the task will be rejected. QueueSize - The initial capacity for this priority queue used for holding tasks
     * before they are executed. The queue will hold only the Runnable tasks submitted by the execute method.
     * key="scheduler.chunksched.ptp.queueSize"; default value=100;
     */
    public int getPtPQueueSize() {
        String key = "scheduler.chunksched.ptp.queueSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Core Pool Size for the srmPrepareToGet management. If no
     * value is found in the configuration medium, then the default value is returned instead. Scheduler component uses
     * a thread pool. Scheduler pool will automatically adjust the pool size according to the bounds set by corePoolSize
     * and maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize threads are
     * running, a new thread is created to handle the request, even if other worker threads are idle. If there are more
     * than corePoolSize but less than maximumPoolSize threads running, a new thread will be created only if the queue
     * is full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool. corePoolSize
     * - the number of threads to keep in the pool, even if they are idle.
     * key="scheduler.chunksched.ptg.workerCorePoolSize"; default value=10;
     */
    public int getPtGCorePoolSize() {
        String key = "scheduler.chunksched.ptg.workerCorePoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Max Pool Size for the srmPrepareToGet management. If no
     * value is found in the configuration medium, then the default value is returned instead. Scheduler component uses
     * a thread pool. Scheduler pool will automatically adjust the pool size according to the bounds set by corePoolSize
     * and maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize threads are
     * running, a new thread is created to handle the request, even if other worker threads are idle. If there are more
     * than corePoolSize but less than maximumPoolSize threads running, a new thread will be created only if the queue
     * is full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool. maxPoolSize -
     * the maximum number of threads to allow in the pool. key="scheduler.chunksched.ptg.workerMaxPoolSize"; default
     * value=100;
     */
    public int getPtGMaxPoolSize() {
        String key = "scheduler.chunksched.ptg.workerMaxPoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Queue Size for the srmPrepareToGet management. If no value is
     * found in the configuration medium, then the default value is returned instead. Scheduler hold a blocking priority
     * queue used to transfer and hols submitted tasks. The use of this queue interacts with pool sizing: - If fewer
     * than corePoolSize threads are running, the Scheduler always prefers adding a new thread rather than queuing. - If
     * corePoolSize or more threads are running, the Scheduler always prefers queuing a request rather than adding a new
     * thread. - If a request cannot be queued, a new thread is created unless this would exceed maxPoolSize, in which
     * case, the task will be rejected. QueueSize - The initial capacity for this priority queue used for holding tasks
     * before they are executed. The queue will hold only the Runnable tasks submitted by the execute method.
     * key="scheduler.chunksched.ptg.queueSize"; default value=100;
     */
    public int getPtGQueueSize() {
        String key = "scheduler.chunksched.ptg.queueSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Core Pool Size for the srmCopy management. If no value
     * is found in the configuration medium, then the default value is returned instead. Scheduler component uses a
     * thread pool. Scheduler pool will automatically adjust the pool size according to the bounds set by corePoolSize
     * and maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize threads are
     * running, a new thread is created to handle the request, even if other worker threads are idle. If there are more
     * than corePoolSize but less than maximumPoolSize threads running, a new thread will be created only if the queue
     * is full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool. corePoolSize
     * - the number of threads to keep in the pool, even if they are idle.
     * key="scheduler.chunksched.copy.workerCorePoolSize"; default value=10;
     */
    public int getCopyCorePoolSize() {
        String key = "scheduler.chunksched.copy.workerCorePoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Max Pool Size for the srmCopy management. If no value is
     * found in the configuration medium, then the default value is returned instead. Scheduler component uses a thread
     * pool. Scheduler pool will automatically adjust the pool size according to the bounds set by corePoolSize and
     * maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize threads are running,
     * a new thread is created to handle the request, even if other worker threads are idle. If there are more than
     * corePoolSize but less than maximumPoolSize threads running, a new thread will be created only if the queue is
     * full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool. maxPoolSize -
     * the maximum number of threads to allow in the pool. key="scheduler.chunksched.copy.workerMaxPoolSize"; default
     * value=100;
     */
    public int getCopyMaxPoolSize() {
        String key = "scheduler.chunksched.copy.workerMaxPoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Queue Size for the srmCopy management. If no value is found in
     * the configuration medium, then the default value is returned instead. Scheduler hold a blocking priority queue
     * used to transfer and hols submitted tasks. The use of this queue interacts with pool sizing: - If fewer than
     * corePoolSize threads are running, the Scheduler always prefers adding a new thread rather than queuing. - If
     * corePoolSize or more threads are running, the Scheduler always prefers queuing a request rather than adding a new
     * thread. - If a request cannot be queued, a new thread is created unless this would exceed maxPoolSize, in which
     * case, the task will be rejected. QueueSize - The initial capacity for this priority queue used for holding tasks
     * before they are executed. The queue will hold only the Runnable tasks submitted by the execute method.
     * key="scheduler.chunksched.copy.queueSize"; default value=100;
     */
    public int getCopyQueueSize() {
        String key = "scheduler.chunksched.copy.queueSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Core Pool Size for the srmBoL management. If no value is
     * found in the configuration medium, then the default value is returned instead. Scheduler component uses a thread
     * pool. Scheduler pool will automatically adjust the pool size according to the bounds set by corePoolSize and
     * maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize threads are running,
     * a new thread is created to handle the request, even if other worker threads are idle. If there are more than
     * corePoolSize but less than maximumPoolSize threads running, a new thread will be created only if the queue is
     * full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool. corePoolSize -
     * the number of threads to keep in the pool, even if they are idle.
     * key="scheduler.chunksched.copy.workerCorePoolSize"; default value=10;
     */
    public int getBoLCorePoolSize() {
        String key = "scheduler.chunksched.bol.workerCorePoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Max Pool Size for the srmBoL management. If no value is
     * found in the configuration medium, then the default value is returned instead. Scheduler component uses a thread
     * pool. Scheduler pool will automatically adjust the pool size according to the bounds set by corePoolSize and
     * maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize threads are running,
     * a new thread is created to handle the request, even if other worker threads are idle. If there are more than
     * corePoolSize but less than maximumPoolSize threads running, a new thread will be created only if the queue is
     * full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool. maxPoolSize -
     * the maximum number of threads to allow in the pool. key="scheduler.chunksched.copy.workerMaxPoolSize"; default
     * value=100;
     */
    public int getBoLMaxPoolSize() {
        String key = "scheduler.chunksched.bol.workerMaxPoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Queue Size for the srmBoL management. If no value is found in
     * the configuration medium, then the default value is returned instead. Scheduler hold a blocking priority queue
     * used to transfer and hols submitted tasks. The use of this queue interacts with pool sizing: - If fewer than
     * corePoolSize threads are running, the Scheduler always prefers adding a new thread rather than queuing. - If
     * corePoolSize or more threads are running, the Scheduler always prefers queuing a request rather than adding a new
     * thread. - If a request cannot be queued, a new thread is created unless this would exceed maxPoolSize, in which
     * case, the task will be rejected. QueueSize - The initial capacity for this priority queue used for holding tasks
     * before they are executed. The queue will hold only the Runnable tasks submitted by the execute method.
     * key="scheduler.chunksched.copy.queueSize"; default value=100;
     */
    public int getBoLQueueSize() {
        String key = "scheduler.chunksched.bol.queueSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Core Pool Size for the Crusher. If no value is found in
     * the configuration medium, then the default value is returned instead. Scheduler component uses a thread pool.
     * Scheduler pool will automatically adjust the pool size according to the bounds set by corePoolSize and
     * maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize threads are running,
     * a new thread is created to handle the request, even if other worker threads are idle. If there are more than
     * corePoolSize but less than maximumPoolSize threads running, a new thread will be created only if the queue is
     * full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool. corePoolSize -
     * the number of threads to keep in the pool, even if they are idle. key="scheduler.crusher.workerCorePoolSize";
     * default value=10;
     */
    public int getCorePoolSize() {
        String key = "scheduler.crusher.workerCorePoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Worker Max Pool Size for the Crisher. If no value is found in
     * the configuration medium, then the default value is returned instead. Scheduler component uses a thread pool.
     * Scheduler pool will automatically adjust the pool size according to the bounds set by corePoolSize and
     * maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize threads are running,
     * a new thread is created to handle the request, even if other worker threads are idle. If there are more than
     * corePoolSize but less than maximumPoolSize threads running, a new thread will be created only if the queue is
     * full. By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool. maxPoolSize -
     * the maximum number of threads to allow in the pool. key="scheduler.crusher.workerMaxPoolSize"; default value=100;
     */
    public int getMaxPoolSize() {
        String key = "scheduler.crusher.workerMaxPoolSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by the Scheduler Component to get the Queue Size for the Crusher. If no value is found in the
     * configuration medium, then the default value is returned instead. Scheduler hold a blocking priority queue used
     * to transfer and hols submitted tasks. The use of this queue interacts with pool sizing: - If fewer than
     * corePoolSize threads are running, the Scheduler always prefers adding a new thread rather than queuing. - If
     * corePoolSize or more threads are running, the Scheduler always prefers queuing a request rather than adding a new
     * thread. - If a request cannot be queued, a new thread is created unless this would exceed maxPoolSize, in which
     * case, the task will be rejected. QueueSize - The initial capacity for this priority queue used for holding tasks
     * before they are executed. The queue will hold only the Runnable tasks submitted by the execute method.
     * key="scheduler.crusher.queueSize"; default value=100;
     */
    public int getQueueSize() {
        String key = "scheduler.crusher.queueSize";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 100;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * getNamespaceConfigFilename
     * 
     * @return String
     */
    public String getNamespaceConfigFilename() {
        String key = "namespace.filename";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "namespace.xml";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Retrieve the namespace schema file name from the first line (attribute) of namespace.xml.
     * 
     * @return String
     */
    public String getNamespaceSchemaFilename() {
        String key = "namespace.schema.filename";
        if (!cr.getConfiguration().containsKey(key)) {
            // scan the first line of namespace.xml 
            String namespaceSchemaFN = "namespace.xsd";

            String namespaceFN = namespaceConfigPath() + File.pathSeparator + getNamespaceConfigFilename();
            File namespaceFile = new File(namespaceFN);
            if (namespaceFile.exists()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(namespaceFN);
                    Element rootElement = doc.getDocumentElement();
                    if (!(rootElement.getTagName().equals("namespace"))) {
                        if (rootElement.hasAttributes()) {
                            String value = rootElement.getAttribute("xsi:noNamespaceSchemaLocation");
                            if ((value != null) && (value.length() > 0)) {
                                namespaceSchemaFN = value;
                                log.debug("namespace schema is : " + namespaceSchemaFN);
                            }
                        } else {
                            log.error("namespace.xml don't have a valid root element attributes");
                        }
                    } else {
                        log.error("namespace.xml don't have a valid root element.");
                    }

                } catch (ParserConfigurationException e) {
                    log.error("Error while parsing namespace.xml." + e.getMessage());
                } catch (SAXException e) {
                    log.error("Error while parsing namespace.xml." + e.getMessage());
                } catch (IOException e) {
                    log.error("Error while parsing namespace.xml." + e.getMessage());
                }
            }
            return namespaceSchemaFN;
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    public int getNamespaceConfigRefreshRateInSeconds() {
        String key = "namespace.refreshrate";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 3;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }

    }

    /**
     * getNamespaceAutomaticReloading
     * 
     * @return boolean Method used by Namespace Configuration Reloading Strategy (Peeper). If "peeper" found
     *         namespace.xml config file changed it checks if it can perform an automatic reload. If no value is found
     *         in the configuration medium, then the default one is used instead.
     *         key="namespace.automatic-config-reload"; default value=false
     */
    public boolean getNamespaceAutomaticReloading() {
        String key = "namespace.automatic-config-reload";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return false;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key);
        }
    }

    /**
     * Method used by SFN to establish the FE binding port. If no value is found in the configuration medium, then the
     * default one is used instead. key="fe.port"; default value="8444"
     */
    /*    public int getFEPort() {
            String key = "fe.port";
            if (!cr.getConfiguration().containsKey(key)) {
                // return default
                return 8444;
            } else {
                // load from external source
                return cr.getConfiguration().getInt(key);
            }
        }
    */
    /**
     * Method used by NaiveGridFTP internal client in srmCopy to establish the time out in milliseconds for a reply from
     * the server. If no value is found in the configuration medium, then the default one is used instead.
     * key="NaiveGridFTP.TimeOut"; default value="15000"
     */
    public int getGridFTPTimeOut() {
        String key = "asynch.srmcopy.gridftp.timeout";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 15000;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by SRM22Client in srmCopy to establish the PinLifeTime in seconds when issuing srmPtP. If no value is
     * found in the configuration medium, then the default one is used instead. key="SRM22Client.PinLifeTime"; default
     * value="300"
     */
    public int getSRM22ClientPinLifeTime() {
        String key = "SRM22Client.PinLifeTime";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 259200;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    // public boolean getSURLInQueryForm() {
    // boolean result = false;
    // String key = "storm.service.inQueryForm";
    // if (!cr.getConfiguration().containsKey(key)) {
    // // return default
    // return result;
    // } else {
    // // load from external source
    // result = cr.getConfiguration().getBoolean(key);
    // }
    // return result;
    // }

    // public String getSFNQueryStringPrefix() {
    // String key = "storm.service.SFNQueryStringPrefix";
    // String defaultValue = "SFN";
    // if (!cr.getConfiguration().containsKey(key)) {
    // // return default
    // return defaultValue;
    // } else {
    // // load from external source
    // return cr.getConfiguration().getString(key);
    // }
    // }

    /**
     * Method used by RequestCredentialsDAO to establish the directory that holds the proxy file. If no value is found
     * in the configuration medium, then the default one is used instead. key="proxy.home"; default
     * value="/opt/storm/var/proxies"
     */
    public String getProxyHome() {
        String key = "proxy.home";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "/opt/storm/var/proxies";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by PtPChunk to find out if missing local directories should be created automatically or not. SRM 2.2
     * specification forbids automatic creation. If no value is found in the configuration medium, then the default one
     * is used instead. key="automatic.directory.creation"; default value=false
     */
    public boolean getAutomaticDirectoryCreation() {
        String key = "directory.automatic-creation";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return false;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key);
        }
    }

    /**
     * Method used by TOverwriteModeConverter to establish the default OverwriteMode to use. If no value is found in the
     * configuration medium, then the default one is used instead. key="default.overwrite"; default value="A"
     */
    public String getDefaultOverwriteMode() {
        String key = "default.overwrite";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "A";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by FileStorageTypeConverter to establish the default TFileStorageType to use. If no value is found in
     * the configuration medium, then the default one is used instead. key="default.storagetype"; default value="V"
     */
    public String getDefaultFileStorageType() {
        String key = "default.storagetype";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "V";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by RequestSummaryDAO to establish the batch size for removing expired requests. If no value is found
     * in the configuration medium, then the default one is used instead. key="purge.size"; default value=800
     */
    public int getPurgeBatchSize() {
        String key = "purge.size";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 800;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by RequestSummaryDAO to establish the time that must be elapsed for considering a request expired.
     * The time measure specified in the configuration medium is in _days_. The value returned by this method, is
     * expressed in _seconds_ If no value is found in the configuration medium, then the default one is used instead.
     * key="expired.request.time"; default value=7 (days - which correspond to 7 * 24 * 60 * 60 seconds)
     */
    public long getExpiredRequestTime() {
        String key = "expired.request.time";
        if (!cr.getConfiguration().containsKey(key)) {
            // 7 * 24 * 60 * 60
            return 604800;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by RequestSummaryCatalog to establish the initial delay before starting the purging thread, in
     * _seconds_. If no value is found in the configuration medium, then the default one is used instead.
     * key="purge.delay"; default value=10
     */
    public int getRequestPurgerDelay() {
        String key = "purge.delay";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by RequestSummaryCatalog to establish the time interval in _seconds_ between successive purging
     * checks. If no value is found in the configuration medium, then the default one is used instead.
     * key="purge.interval"; default value=600 (1o minutes)
     */
    public int getRequestPurgerPeriod() {
        String key = "purge.interval";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 600;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used by RequestSummaryCatalog to establish if the purging of expired requests should be enabled or not. If
     * no value is found in the configuration medium, then the default one is used instead. key="purging"; default
     * value=true
     */
    public boolean getExpiredRequestPurging() {
        String key = "purging";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return true;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key);
        }
    }

    /**
     * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a TURL If no value is
     * found in the configuration medium, then the default one is used instead. key="extraslashes.file"; default
     * value="" (that is 'file:///) value = "/" ==> file:////
     */
    public String getExtraSlashesForFileTURL() {
        String key = "extraslashes.file";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a TURL If no value is
     * found in the configuration medium, then the default one is used instead. key="extraslashes.rfio"; default
     * value="" (that is 'rfio://<hostname>:port<PhysicalFN>')) value = "/" ==> 'rfio://<hostname>:port/<PhysicalFN>'
     */
    public String getExtraSlashesForRFIOTURL() {
        String key = "extraslashes.rfio";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a TURL If no value is
     * found in the configuration medium, then the default one is used instead. key="extraslashes.gsiftp"; default
     * value="" (that is 'gsiftp://<hostname>:port<PhysicalFN>')) value = "/" ==>
     * 'gsiftp://<hostname>:port/<PhysicalFN>'
     */
    public String getExtraSlashesForGsiFTPTURL() {
        String key = "extraslashes.gsiftp";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a TURL If no value is
     * found in the configuration medium, then the default one is used instead. key="extraslashes.root"; default
     * value="/" (that is 'root://<hostname>:port<PhysicalFN>')) value = "" ==> 'root://<hostname>:port<PhysicalFN>'
     */
    public String getExtraSlashesForROOTTURL() {
        String key = "extraslashes.root";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "/";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used by Ping Executor to retrieve the Properties File Name where the properties <key,value> are stored. If
     * no value is found in the configuration medium, then the default one is used instead. key="extraslashes.gsiftp";
     * default value="" (that is 'gsiftp://<hostname>:port<PhysicalFN>')) value = "/" ==>
     * 'gsiftp://<hostname>:port/<PhysicalFN>'
     */
    public String getPingValuesPropertiesFilename() {
        String key = "ping-properties.filename";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "ping-values.properties";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * If no value is found in the configuration medium, then the default one is used instead.
     * key="health.electrocardiogram.period"; default value=60 (1 min)
     */
    public int getHearthbeatPeriod() {
        String key = "health.electrocardiogram.period";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 60;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * getPerformanceGlancePeriod
     * 
     * @return int If no value is found in the configuration medium, then the default one is used instead.
     *         key="health.performance.glance.timeInterval"; default value=15 (15 sec)
     */
    public int getPerformanceGlanceTimeInterval() {
        String key = "health.performance.glance.timeInterval";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 15;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * getPerformanceGlancePeriod
     * 
     * @return int If no value is found in the configuration medium, then the default one is used instead.
     *         key="health.performance.logbook.timeInterval"; default value=20 (20 sec)
     */
    public int getPerformanceLogbookTimeInterval() {
        String key = "health.performance.logbook.timeInterval";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 15;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * getPerformanceMeasuring
     * 
     * @return boolean If no value is found in the configuration medium, then the default one is used instead.
     *         key="health.performance.mesauring.enabled"; default value=false
     */
    public boolean getPerformanceMeasuring() {
        String key = "health.performance.mesauring.enabled";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return false;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key);
        }
    }

    /**
     * getBookKeppeingEnabled
     * 
     * @return boolean Method used by Namespace Configuration Reloading Strategy (Peeper). If "peeper" found
     *         namespace.xml config file changed it checks if it can perform an automatic reload. If no value is found
     *         in the configuration medium, then the default one is used instead. key="health.bookkeeping.enabled";
     *         default value=false
     */
    public boolean getBookKeepingEnabled() {
        String key = "health.bookkeeping.enabled";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return false;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key);
        }
    }

    /**
     * Enable write permission on new created directory for LocalAuthorizationSource usage.
     * 
     * @return false by default, otherwise what is specified in the properties
     */
    public boolean getEnableWritePermOnDirectory() {
        String key = "directory.writeperm";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return false;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key, false);
        }
    }

    public int getMaxLoop() {
        String key = "abort.maxloop";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 10;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used to retrieve the ClassName for the User Mapper Class If no value is found in the configuration medium,
     * then the default one is used instead, that is "it.grid.storm.griduser.LcmapsMapper"
     * key="griduser.mapper.classname";
     */
    public String getGridUserMapperClassname() {
        String key = "griduser.mapper.classname";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "it.grid.storm.griduser.LcmapsMapper";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used to retrieve the default path where the AuthzDB file are stored If no value is found in the
     * configuration medium, then the default one is used instead, that is the "configuration directory"
     * key="authzdb.path";
     */
    public String getAuthzDBPath() {
        String key = "authzdb.path";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return cr.configurationDirectory();
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used to retrieve the default refresh rate of the AuthzDB files If no value is found in the configuration
     * medium, then the default one is used instead, that is the "5 sec" key="authzdb.refreshrate";
     */
    public int getRefreshRateAuthzDBfilesInSeconds() {
        String key = "authzdb.refreshrate";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 5;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    public String[] getChecksumServiceURLArray() {
        String key = "checksum.serviceURL";
        String[] urlArray;

        if (cr.getConfiguration().containsKey(key)) {
            urlArray = cr.getConfiguration().getStringArray(key);
        } else {
            urlArray = new String[0];
        }
        return urlArray;
    }

    public String getChecksumAlgorithm() {

        String key = "checksum.algorithm";

        if (cr.getConfiguration().containsKey(key)) {

            return cr.getConfiguration().getString(key);
        }

        return "Adler32";
    }

    public boolean getRecallTableTestingMode() {

        String key = "tape.recalltable.service.test-mode";

        if (cr.getConfiguration().containsKey(key)) {
            return cr.getConfiguration().getBoolean(key);
        }

        return false;
    }

    /**
     * Method used to retrieve the PORT where the Recall Table (RESTful) service listen If no value is found in the
     * configuration medium, then the default one is used instead, that is the "9998"
     * key="tape.recalltable.service.port";
     */
    public int getRecallTableServicePort() {
        String key = "tape.recalltable.service.port";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return 9998;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    /**
     * Method used to retrieve the key string used to pass RETRY-VALUE parameter to Recall Table service
     * key="tape.recalltable.service.param.retry-value";
     */
    public String getRetryValueKey() {
        String key = "tape.recalltable.service.param.retry-value";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "retry-value";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used to retrieve the key string used to pass RETRY-VALUE parameter to Recall Table service
     * key="tape.recalltable.service.param.status";
     */
    public String getStatusKey() {
        String key = "tape.recalltable.service.param.status";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "status";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * Method used to retrieve the key string used to pass RETRY-VALUE parameter to Recall Table service
     * key="tape.recalltable.service.param.takeover";
     */
    public String getTaskoverKey() {
        String key = "tape.recalltable.service.param.takeover";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return "first";
        } else {
            // load from external source
            return cr.getConfiguration().getString(key);
        }
    }

    /**
     * This is the FLAG to support or not the checksum in the srmLS sull detailed list. Since the checksum is calculated
     * run time each time and LS request in full detailed is done, it could be quite expensive for large file. Since FTS
     * can use both srmls and gridftp based checksum, the support has been made optional. Default is false.
     */
    public boolean getChecksumEnabled() {
        String key = "checksum.enabled";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return false;
        } else {
            // load from external source
            return cr.getConfiguration().getBoolean(key);
        }
    }

    public int getStoRMPropertiesVersion() {
        String key = "storm.properties.version";
        if (!cr.getConfiguration().containsKey(key)) {
            // return default
            return -1;
        } else {
            // load from external source
            return cr.getConfiguration().getInt(key);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        try {
            // This class methods!!!
            Method m[] = Configuration.instance.getClass().getDeclaredMethods();

            Object aux = null;
            for (int i = 0; i < m.length; i++) {
                if ((m[i].getName().substring(0, 3).equals("get")) && (!m[i].getName().equals("getInstance"))) {
                    sb.append(m[i].getName());
                    sb.append(" == ");
                    aux = m[i].invoke(Configuration.instance, new Object[0]);
                    sb.append(aux);
                    sb.append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e2) {
            String partialOutput = "!!! Cannot do toString! Got an Exception: " + e2 + "\n" + sb.toString();
            return partialOutput;
        }
    }

}
