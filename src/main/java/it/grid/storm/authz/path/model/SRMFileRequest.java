/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.authz.path.model;

import java.util.ArrayList;
import java.util.List;

public enum SRMFileRequest {

  /**
   * WRITE_FILE 'W' READ_FILE 'R' RENAME 'F' DELETE 'D' TRAVERSE_DIRECTORY 'T' LIST_DIRECTORY 'L'
   * MAKE_DIRECTORY 'M' CREATE_FILE 'N' UNDEFINED '?'
   **/

  // Operations to SURL
  PTP_Overwrite("srmPrepareToPut-overwrite","PTP-Over",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.WRITE_FILE);}}),

  PTP("srmPrepareToPut","PTP",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.CREATE_FILE);add(PathOperation.WRITE_FILE);}}),

  PTG("srmPrepareToGet","PTG",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.READ_FILE);}}),

  CPto("srmCopy to","CPto",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.WRITE_FILE);add(PathOperation.CREATE_FILE);}}),

  CPto_Overwrite("srmCopy to-overwrite","CPto_Over",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.WRITE_FILE);}}),

  CPfrom("srmCopy from","CPfrom",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.READ_FILE);}}),

  // OVERLOAD with OP
  RM("srmRemove","RM",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.DELETE);}}),

  RMD("srmRemoveDir","RMD",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.DELETE);}}),

  MD("srmMakeDir","MD",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.MAKE_DIRECTORY);}}),

  LS("srmLS","LS",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.LIST_DIRECTORY);}}),

  MV_source("srmMove-source","MV-source",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.READ_FILE);add(PathOperation.DELETE);}}),

  MV_dest_Overwrite("srmMove-dest-overwrite","MV-dest-Over",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.WRITE_FILE);}}),

  MV_dest("srmMove-dest-overwrite","MV-dest",new ArrayList<PathOperation>(){

  private static final long serialVersionUID=1L;

  {add(PathOperation.CREATE_FILE);add(PathOperation.WRITE_FILE);}});

  private final String description;
  private final String srmOp;
  private final PathAccessMask requestedPathOps;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * SRMOperation
   */
  private SRMFileRequest(String description, String srmOp, List<PathOperation> pathOps) {

    this.description = description;
    this.srmOp = srmOp;
    requestedPathOps = new PathAccessMask();
    for (PathOperation pathOp : pathOps) {
      requestedPathOps.addPathOperation(pathOp);
    }
  }

  public PathAccessMask getSRMPathAccessMask() {

    return requestedPathOps;
  }

  @Override
  public String toString() {

    return String.format("%s : %s = %s", srmOp, description, requestedPathOps);
  }

  public boolean isReadOnly() {

    for (PathOperation operation : requestedPathOps.getPathOperations()) {
      if (!operation.isReadOnly()) {
        return false;
      }
    }
    return true;
  }

}
