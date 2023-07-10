/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.grid.storm.config.Configuration;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents a Request Token
 *
 * @author Magnoni Luca
 */
public class TRequestToken implements Serializable {

  private static final long serialVersionUID = -6926632390881024529L;

  public static final String PNAME_REQUESTOKEN = "requestToken";

  private final String value;

  private final Calendar expiration;

  private static final long REQUEST_LIFETIME =
      Configuration.getInstance().getExpiredRequestTime() * 1000;

  public TRequestToken(String requestToken, Date timestamp)
      throws InvalidTRequestTokenAttributesException {

    if (requestToken == null || requestToken.trim().isEmpty()) {
      throw new InvalidTRequestTokenAttributesException(requestToken);
    }
    this.value = requestToken;
    Calendar expiration = null;
    if (timestamp != null) {
      expiration = Calendar.getInstance();
      expiration.setTimeInMillis(timestamp.getTime() + REQUEST_LIFETIME);
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

  /** @return the expiration */
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
