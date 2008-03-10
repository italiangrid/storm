package it.grid.storm.health;

import java.text.SimpleDateFormat;
import java.util.Date;


public class StoRMStatus {

  private long heapSize = -1L;
  private long heapMaxSize = -1L;
  private long heapFreeSize = -1L;
  private String pulseNumberStr = "";

  private int ptgRequests = 0;
  private int ptgSuccess = 0;
  private long ptgMeansTime = -1L;

  private int ptpRequests = 0;
  private int ptpSuccess = 0;
  private long ptpMeansTime = -1L;

  private long lifetime = -1L;
  private String lifetimeStr = "";

  private long totPtGRequest = 0L;
  private long totPtPRequest = 0L;

  private int synchRequest = 0;


  public StoRMStatus() {
  }


  /**
   *
   * @param heapSize long
   */
  public void setHeapSize(long heapSize) {
    this.heapSize = heapSize;
  }

  /**
   *
   * @param maxHeapSize long
   */
  public void setMAXHeapSize(long maxHeapSize) {
    this.heapMaxSize = maxHeapSize;
  }

  /**
   *
   * @param heapFreeSize long
   */
  public void setHeapFreeSize(long heapFreeSize) {
    this.heapFreeSize = heapFreeSize;
  }


  /**
   *
   * @return int
   */
  public int getHeapFreePercentile() {
    int result = 100;
    if (this.heapMaxSize > 0) {
      double average = this.heapFreeSize / this.heapMaxSize * 100;
      result = (int)average;
    }
    return result;
  }



  /**
   *
   * @param number long
   */
  public void setPulseNumber(long number) {
    this.pulseNumberStr = number + "";
    String prefix = "";
    for (int i = 0; i < (6 - pulseNumberStr.length()); i++) {
       prefix+=".";
    }
     this.pulseNumberStr = prefix + this.pulseNumberStr;
  }

  /**
   *
   * @param synchRequest int
   */
  public void setSynchRequest(int synchRequest) {
    this.synchRequest = synchRequest;
  }

  /**
   *
   * @param ptgNumber int
   */
  public void setPtGNumberRequests(int ptgNumber) {
    this.ptgRequests = ptgNumber;
  }

  /**
   *
   * @param ptpSuccess int
   */
  public void setPtGSuccessRequests(int ptgSuccess) {
    this.ptgSuccess = ptgSuccess;
  }


  public void setTotalPtGRequest(long totPtG) {
    this.totPtGRequest = totPtG;
  }

  public void setTotalPtPRequest(long totPtP) {
    this.totPtPRequest = totPtP;
  }

  /**
   *
   * @param meanTime long
   */
  public void setPtGMeanDuration(long meanTime) {
    this.ptgMeansTime = meanTime;
  }

  /**
   *
   * @param ptpNumber int
   */
  public void setPtPNumberRequests(int ptpNumber) {
    this.ptpRequests = ptpNumber;
  }

  /**
   *
   * @param ptpSuccess int
   */
  public void setPtPSuccessRequests(int ptpSuccess) {
    this.ptpSuccess = ptpSuccess;
  }


  /**
   *
   * @param meanTime long
   */
  public void setPtPMeanDuration(long meanTime) {
    this.ptpMeansTime = meanTime;
  }

  public void calculateLifeTime() {
    long bornTime = HealthDirector.getBornInstant();
    long now = System.currentTimeMillis();
    this.lifetime = now - bornTime;

    Date date = new Date(this.lifetime);
    SimpleDateFormat formatter = new SimpleDateFormat("mm.ss");
    String minsec = formatter.format(date);
    long hours = this.lifetime / 3600000;
    this.lifetimeStr = hours + ":" + minsec;
  }

  /**
   *
   * @return String
   */
  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(" [#"+this.pulseNumberStr+" lifetime="+this.lifetimeStr+"]");
    result.append(" Heap Free:"+this.heapFreeSize);
    result.append(" SYNCH ["+this.synchRequest+"]");
    result.append(" ASynch [PTG:"+this.totPtGRequest);
    result.append(" PTP:"+this.totPtPRequest+"]");
    result.append(" Last:( [#PTG="+this.ptgRequests);
    result.append(" OK="+this.ptgSuccess);
    result.append(" M.Dur.="+this.ptgMeansTime+"]");
    result.append(" [#PTP="+this.ptpRequests);
    result.append(" OK="+this.ptpSuccess);
    result.append(" M.Dur.="+this.ptpMeansTime+"] )");
    return result.toString();
  }


}
