/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common;

import static it.grid.storm.metrics.StormMetricRegistry.METRIC_REGISTRY;

import com.codahale.metrics.Timer;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag @date May 28, 2008
 * 
 */

public enum OperationType {
  UNDEF("undefined"),
  PTG("synch.ptg"),
  SPTG("synch.sPtg"),
  PTP("synch.ptp"),
  SPTP("synch.sPtp"),
  COPY("synch.copy"),
  BOL("synch.bol"),
  AF("synch.af"),
  AR("synch.ar"),
  EFL("synch.efl"),
  GSM("synch.gsm"),
  GST("synch.gst"),
  LS("synch.ls"),
  MKD("synch.mkdir"),
  MV("synch.mv"),
  PNG("synch.ping"),
  PD("synch.pd"),
  RF("synch.rf"),
  RESSP("synch.reserveSpace"),
  RELSP("synch.releaseSpace"),
  RM("synch.rm"),
  RMD("synch.rmDir");

  private final String opName;

  private final Timer timer;

  private OperationType(String name) {
    this.opName = name;
    timer = METRIC_REGISTRY.getRegistry().timer(opName);
  }

  public String getOpName() {

    return opName;
  }

  public Timer getTimer() {

    return timer;
  }

}
