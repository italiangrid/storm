/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
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

import java.util.Map;

import it.grid.storm.common.OperationType;

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

  public Map<String, Object> getSpaceMetaData(Map<String, Object> inputParam)
      throws StoRMXmlRpcException {

    return executor.execute(OperationType.GSM, inputParam);
  }

  public Map<String, Object> getSpaceTokens(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.GST, inputParam);
  }

  public Map<String, Object> ReleaseSpace(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.RELSP, inputParam);
  }

  public Map<String, Object> ls(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.LS, inputParam);
  }

  public Map<String, Object> mkdir(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.MKD, inputParam);
  }

  public Map<String, Object> rmdir(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.RMD, inputParam);
  }

  public Map<String, Object> rm(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.RM, inputParam);
  }

  public Map<String, Object> mv(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.MV, inputParam);
  }

  public Map<String, Object> prepareToPut(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.PTP, inputParam);
  }

  public Map<String, Object> prepareToPutStatus(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.SPTP, inputParam);
  }

  public Map<String, Object> prepareToGet(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.PTG, inputParam);
  }

  public Map<String, Object> prepareToGetStatus(Map<String, Object> inputParam) throws StoRMXmlRpcException {

    return executor.execute(OperationType.SPTG, inputParam);
  }
}
