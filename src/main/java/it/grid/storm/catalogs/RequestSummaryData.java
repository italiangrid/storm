/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;

/**
 * This class represents the SummaryData associated with the SRM request. It contains info about:
 * Primary Key of request, TRequestType, TRequestToken, VomsGridUser.
 *
 * @author EGRID - ICTP Trieste
 * @date March 18th, 2005
 * @version 4.0
 */
public class RequestSummaryData {

  private TRequestType requestType = null; // request type of SRM request
  private TRequestToken requestToken = null; // TRequestToken of SRM request
  private GridUserInterface gu = null; // VomsGridUser that issued This request
  private long id = -1; // long representing This object in persistence

  private String userToken = null;
  private Integer retrytime = null;
  private TLifeTimeInSeconds pinLifetime = null;
  private String spaceToken = null;
  private TReturnStatus status = null;
  private String errstring = null;
  private Integer remainingTotalTime = null;
  private Integer nbreqfiles = null;
  private Integer numOfCompleted = null;
  private TLifeTimeInSeconds fileLifetime = null;
  private Integer deferredStartTime = null;
  private Integer numOfWaiting = null;
  private Integer numOfFailed = null;
  private Integer remainingDeferredStartTime = null;

  public RequestSummaryData(TRequestType rtype, TRequestToken rtoken, GridUserInterface gu)
      throws InvalidRequestSummaryDataAttributesException {

    boolean ok = rtype != null && rtoken != null && gu != null;
    if (!ok) throw new InvalidRequestSummaryDataAttributesException(rtype, rtoken, gu);
    this.requestType = rtype;
    this.requestToken = rtoken;
    this.gu = gu;
  }

  /** Method that returns the type of SRM request */
  public TRequestType requestType() {

    return requestType;
  }

  /** Method that returns the SRM request TRequestToken */
  public TRequestToken requestToken() {

    return requestToken;
  }

  /** Method that returns the VomsGridUser that issued this request */
  public GridUserInterface gridUser() {

    return gu;
  }

  /** Method that returns a long corresponding to the identifier of This object in persistence. */
  public long primaryKey() {

    return id;
  }

  /** Method used to set the log corresponding to the identifier of This object in persistence. */
  public void setPrimaryKey(long l) {

    this.id = l;
  }

  /** @return the userToken */
  public String getUserToken() {

    return userToken;
  }

  /** @return the retrytime */
  public Integer getRetrytime() {

    return retrytime;
  }

  /** @return the pinLifetime */
  public TLifeTimeInSeconds getPinLifetime() {

    return pinLifetime;
  }

  /** @return the spaceToken */
  public String getSpaceToken() {

    return spaceToken;
  }

  /** @return the status */
  public TReturnStatus getStatus() {

    return status;
  }

  /** @return the errstring */
  public String getErrstring() {

    return errstring;
  }

  /** @return the remainingTotalTime */
  public Integer getRemainingTotalTime() {

    return remainingTotalTime;
  }

  /** @return the nbreqfiles */
  public Integer getNbreqfiles() {

    return nbreqfiles;
  }

  /** @return the numOfCompleted */
  public Integer getNumOfCompleted() {

    return numOfCompleted;
  }

  /** @return the fileLifetime */
  public TLifeTimeInSeconds getFileLifetime() {

    return fileLifetime;
  }

  /** @return the deferredStartTime */
  public Integer getDeferredStartTime() {

    return deferredStartTime;
  }

  /** @return the numOfWaiting */
  public Integer getNumOfWaiting() {

    return numOfWaiting;
  }

  /** @return the numOfFailed */
  public Integer getNumOfFailed() {

    return numOfFailed;
  }

  /** @return the remainingDeferredStartTime */
  public Integer getRemainingDeferredStartTime() {

    return remainingDeferredStartTime;
  }

  public void setUserToken(String userToken) {

    this.userToken = userToken;
  }

  public void setRetrytime(Integer retrytime) {

    this.retrytime = retrytime;
  }

  public void setPinLifetime(TLifeTimeInSeconds pinLifetime) {

    this.pinLifetime = pinLifetime;
  }

  public void setSpaceToken(String spaceToken) {

    this.spaceToken = spaceToken;
  }

  public void setStatus(TReturnStatus status) {

    this.status = status;
  }

  public void setErrstring(String errstring) {

    this.errstring = errstring;
  }

  public void setRemainingTotalTime(Integer remainingTotalTime) {

    this.remainingTotalTime = remainingTotalTime;
  }

  public void setNbreqfiles(Integer nbreqfiles) {

    this.nbreqfiles = nbreqfiles;
  }

  public void setNumOfCompleted(Integer numOfCompleted) {

    this.numOfCompleted = numOfCompleted;
  }

  public void setFileLifetime(TLifeTimeInSeconds fileLifetime) {

    this.fileLifetime = fileLifetime;
  }

  public void setDeferredStartTime(Integer deferredStartTime) {

    this.deferredStartTime = deferredStartTime;
  }

  public void setNumOfWaiting(Integer numOfWaiting) {

    this.numOfWaiting = numOfWaiting;
  }

  public void setNumOfFailed(Integer numOfFailed) {

    this.numOfFailed = numOfFailed;
  }

  public void setRemainingDeferredStartTime(Integer remainingDeferredStartTime) {

    this.remainingDeferredStartTime = remainingDeferredStartTime;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append("RequestSummaryData [requestType=");
    builder.append(requestType);
    builder.append(", requestToken=");
    builder.append(requestToken);
    builder.append(", gu=");
    builder.append(gu);
    builder.append(", id=");
    builder.append(id);
    builder.append(", userToken=");
    builder.append(userToken);
    builder.append(", retrytime=");
    builder.append(retrytime);
    builder.append(", pinLifetime=");
    builder.append(pinLifetime);
    builder.append(", spaceToken=");
    builder.append(spaceToken);
    builder.append(", status=");
    builder.append(status);
    builder.append(", errstring=");
    builder.append(errstring);
    builder.append(", remainingTotalTime=");
    builder.append(remainingTotalTime);
    builder.append(", nbreqfiles=");
    builder.append(nbreqfiles);
    builder.append(", numOfCompleted=");
    builder.append(numOfCompleted);
    builder.append(", fileLifetime=");
    builder.append(fileLifetime);
    builder.append(", deferredStartTime=");
    builder.append(deferredStartTime);
    builder.append(", numOfWaiting=");
    builder.append(numOfWaiting);
    builder.append(", numOfFailed=");
    builder.append(numOfFailed);
    builder.append(", remainingDeferredStartTime=");
    builder.append(remainingDeferredStartTime);
    builder.append("]");
    return builder.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + ((deferredStartTime == null) ? 0 : deferredStartTime.hashCode());
    result = prime * result + ((errstring == null) ? 0 : errstring.hashCode());
    result = prime * result + ((fileLifetime == null) ? 0 : fileLifetime.hashCode());
    result = prime * result + ((gu == null) ? 0 : gu.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((nbreqfiles == null) ? 0 : nbreqfiles.hashCode());
    result = prime * result + ((numOfCompleted == null) ? 0 : numOfCompleted.hashCode());
    result = prime * result + ((numOfFailed == null) ? 0 : numOfFailed.hashCode());
    result = prime * result + ((numOfWaiting == null) ? 0 : numOfWaiting.hashCode());
    result = prime * result + ((pinLifetime == null) ? 0 : pinLifetime.hashCode());
    result =
        prime * result
            + ((remainingDeferredStartTime == null) ? 0 : remainingDeferredStartTime.hashCode());
    result = prime * result + ((remainingTotalTime == null) ? 0 : remainingTotalTime.hashCode());
    result = prime * result + ((requestToken == null) ? 0 : requestToken.hashCode());
    result = prime * result + ((requestType == null) ? 0 : requestType.hashCode());
    result = prime * result + ((retrytime == null) ? 0 : retrytime.hashCode());
    result = prime * result + ((spaceToken == null) ? 0 : spaceToken.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((userToken == null) ? 0 : userToken.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    RequestSummaryData other = (RequestSummaryData) obj;
    if (deferredStartTime == null) {
      if (other.deferredStartTime != null) {
        return false;
      }
    } else if (!deferredStartTime.equals(other.deferredStartTime)) {
      return false;
    }
    if (errstring == null) {
      if (other.errstring != null) {
        return false;
      }
    } else if (!errstring.equals(other.errstring)) {
      return false;
    }
    if (fileLifetime == null) {
      if (other.fileLifetime != null) {
        return false;
      }
    } else if (!fileLifetime.equals(other.fileLifetime)) {
      return false;
    }
    if (gu == null) {
      if (other.gu != null) {
        return false;
      }
    } else if (!gu.equals(other.gu)) {
      return false;
    }
    if (id != other.id) {
      return false;
    }
    if (nbreqfiles == null) {
      if (other.nbreqfiles != null) {
        return false;
      }
    } else if (!nbreqfiles.equals(other.nbreqfiles)) {
      return false;
    }
    if (numOfCompleted == null) {
      if (other.numOfCompleted != null) {
        return false;
      }
    } else if (!numOfCompleted.equals(other.numOfCompleted)) {
      return false;
    }
    if (numOfFailed == null) {
      if (other.numOfFailed != null) {
        return false;
      }
    } else if (!numOfFailed.equals(other.numOfFailed)) {
      return false;
    }
    if (numOfWaiting == null) {
      if (other.numOfWaiting != null) {
        return false;
      }
    } else if (!numOfWaiting.equals(other.numOfWaiting)) {
      return false;
    }
    if (pinLifetime == null) {
      if (other.pinLifetime != null) {
        return false;
      }
    } else if (!pinLifetime.equals(other.pinLifetime)) {
      return false;
    }
    if (remainingDeferredStartTime == null) {
      if (other.remainingDeferredStartTime != null) {
        return false;
      }
    } else if (!remainingDeferredStartTime.equals(other.remainingDeferredStartTime)) {
      return false;
    }
    if (remainingTotalTime == null) {
      if (other.remainingTotalTime != null) {
        return false;
      }
    } else if (!remainingTotalTime.equals(other.remainingTotalTime)) {
      return false;
    }
    if (requestToken == null) {
      if (other.requestToken != null) {
        return false;
      }
    } else if (!requestToken.equals(other.requestToken)) {
      return false;
    }
    if (requestType != other.requestType) {
      return false;
    }
    if (retrytime == null) {
      if (other.retrytime != null) {
        return false;
      }
    } else if (!retrytime.equals(other.retrytime)) {
      return false;
    }
    if (spaceToken == null) {
      if (other.spaceToken != null) {
        return false;
      }
    } else if (!spaceToken.equals(other.spaceToken)) {
      return false;
    }
    if (status == null) {
      if (other.status != null) {
        return false;
      }
    } else if (!status.equals(other.status)) {
      return false;
    }
    if (userToken == null) {
      if (other.userToken != null) {
        return false;
      }
    } else if (!userToken.equals(other.userToken)) {
      return false;
    }
    return true;
  }
}
