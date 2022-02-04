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

package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.grid.storm.config.Configuration;

/**
 * This class represents a Request Token
 * 
 * @author Magnoni Luca
 * 
 */
public class TRequestToken implements Serializable {

  private static final long serialVersionUID = -6926632390881024529L;

  public static final String PNAME_REQUESTOKEN = "requestToken";

  private final String value;

  private final Calendar expiration;

  private final long defaultExpirationTime = Configuration.getInstance().getCompletedRequestsAgentPurgeAge();

  public TRequestToken(String requestToken, Date timestamp)
      throws InvalidTRequestTokenAttributesException {

    if (requestToken == null || requestToken.trim().isEmpty()) {
      throw new InvalidTRequestTokenAttributesException(requestToken);
    }
    this.value = requestToken;
    Calendar expiration = null;
    if (timestamp != null) {
      expiration = Calendar.getInstance();
      expiration.setTimeInMillis(timestamp.getTime() + defaultExpirationTime * 1000);
    }
    this.expiration = expiration;
  }

  public TRequestToken() throws InvalidTRequestTokenAttributesException {
    this(UUID.randomUUID().toString(), Calendar.getInstance().getTime());
  }

  public static TRequestToken getRandom() {

    UUID token = UUID.randomUUID();
    try {
      return new TRequestToken(token.toString(), Calendar.getInstance().getTime());
    } catch (InvalidTRequestTokenAttributesException e) {
      // never thrown
      throw new IllegalStateException("Unexpected InvalidTRequestTokenAttributesException", e);
    }
  }

  @JsonIgnore
  public boolean hasExpirationDate() {

    return expiration != null;
  }

  @JsonIgnore
  public boolean isExpired() throws IllegalStateException {

    if (!hasExpirationDate()) {
      throw new IllegalStateException(
          "Unable to check expiration, the token han not an expiration date");
    }
    return expiration.before(Calendar.getInstance());
  }

  /**
   * @return the expiration
   */
  public Calendar getExpiration() {

    return expiration;
  }

  public void updateExpiration(Date expiration) {

    this.expiration.setTime(expiration);
  }

  public String getValue() {

    return value;
  }

  public String toString() {

    return value;
  }

  public static TRequestToken decode(Map<String, Object> inputParam, String fieldName)
      throws InvalidTRequestTokenAttributesException {

    return new TRequestToken((String) inputParam.get(fieldName), null);
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
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    TRequestToken other = (TRequestToken) obj;
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
