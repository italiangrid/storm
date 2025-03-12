/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.path.model;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.common.types.StFN;

import java.util.LinkedList;
import java.util.List;

public abstract class PathAuthzEvaluationAlgorithm {

  public abstract AuthzDecision evaluate(String subject, StFN fileName,
      SRMFileRequest pathOperation, List<PathACE> acl);

  public abstract AuthzDecision evaluate(String subject, StFN fileName, PathOperation pathOperation,
      List<PathACE> acl);

  /**
   * @return
   */
  public abstract String getDescription();

  public abstract AuthzDecision evaluateAnonymous(StFN fileStFN, PathOperation pathOperation,
      LinkedList<PathACE> authzDB);

  public abstract AuthzDecision evaluateAnonymous(StFN fileStFN, SRMFileRequest pathOperation,
      LinkedList<PathACE> authzDB);

}
