package it.grid.storm.health;

import it.grid.storm.scheduler.ChunkType;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OperationType {

  private int operationIndex = -1;
  private String operationName;
  private String operationDescription;

  public final static OperationType UNDEF = new OperationType(0, "UNDEF", "Undefined");

  public final static OperationType PTG = new OperationType(1, "PTG", "srmPrepareToGet");
  public final static OperationType PTP = new OperationType(2, "PTP", "srmPrepareToPut");
  public final static OperationType COPY = new OperationType(3, "COPY", "srmCopy");
  public final static OperationType BOL = new OperationType(4, "BOL", "srmBringOnLine");

  public final static OperationType AF = new OperationType(5, "AF", "srmAbortFile");
  public final static OperationType AR = new OperationType(6, "AR", "srmAbortRequest");
  public final static OperationType EFL = new OperationType(7, "ELT", "srmExtendLifeTime");
  public final static OperationType GSM = new OperationType(8, "GSM", "srmGetSpaceMetaData");
  public final static OperationType GST = new OperationType(9, "GST", "srmGetSpaceTokens");
  public final static OperationType LS = new OperationType(10, "LS", "srmLs");
  public final static OperationType MKD = new OperationType(11, "MKD", "srmMkdir");
  public final static OperationType MV = new OperationType(12, "MV", "srmMv");
  public final static OperationType PNG = new OperationType(13, "PNG", "srmPing");
  public final static OperationType PD = new OperationType(14, "PD", "srmPutDone");
  public final static OperationType RF = new OperationType(15, "RF", "srmReleaseFile");
  public final static OperationType RSP = new OperationType(16, "RSP", "srmReleaseSpace");
  public final static OperationType RS = new OperationType(17, "RS", "srmReserveSpace");
  public final static OperationType RM = new OperationType(18, "RM", "srmRm");
  public final static OperationType RMD = new OperationType(19, "RMD", "srmRmdir");



  /**
   * Constructor
   *
   * @param protocolName String
   * @param protocolSchema String
   */
  public OperationType(int operationIndex, String operationName, String operationDescription) {
    this.operationIndex = operationIndex;
    this.operationName = operationName;
    this.operationDescription = operationDescription;
  }

  /**
   *
   * @param chunkType ChunkType
   * @return OperationType
   */
  public static OperationType makeFromChunkType(ChunkType chunkType) {
    OperationType result = OperationType.UNDEF;
    switch (chunkType.getIndex()) {
      case 1:
        result = OperationType.PTG;
        break;
      case 2:
        result = OperationType.PTP;
        break;
      case 3:
        result = OperationType.COPY;
        break;
      case 4:
        result = OperationType.BOL;
        break;
      default:
        result = OperationType.UNDEF;
        break;
    }
    return result;
  }

  public boolean isSynchronousOperation() {
    boolean result = false;
    if (this.operationIndex > 4 ) {
      result = true;
    }
    return result;
  }

  public String getOperationDescription() {
    return this.operationDescription;
  }

  public String toString() {
    return this.operationName;
  }

  public int hashCode() {
    return this.operationIndex;
  }

  public boolean equals(Object obj) {
    boolean result = false;
    if (obj instanceof OperationType) {
      OperationType other = (OperationType) obj;
      if (other.operationIndex == this.operationIndex) {
        result = true;
      }
    }
    return result;
  }

}
