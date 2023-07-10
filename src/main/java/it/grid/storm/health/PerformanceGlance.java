/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/** */
package it.grid.storm.health;

import java.util.ArrayList;
import org.slf4j.Logger;

/** @author zappi */
public class PerformanceGlance {

  private static Logger PERFLOG = HealthDirector.getPerformanceLogger();

  /** @return StoRMStatus */
  public PerformanceStatus haveaLook() {

    HealthDirector.LOGGER.debug("Having a look..");
    PerformanceStatus performanceStatus = null;

    PerformanceBookKeeper pbk = HealthDirector.getHealthMonitor().getPerformanceBookKeeper();

    if (pbk != null) {
      performanceStatus = pbk.getPerformanceStatus();
      ArrayList<LogEvent> zombies = pbk.removeZombieEvents();
      HealthDirector.LOGGER.debug("Removed # <{}> zombies.", zombies.size());

      HealthDirector.LOGGER.debug("have a look : {}", performanceStatus);
    }

    HealthDirector.LOGGER.debug(".. glance ended.");
    return performanceStatus;
  }
}
