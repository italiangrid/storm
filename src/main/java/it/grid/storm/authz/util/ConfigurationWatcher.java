/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/** */
package it.grid.storm.authz.util;

import java.io.File;
import java.util.TimerTask;

/** @author ritz */
public abstract class ConfigurationWatcher extends TimerTask {

  private long timeStamp;
  private final File file;

  public ConfigurationWatcher(File file) {

    this.file = file;
    timeStamp = file.lastModified();
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
