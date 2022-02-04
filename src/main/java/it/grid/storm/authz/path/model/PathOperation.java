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

/**
 * 
 */
package it.grid.storm.authz.path.model;

/**
 * @author zappi
 */

public enum PathOperation {

  WRITE_FILE('W', "WRITE_FILE", "Write data"),
  READ_FILE('R', "READ_FILE", "Read data", true),
  RENAME('F', "RENAME", "Rename a file or a directory"),
  DELETE('D', "DELETE", "Delete a file or a directory"),
  LIST_DIRECTORY('L', "LIST_DIRECTORY", "Listing a directory", true),
  MAKE_DIRECTORY('M', "CREATE_DIRECTORY", "Create a directory"),
  CREATE_FILE('N', "CREATE_FILE", "Create a new file"),
  UNDEFINED('?', "UNDEFINED", "Undefined");

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

  public int getNumberOfPathOp() {

    return PathOperation.values().length - 1;
  }

  public boolean isReadOnly() {

    return this.readonly;
  }

}
