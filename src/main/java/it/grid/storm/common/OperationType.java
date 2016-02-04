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

package it.grid.storm.common;

import com.codahale.metrics.Timer;

import it.grid.storm.metrics.StormMetricRegistry;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag @date May 28, 2008
 * 
 */

public enum OperationType {
  UNDEF("undefined"),
  PTG("ptg"),
  SPTG("sPtg"),
  PTP("ptp"),
  SPTP("sPtp"),
  COPY("copy"),
  BOL("bol"),
  AF("abortFiles"),
  AR("abortRequest"),
  EFL("extendFileLifetime"),
  GSM("getSpaceMetadata"),
  GST("getSpaceTokens"),
  LS("ls"),
  MKD("mkdir"),
  MV("mv"),
  PNG("ping"),
  PD("putDone"),
  RF("releaseFiles"),
  RESSP("reserveSpace"),
  RELSP("releaseSpace"),
  RM("rm"),
  RMD("rmDir");

  private final String opName;

  private final Timer timer;

  private OperationType(String name) {
    this.opName = name;
    timer = StormMetricRegistry.INSTANCE.getRegistry().timer(opName);
  }

  public String getOpName() {

    return opName;
  }

  public Timer getTimer() {

    return timer;
  }

}
