/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.check.sanity.filesystem;

import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;

public class FakeGridUser implements GridUserInterface {

  /** */
  private DistinguishedName dn;

  /** @param dn */
  public FakeGridUser(String dn) {

    this.setDN(dn);
  }

  /** @param dnString */
  private void setDN(String dnString) {

    this.dn = new DistinguishedName(dnString);
  }

  public String getDn() {

    return dn.toString();
  }

  public LocalUser getLocalUser() throws CannotMapUserException {

    return new LocalUser(0, 0);
  }

  public DistinguishedName getDistinguishedName() {

    return this.dn;
  }

  public String toString() {

    return "Fake Grid User (no VOMS): '" + getDistinguishedName().getX500DN_rfc1779() + "'";
  }
}
