package it.grid.storm.health;

import java.text.SimpleDateFormat;
import java.util.Date;
import it.grid.storm.srm.types.TSURL;

public class LogEvent {

  private OperationType opType = null;
  private String userDN  = null;
  private String surl = null;
  private long startTime = -1L;
  private String startTimeStr = null;
  private long duration = -1L;
  private String requestToken = null;
  private boolean successResult = false;

  /**
   * Constructor for ASYNCHRONOUS Event
   *
   * @param opType OperationType
   * @param userDN String
   * @param surl String
   * @param startTime long
   * @param duration long
   * @param requestToken String
   * @param successResult boolean
   */
  public LogEvent(OperationType opType, String userDN, String surl, long startTime, long duration, String requestToken, boolean successResult) {
    this.opType = opType;
    this.userDN = userDN;
    this.surl = surl;
    this.startTime = startTime;
    this.duration = duration;
    this.requestToken = requestToken;
    Date date = new Date(startTime) ;
    SimpleDateFormat formatter = new SimpleDateFormat("HH.mm.ss");
    this.startTimeStr = formatter.format(date);
    this.successResult = successResult;
  }

  /**
   * Constructor for SYNCHRONOUS Event
   *
   * @param opType OperationType
   * @param userDN String
   * @param startTime long
   * @param duration long
   * @param successResult boolean
   */
  public LogEvent(OperationType opType, String userDN, long startTime, long duration, boolean successResult) {
    this.opType = opType;
    this.userDN = userDN;
    //Empty SURL
    this.surl = TSURL.makeEmpty().toString();
    this.startTime = startTime;
    this.duration = duration;
    this.requestToken = "SYNCH";
    Date date = new Date(startTime) ;
    SimpleDateFormat formatter = new SimpleDateFormat("HH.mm.ss");
    this.startTimeStr = formatter.format(date);
    this.successResult = successResult;
  }

  public OperationType getOperationType() {
    return this.opType;
  }

  public String getDN() {
    return this.userDN;
  }

  public String getSURL() {
    return this.surl;
  }

  public long getStartTime() {
    return this.startTime;
  }

  public String getStartTimeString() {
    return this.startTimeStr;
  }

  public long getDuration() {
    return this.duration;
  }

  public String getRequestToken() {
    return this.requestToken;
  }

  public boolean isSuccess() {
    return this.successResult;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    final char fieldSeparator = '\t';
    sb.append(userDN).append(fieldSeparator);
    sb.append(opType.toString()).append(fieldSeparator);
    if (this.successResult) {
      sb.append("-OK-").append(fieldSeparator);
    } else {
      sb.append("#ko#").append(fieldSeparator);
    }
    sb.append(surl).append(fieldSeparator);
    sb.append(startTimeStr).append(fieldSeparator);
    sb.append(duration).append(fieldSeparator);
    sb.append(requestToken).append(fieldSeparator);
    return sb.toString();
  }

}
