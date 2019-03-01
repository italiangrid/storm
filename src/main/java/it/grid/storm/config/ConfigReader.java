/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {

  private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);

  private Configuration c;

  private String configurationPathname = "";

  public ConfigReader(String configurationPathname, int refresh) throws ConfigurationException {

    checkNotNull(configurationPathname, "Null configuration pathname.");
    int refreshRate = refresh < 0 ? 0 : refresh;
    this.configurationPathname = configurationPathname;
    log.info("Configuration file {}. Refresh rate: {} seconds", configurationPathname, refreshRate);

    FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
    strategy.setRefreshDelay(refreshRate);
    PropertiesConfiguration properties = new PropertiesConfiguration(configurationPathname);
    logPropertiesConfiguration(properties);
    properties.setReloadingStrategy(strategy);
    this.c = new CompositeConfiguration();
    ((CompositeConfiguration) this.c).addConfiguration(properties);
    log.info("Configuration read successfully.");
  }

  private void logPropertiesConfiguration(PropertiesConfiguration properties) {

    log.debug("Configuration properties: ");
    String key;
    for (Iterator<?> i = properties.getKeys(); i.hasNext();) {
      key = (String) i.next();
      log.debug("{} = {}", key, properties.getProperty(key));
    }
  }

  /**
   * Method that returns the Apache object holding all configuration parameters!
   */
  public Configuration getConfiguration() {

    return c;
  }

  /**
   * Method that returns the directory containing the configuration files: it is extrapolated from
   * the complete pathname of the configuration file. If the pathname was not setup, an empty String
   * is returned.
   */
  public String configurationDirectory() {

    if (configurationPathname.isEmpty())
      return "";
    int lastSlash = this.configurationPathname.lastIndexOf(java.io.File.separator);
    if (lastSlash == -1)
      return ""; // no slash!
    return this.configurationPathname.substring(0, lastSlash + 1);
  }

}
