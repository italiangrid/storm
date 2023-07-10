/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/** */
package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.AuthzDBReaderException;
import it.grid.storm.griduser.SubjectAttribute;

/** @author zappi */
public interface SubjectPattern {

  public abstract boolean match(SubjectAttribute subjectAttribute);

  public abstract boolean isValidPattern() throws AuthzDBReaderException;
}
