/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.sa.model;

public class SRMSpaceRequest {

  /**
   * RELEASE_SPACE (D) UPDATE_SPACE (U) READ_FROM_SPACE (R) WRITE_TO_SPACE (W) STAGE_TO_SPACE (S)
   * REPLICATE_FROM_SPACE(C) PURGE_FROM_SPACE (P) QUERY_SPACE (Q) MODIFY_SPACE_ACL (M)
   **/

  // Operations to SPACE+SURL
  public final static SRMSpaceRequest PTP = new SRMSpaceRequest("srmPrepareToPut", "PTP",
      new SpaceOperation[] {SpaceOperation.WRITE_TO_SPACE});
  public final static SRMSpaceRequest PTG = new SRMSpaceRequest("srmPrepareToGet", "PTG",
      new SpaceOperation[] {SpaceOperation.READ_FROM_SPACE, SpaceOperation.REPLICATE_FROM_SPACE});
  public final static SRMSpaceRequest BOL = new SRMSpaceRequest("srmBringOnLine", "BOL",
      new SpaceOperation[] {SpaceOperation.STAGE_TO_SPACE, SpaceOperation.REPLICATE_FROM_SPACE});
  public final static SRMSpaceRequest CPto = new SRMSpaceRequest("srmCopy to", "CPto",
      new SpaceOperation[] {SpaceOperation.WRITE_TO_SPACE});
  public final static SRMSpaceRequest CPfrom = new SRMSpaceRequest("srmCopy from", "CPfrom",
      new SpaceOperation[] {SpaceOperation.READ_FROM_SPACE, SpaceOperation.REPLICATE_FROM_SPACE});

  // Space Operations
  public final static SRMSpaceRequest PFS = new SRMSpaceRequest("srmPurgeFromSpace", "PFS",
      new SpaceOperation[] {SpaceOperation.PURGE_FROM_SPACE});
  public final static SRMSpaceRequest RS = new SRMSpaceRequest("srmReleaseSpace", "RS",
      new SpaceOperation[] {SpaceOperation.RELEASE_SPACE});
  public final static SRMSpaceRequest QS = new SRMSpaceRequest("srmGetSpaceMetadata", "QS",
      new SpaceOperation[] {SpaceOperation.QUERY_SPACE, SpaceOperation.UPDATE_SPACE});

  // OVERLOAD with OP
  public final static SRMSpaceRequest RM =
      new SRMSpaceRequest("srmRemove", "RM", new SpaceOperation[] {SpaceOperation.WRITE_TO_SPACE});
  public final static SRMSpaceRequest RMD = new SRMSpaceRequest("srmRemoveDir", "RMD",
      new SpaceOperation[] {SpaceOperation.WRITE_TO_SPACE});
  public final static SRMSpaceRequest MD =
      new SRMSpaceRequest("srmMakeDir", "MD", new SpaceOperation[] {SpaceOperation.WRITE_TO_SPACE});
  public final static SRMSpaceRequest LS =
      new SRMSpaceRequest("srmLS", "LS", new SpaceOperation[] {SpaceOperation.READ_FROM_SPACE});
  public final static SRMSpaceRequest MV =
      new SRMSpaceRequest("srmMove", "MV", new SpaceOperation[] {SpaceOperation.WRITE_TO_SPACE});

  private String description;
  private String srmOp;
  private SpaceAccessMask requestedSpaceOps;

  /**
   * SRMOperation
   */
  private SRMSpaceRequest(String description, String srmOp, SpaceOperation[] spaceOps) {

    this.description = description;
    this.srmOp = srmOp;
    requestedSpaceOps = new SpaceAccessMask();
    for (SpaceOperation spaceOp : spaceOps) {
      requestedSpaceOps.addSpaceOperation(spaceOp);
    }
  }

  public String toString() {

    String result;
    result = srmOp + " : " + description + " = " + requestedSpaceOps;
    return result;
  }
}
