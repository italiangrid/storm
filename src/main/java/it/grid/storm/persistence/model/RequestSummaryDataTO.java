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

package it.grid.storm.persistence.model;

import java.sql.Timestamp;

/**
 * Class that represents data of an asynchrnous Request, regardless of whether it is a Put, Get or
 * Copy, in the Persistence Layer: this is all raw data referring to the request proper, that is,
 * String and primitive types.
 * 
 * @author EGRID ICTP
 * @version 2.0
 * @date June 2005
 */
public class RequestSummaryDataTO {

  public static final String PTG_REQUEST_TYPE = "PTG";
  public static final String PTP_REQUEST_TYPE = "PTP";
  public static final String BOL_REQUEST_TYPE = "BOL";
  public static final String COPY_REQUEST_TYPE = "COP";

  private long id = -1; // id of request in persistence
  private String requestType = ""; // request type
  private String requestToken = ""; // request token
  private String clientDN = ""; // DN that issued request
  private String vomsAttributes = ""; // String containing all VOMS attributes
  private Timestamp timestamp = null;

  private boolean empty = true;
  private String userToken = null;
  private Integer retrytime = null;
  private Integer pinLifetime = null;
  private String spaceToken = null;
  private Integer status = null;
  private String errstring = null;
  private Integer remainingTotalTime = null;
  private Integer nbreqfiles = null;
  private Integer numOfCompleted = null;
  private Integer fileLifetime = null;
  private Integer deferredStartTime = null;
  private Integer numOfWaiting = null;
  private Integer numOfFailed = null;
  private Integer remainingDeferredStartTime = null;

  public boolean isEmpty() {

    return empty;
  }

  public long primaryKey() {

    return id;
  }

  public void setPrimaryKey(long l) {

    empty = false;
    id = l;
  }

  public String requestType() {

    return requestType;
  }

  public void setRequestType(String s) {

    empty = false;
    requestType = s;
  }

  public String requestToken() {

    return requestToken;
  }

  public void setRequestToken(String s) {

    empty = false;
    requestToken = s;
  }

  public String clientDN() {

    return clientDN;
  }

  public void setClientDN(String s) {

    empty = false;
    clientDN = s;
  }

  public String vomsAttributes() {

    return vomsAttributes;
  }

  public void setVomsAttributes(String s) {

    empty = false;
    vomsAttributes = s;
  }

  public Timestamp timestamp() {

    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {

    empty = false;
    this.timestamp = timestamp;
  }

  /**
   * @return the userToken
   */
  public String getUserToken() {

    return userToken;
  }

  /**
   * @return the retrytime
   */
  public Integer getRetrytime() {

    return retrytime;
  }

  /**
   * @return the pinLifetime
   */
  public Integer getPinLifetime() {

    return pinLifetime;
  }

  /**
   * @return the spaceToken
   */
  public String getSpaceToken() {

    return spaceToken;
  }

  /**
   * @return the status
   */
  public Integer getStatus() {

    return status;
  }

  /**
   * @return the errstring
   */
  public String getErrstring() {

    return errstring;
  }

  /**
   * @return the remainingTotalTime
   */
  public Integer getRemainingTotalTime() {

    return remainingTotalTime;
  }

  /**
   * @return the nbreqfiles
   */
  public Integer getNbreqfiles() {

    return nbreqfiles;
  }

  /**
   * @return the numOfCompleted
   */
  public Integer getNumOfCompleted() {

    return numOfCompleted;
  }

  /**
   * @return the fileLifetime
   */
  public Integer getFileLifetime() {

    return fileLifetime;
  }

  /**
   * @return the deferredStartTime
   */
  public Integer getDeferredStartTime() {

    return deferredStartTime;
  }

  /**
   * @return the numOfWaiting
   */
  public Integer getNumOfWaiting() {

    return numOfWaiting;
  }

  /**
   * @return the numOfFailed
   */
  public Integer getNumOfFailed() {

    return numOfFailed;
  }

  /**
   * @return the remainingDeferredStartTime
   */
  public Integer getRemainingDeferredStartTime() {

    return remainingDeferredStartTime;
  }

  public void setUserToken(String userToken) {

    this.userToken = userToken;
  }

  public void setRetrytime(Integer retrytime) {

    this.retrytime = retrytime;

  }

  public void setPinLifetime(Integer pinLifetime) {

    this.pinLifetime = pinLifetime;

  }

  public void setSpaceToken(String spaceToken) {

    this.spaceToken = spaceToken;

  }

  public void setStatus(Integer status) {

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

  public void setFileLifetime(Integer fileLifetime) {

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
    builder.append("RequestSummaryDataTO [id=");
    builder.append(id);
    builder.append(", requestType=");
    builder.append(requestType);
    builder.append(", requestToken=");
    builder.append(requestToken);
    builder.append(", clientDN=");
    builder.append(clientDN);
    builder.append(", vomsAttributes=");
    builder.append(vomsAttributes);
    builder.append(", timestamp=");
    builder.append(timestamp);
    builder.append(", empty=");
    builder.append(empty);
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
    result = prime * result + ((clientDN == null) ? 0 : clientDN.hashCode());
    result = prime * result + (int) (deferredStartTime ^ (deferredStartTime >>> 32));
    result = prime * result + (empty ? 1231 : 1237);
    result = prime * result + ((errstring == null) ? 0 : errstring.hashCode());
    result = prime * result + (int) (fileLifetime ^ (fileLifetime >>> 32));
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + (int) (nbreqfiles ^ (nbreqfiles >>> 32));
    result = prime * result + (int) (numOfCompleted ^ (numOfCompleted >>> 32));
    result = prime * result + (int) (numOfFailed ^ (numOfFailed >>> 32));
    result = prime * result + (int) (numOfWaiting ^ (numOfWaiting >>> 32));
    result = prime * result + (int) (pinLifetime ^ (pinLifetime >>> 32));
    result =
        prime * result + (int) (remainingDeferredStartTime ^ (remainingDeferredStartTime >>> 32));
    result = prime * result + (int) (remainingTotalTime ^ (remainingTotalTime >>> 32));
    result = prime * result + ((requestToken == null) ? 0 : requestToken.hashCode());
    result = prime * result + ((requestType == null) ? 0 : requestType.hashCode());
    result = prime * result + (int) (retrytime ^ (retrytime >>> 32));
    result = prime * result + ((spaceToken == null) ? 0 : spaceToken.hashCode());
    result = prime * result + (int) (status ^ (status >>> 32));
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((userToken == null) ? 0 : userToken.hashCode());
    result = prime * result + ((vomsAttributes == null) ? 0 : vomsAttributes.hashCode());
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
    RequestSummaryDataTO other = (RequestSummaryDataTO) obj;
    if (clientDN == null) {
      if (other.clientDN != null) {
        return false;
      }
    } else if (!clientDN.equals(other.clientDN)) {
      return false;
    }
    if (deferredStartTime != other.deferredStartTime) {
      return false;
    }
    if (empty != other.empty) {
      return false;
    }
    if (errstring == null) {
      if (other.errstring != null) {
        return false;
      }
    } else if (!errstring.equals(other.errstring)) {
      return false;
    }
    if (fileLifetime != other.fileLifetime) {
      return false;
    }
    if (id != other.id) {
      return false;
    }
    if (nbreqfiles != other.nbreqfiles) {
      return false;
    }
    if (numOfCompleted != other.numOfCompleted) {
      return false;
    }
    if (numOfFailed != other.numOfFailed) {
      return false;
    }
    if (numOfWaiting != other.numOfWaiting) {
      return false;
    }
    if (pinLifetime != other.pinLifetime) {
      return false;
    }
    if (remainingDeferredStartTime != other.remainingDeferredStartTime) {
      return false;
    }
    if (remainingTotalTime != other.remainingTotalTime) {
      return false;
    }
    if (requestToken == null) {
      if (other.requestToken != null) {
        return false;
      }
    } else if (!requestToken.equals(other.requestToken)) {
      return false;
    }
    if (requestType == null) {
      if (other.requestType != null) {
        return false;
      }
    } else if (!requestType.equals(other.requestType)) {
      return false;
    }
    if (retrytime != other.retrytime) {
      return false;
    }
    if (spaceToken == null) {
      if (other.spaceToken != null) {
        return false;
      }
    } else if (!spaceToken.equals(other.spaceToken)) {
      return false;
    }
    if (status != other.status) {
      return false;
    }
    if (timestamp == null) {
      if (other.timestamp != null) {
        return false;
      }
    } else if (!timestamp.equals(other.timestamp)) {
      return false;
    }
    if (userToken == null) {
      if (other.userToken != null) {
        return false;
      }
    } else if (!userToken.equals(other.userToken)) {
      return false;
    }
    if (vomsAttributes == null) {
      if (other.vomsAttributes != null) {
        return false;
      }
    } else if (!vomsAttributes.equals(other.vomsAttributes)) {
      return false;
    }
    return true;
  }

}
