/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

/**
 * This class represents the Synchronous Call xmlrpc Server . This class hava a
 * set of Handler that manage the FE call invoking the right BackEnd manager.
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.xmlrpc;

import it.grid.storm.common.OperationType;
import java.util.Map;

public class XMLRPCMethods {

  private final XMLRPCExecutor executor = new XMLRPCExecutor();

  public XMLRPCMethods() {

  };

  public Map<String, Object> ping(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.PNG, inputParam);
  }

  public Map<String, Object> putDone(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.PD, inputParam);
  }

  public Map<String, Object> releaseFiles(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.RF, inputParam);
  }

  public Map<String, Object> extendFileLifeTime(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.EFL, inputParam);
  }

  public Map<String, Object> abortRequest(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.AR, inputParam);
  }

  public Map<String, Object> abortFiles(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.AF, inputParam);
  }

  public Map<String, Object> reserveSpace(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.RESSP, inputParam);
  }

  /**
   * GetSpaceMetaData
   */
  public Map<String, Object> getSpaceMetaData(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.GSM, inputParam);
  }

  /**
   * GetSpaceTokens
   * 
   * @param inputParam
   * @return
   */
  public Map<String, Object> getSpaceTokens(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.GST, inputParam);
  }

  /**
   * ReleaseSpace
   */

  public Map<String, Object> ReleaseSpace(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.RELSP, inputParam);
  }

  /**
   * SrmLs request. This method catch an SrmLs request passed by StoRM Frontend trough XMLRPC
   * communication. The HashTable is the default Java type used to represent structure passed by
   * XMLRPC.
   * 
   * @param Hastable output parameter structure returned.
   * @param inputParameter input parameter structure received from xmlrpc call.
   */

  public Map<String, Object> ls(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.LS, inputParam);
  }

  /**
   * SrmMkdir functionality.
   */

  public Map<String, Object> mkdir(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.MKD, inputParam);
  }

  /**
   * SrmRmdir functionality.
   * 
   * @param inputParam
   * @return
   */
  public Map<String, Object> rmdir(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.RMD, inputParam);
  }

  /**
   * SrmRm functionality.
   * 
   * @param inputParam
   * @return
   */
  public Map<String, Object> rm(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.RM, inputParam);
  }

  /**
   * SrmMv functionality.
   */

  public Map<String, Object> mv(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.MV, inputParam);
  }

  /**
   * SrmPrepareToPut functionality.
   */
  public Map<String, Object> prepareToPut(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.PTP, inputParam);
  }

  /**
   * SrmPrepareToPutStatus functionality.
   */
  public Map<String, Object> prepareToPutStatus(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.SPTP, inputParam);
  }

  /**
   * SrmPrepareToGet functionality.
   */
  public Map<String, Object> prepareToGet(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.PTG, inputParam);
  }

  /**
   * SrmPrepareToGetStatus functionality.
   */
  public Map<String, Object> prepareToGetStatus(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.SPTG, inputParam);
  }
}
