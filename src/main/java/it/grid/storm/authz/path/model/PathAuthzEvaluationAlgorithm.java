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

package it.grid.storm.authz.path.model;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.common.types.StFN;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zappi
 */
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
