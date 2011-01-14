/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.config;

import java.util.Iterator;
import java.util.ArrayList;

import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;

/**
 * Class that reads the configuration parameters from different sources. It makes
 * use of org.apache.commons.configuration package.
 *
 * For now, the parameters are read from only one file, and there _is_ automatic
 * reloading of parameters if they change on file! Yet not all parts of the BE are
 * for now capable of changing their behaviour when those parameters change!
 *
 * Certain keys present in the configuration file may contain more comma separated
 * values; see Class Configuration for information on each available key and whether
 * such keys may or may not have several comma separated values.
 *
 * Be warned that the primary reason for this class is that Apache s package could
 * not be loaded directly into StoRMs Configuration class because of name conflicts!
 *
 * @author Riccardo Zappi; EGRID - ICTP Trieste;
 * @version 2.0
 */
public class ConfigReader {
    private Configuration c =  makeEmptyConfiguration(); //configuration object holding all parameters!
    private String configurationPathname = ""; //complete path to configuration file set to empty!
    private int refresh = 0; //refresh time in seconds before the configuration is checked for a change in parameters!

    /**
     * Constructor that returns a ConfigReader made of an empty Configuration: no file
     * from which to read parameters is specified, and essentially all requests for
     * existance of specific keys returns false.
     */
    public ConfigReader() {
        makeEmptyConfiguration();
    }

    /**
     * Constructor used to setup the complete pathname to the sole file holding the
     * configuration parameters. It requires a String representing the
     * configurationPathname, and an int representing the refresh rate when
     * checking for a change in the content of the configuration file.
     *
     * Beware, that by pathname it is meant the complete path from root, including
     * the _name_ of the file.
     *
     * If null configurationPathname is passed, then no setting of file
     * pathname takes place. In such case and in case of any error, such as
     * missing file, then an empty Configuration gets set up and proper messages
     * get displayed in std.err, st.out as well as in the logs.
     *
     * The second parameter refers to the refresh rate for checking modifications
     * to the configuration file, in seconds: 0 means no refresh. If a negative
     * refresh is specified, by default no refresh takes place (i.e. it is like
     * supplying 0).
     */
    public ConfigReader(String configurationPathname, int refresh) {
        if (configurationPathname!=null) {
            if (refresh<0) refresh=0;
            this.refresh = refresh;
            this.configurationPathname = configurationPathname;
            System.out.println("Reading configuration file " + configurationPathname + " and setting refresh rate to "+refresh+" seconds.");
            try {
                //create reloading strategy for refresh
                FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
                strategy.setRefreshDelay(refresh);
                //specify the properties file and set the reloading strategy for that file
                PropertiesConfiguration properties = new PropertiesConfiguration(configurationPathname);
                System.out.println("Properties read from file:");
                String key;
                for (Iterator i=properties.getKeys(); i.hasNext(); ) {
                    key = (String) i.next();
                    System.out.println(key+ "=" + properties.getProperty(key).toString());
                }
                properties.setReloadingStrategy(strategy);
                //add the properties to the configuration
                this.c = new CompositeConfiguration();
                ((CompositeConfiguration)this.c).addConfiguration(properties);
                System.out.println("Configuration file read. Full list of values in use follows; a copy has also been written to the logs.");
            } catch (ConfigurationException e) {
                this.c = makeEmptyConfiguration();
                System.out.println("************************************************************************");
                System.out.println("            ATTENTION! Reading of configuration file failed!");
                System.out.println("************************************************************************");
                System.out.println("Full list of values in use follows: please check it! A copy has also");
                System.out.println("been written to the logs.");
                System.err.println("************************************************************************");
                System.err.println("ATTENTION! Reading of configuration file "+ configurationPathname +" failed! "+e);
                System.err.println("ATTENTION! Please check standard output or logs for exact configuration in use!");
                System.err.println("************************************************************************");
            }
        } else {
            System.err.println("WARNING!!! Null configuration pathname supplied: this could be a programming bug! Please check standard output or logs for exact configuration in use!");
        }
    }

    /**
     * Method that returns the Apache object holding all configuration parameters!
     */
    public Configuration getConfiguration() {
        return c;
    }

    /**
     * Method that returns the directory containing the configuration files: it is
     * extrapolated from the complete pathname of the configuration file. If the
     * pathname was not setup, an empty String is returned.
     */
    public String configurationDirectory() {
        if (this.configurationPathname.equals("")) return "";
        int lastSlash = this.configurationPathname.lastIndexOf(java.io.File.separator);
        if (lastSlash==-1) return ""; //no slash!
        return this.configurationPathname.substring(0,lastSlash+1);
    }

    /**
     * Private method that returns an Empty implemetnation of Apache s Configuration
     * Object, which does not contain any key, does not addPropertyDirect, does not
     * clearProperty, returns an empy Iterator for getKeys, returns a primitive Object
     * for getPropertyDirect, returns true for isEmpty.
     */
    private Configuration makeEmptyConfiguration() {
        return new AbstractConfiguration() {
            protected void addPropertyDirect(String key, Object obj) {}

            public void clearProperty(String key) {}

            public boolean containsKey(String key) {
                return false;
            }

            public Iterator getKeys() {
                return new ArrayList().iterator();
            }

            protected Object getPropertyDirect(String key) {
                return new Object();
            }

            public boolean isEmpty() {
                return true;
            }

            public Object getProperty(String key) {
                return new Object();
            }
        };
    }


}
