/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/*
 * You may copy, distribute and modify this file under the terms of the INFN
 * GRID licence. For a copy of the licence please visit
 *
 * http://www.cnaf.infn.it/license.html
 *
 * Original design made by Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007
 *
 * $Id: AbstractGridUser.java 3604 2007-05-22 11:16:27Z rzappi $
 */

package it.grid.storm.griduser;

import it.grid.storm.common.types.VO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGridUser implements GridUserInterface {

  protected static final Logger log = LoggerFactory.getLogger(AbstractGridUser.class);
  protected DistinguishedName subjectDN = null;
  protected String proxyString = null;
  protected MapperInterface userMapperClass = null;
  protected LocalUser localUser = null;

  protected AbstractGridUser(MapperInterface mapperClass, String distinguishedName) {

    if (mapperClass == null || distinguishedName == null) {
      throw new IllegalArgumentException(
          "Provided null parameter: mapperClass=\'"
              + mapperClass
              + "\' distinguishedName=\'"
              + distinguishedName
              + "\'");
    }
    this.userMapperClass = mapperClass;
    this.setDistinguishedName(distinguishedName);
  }

  protected AbstractGridUser(MapperInterface mapperClass, String distinguishedName, String proxy) {

    this(mapperClass, distinguishedName);
    this.setProxyString(proxy);
  }

  void setUserMapper(MapperInterface mapperClass) {

    if (mapperClass == null) {
      throw new IllegalArgumentException("Provided null MapperInterface!");
    }
    this.userMapperClass = mapperClass;
  }

  void setDistinguishedName(String dnString) {

    if (dnString == null) {
      throw new IllegalArgumentException("Provided null DistinguishedName!");
    }
    this.subjectDN = new DistinguishedName(dnString);
  }

  public String getDn() {

    String dn = this.subjectDN.getDN();
    return dn;
  }

  public DistinguishedName getDistinguishedName() {

    return subjectDN;
  }

  void setProxyString(String proxy) {

    this.proxyString = proxy;
  }

  public String getProxyString() {

    return this.proxyString;
  }

  public String getUserCredentials() {

    return this.proxyString;
  }

  public LocalUser getLocalUser() throws CannotMapUserException {

    if (localUser == null) {
      try {
        if (this.hasVoms()) {
          localUser = userMapperClass.map(getDn(), this.getFQANsAsString());
        } else {
          localUser = userMapperClass.map(getDn(), null);
        }
      } catch (CannotMapUserException e) {
        log.error(
            "Mapping error: {}. Subject='{}'", e.getMessage(), subjectDN.getX500DN_rfc1779(), e);
        throw e;
      }
    }
    return localUser;
  }

  public abstract String[] getFQANsAsString();

  public abstract FQAN[] getFQANs();

  public abstract boolean hasVoms();

  public abstract VO getVO();
}
