/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.authz.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ritz
 */
public class PathAuthzConfigurationWatcher extends ConfigurationWatcher {

  private static final Logger log = LoggerFactory.getLogger(PathAuthzConfigurationWatcher.class);

  /**
   * @param file
   */
  public PathAuthzConfigurationWatcher(File file) {

    super(file);
    log.debug("Watcher manages the configuration file: {}", file);
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.grid.storm.authz.util.ConfigurationWatcher#onChange()
   */
  @Override
  protected void onChange() {

    log.info("Path Authorization DB is changed! Going to reload it");
    // Force the reload of the configuration file

  }

}
