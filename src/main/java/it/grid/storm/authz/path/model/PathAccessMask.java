/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.path.model;

import java.util.ArrayList;
import java.util.List;

/** @author zappi */
@SuppressWarnings("serial")
public class PathAccessMask {

  private final List<PathOperation> pathAccessMask;

  private static List<PathOperation> operations =
      new ArrayList<PathOperation>() {

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
