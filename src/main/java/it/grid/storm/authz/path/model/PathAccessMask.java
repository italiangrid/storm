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

import java.util.ArrayList;
import java.util.List;

/**
 * @author zappi
 */
@SuppressWarnings("serial")
public class PathAccessMask {

  private final List<PathOperation> pathAccessMask;

  private static List<PathOperation> operations = new ArrayList<PathOperation>() {

    {
      add(PathOperation.READ_FILE);
      add(PathOperation.LIST_DIRECTORY);
    }
  };

  public static final PathAccessMask DEFAULT = new PathAccessMask(operations);

  public PathAccessMask() {

    pathAccessMask = new ArrayList<PathOperation>();
  }

  public PathAccessMask(List<PathOperation> operations) {

    pathAccessMask = operations;
  }

  public void addPathOperation(PathOperation pathOp) {

    pathAccessMask.add(pathOp);
  }

  public boolean containsPathOperation(PathOperation pathOp) {

    return pathAccessMask.contains(pathOp);
  }

  public List<PathOperation> getPathOperations() {

    return pathAccessMask;
  }

  public int getSize() {

    return pathAccessMask != null ? pathAccessMask.size() : 0;
  }

  @Override
  public boolean equals(Object other) {

    boolean result = false;
    if (other instanceof PathAccessMask) {
      PathAccessMask pOther = (PathAccessMask) other;
      if (pathAccessMask.size() == pOther.getSize()) {
        result = true;
        for (PathOperation element : pathAccessMask) {
          if (!(pOther.containsPathOperation(element))) {
            result = false;
          }
        }
      }
    }
    return result;
  }

  @Override
  public String toString() {

    String pathPermissionStr = "";
    for (PathOperation pathOp : PathOperation.values()) {
      if (!(pathOp.equals(PathOperation.UNDEFINED))) {
        if (pathAccessMask.contains(pathOp)) {
          pathPermissionStr += pathOp.getSpaceOperationValue();
        } else {
          pathPermissionStr += "-";
        }
      }
    }
    return pathPermissionStr;
  }

  @Override
  public int hashCode() {

    int result = 17;
    if (pathAccessMask != null) {
      for (PathOperation element : pathAccessMask) {
        result += 31 * result + element.hashCode();
      }
    }
    return result;
  }
}
