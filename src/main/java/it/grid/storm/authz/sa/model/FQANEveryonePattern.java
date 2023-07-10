/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/** */
package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.AuthzDBReaderException;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.SubjectAttribute;

/** @author zappi */
public class FQANEveryonePattern extends FQANPattern implements Everyone {

  /*
   * Return always true because the pattern is built programmatically, and it is supposed to be
   * valid.
   *
   * @see it.grid.storm.authz.sa.model.SubjectPattern#isValidPattern()
   */
  @Override
  public boolean isValidPattern() throws AuthzDBReaderException {

    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see it.grid.storm.authz.sa.model.SubjectPattern#match(it.grid.storm.griduser .FQAN)
   */
  @Override
  public boolean match(SubjectAttribute sa) {

    boolean result = false;
    if (sa instanceof FQAN) {
      result = true;
    }
    return result;
  }

  @Override
  public String toString() {

    return Everyone.EVERYONE;
  }
}
