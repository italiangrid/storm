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
 * $Id: GridUser.java 3604 2007-05-22 11:16:27Z rzappi $
 */

package it.grid.storm.griduser;

import it.grid.storm.common.types.*;

class GridUser extends AbstractGridUser {

  GridUser(MapperInterface mapper, String distinguishedName) {

    super(mapper, distinguishedName);
  }

  GridUser(MapperInterface mapper, String distinguishedName, String proxy) {

    super(mapper, distinguishedName, proxy);
  }

  public VO getVO() {
    VO vo = VO.makeNoVo();
    return vo;
  }

  @Override
  public boolean hasVoms() {

    return false;
  }

  @Override
  public String[] getFQANsAsString() {

    return new String[0];
  }

  @Override
  public FQAN[] getFQANs() {

    return new FQAN[0];
  }

  @Override
  public boolean equals(Object obj) {

    boolean result = false;
    if (obj != null) {
      if (obj instanceof GridUserInterface) {
        GridUserInterface other = (GridUserInterface) obj;
        if (other.getDistinguishedName().equals(this.getDistinguishedName())) {
          result = true;
        } else {
          result = false;
        }
      }
    }
    return result;
  }

  public String toString() {

    return "Grid User (no VOMS): '" + getDistinguishedName().getX500DN_rfc1779() + "'";
  }
}
