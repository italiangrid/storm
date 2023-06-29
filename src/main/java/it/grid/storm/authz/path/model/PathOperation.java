/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.authz.path.model;

/**
 * @author zappi
 */

public enum PathOperation {
  WRITE_FILE('W', "WRITE_FILE", "Write data"), READ_FILE('R', "READ_FILE", "Read data",
      true), RENAME('F', "RENAME", "Rename a file or a directory"), DELETE('D', "DELETE",
          "Delete a file or a directory"), LIST_DIRECTORY('L', "LIST_DIRECTORY",
              "Listing a directory",
              true), MAKE_DIRECTORY('M', "CREATE_DIRECTORY", "Create a directory"), CREATE_FILE('N',
                  "CREATE_FILE", "Create a new file"), UNDEFINED('?', "UNDEFINED", "Undefined");

  private final char operation;
  private final String operationName;
  private final String operationDescription;
  private final boolean readonly;

  private PathOperation(char operation, String spaceOpName, String spaceOpDesc) {

    this.operation = operation;
    operationName = spaceOpName;
    operationDescription = spaceOpDesc;
    readonly = false;
  }

  private PathOperation(char operation, String spaceOpName, String spaceOpDesc, boolean readonly) {

    this.operation = operation;
    operationName = spaceOpName;
    operationDescription = spaceOpDesc;
    this.readonly = readonly;
  }

  public String getDescription() {

    return operationDescription;
  }

  public static PathOperation getSpaceOperation(char op) {

    switch (op) {
      case 'W':
        return WRITE_FILE;
      case 'R':
        return READ_FILE;
      case 'F':
        return RENAME;
      case 'D':
        return DELETE;
      // case 'T':
      // return TRAVERSE_DIRECTORY;
      case 'L':
        return LIST_DIRECTORY;
      case 'M':
        return MAKE_DIRECTORY;
      case 'N':
        return CREATE_FILE;
      default:
        return UNDEFINED;
    }
  }

  @Override
  public String toString() {

    return String.valueOf(operationName);
  }

  public char getSpaceOperationValue() {

    return operation;
  }

  public PathOperation getSpaceOp(int ordinal) {

    PathOperation[] sp = PathOperation.values();
    if ((ordinal >= 0) && (ordinal < sp.length)) {
      return sp[ordinal];
    } else {
      return UNDEFINED;
    }
  }

  public int getNumberOfPathOp() {

    return PathOperation.values().length - 1;
  }

  public boolean isReadOnly() {

    return this.readonly;
  }

}
