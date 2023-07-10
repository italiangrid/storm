/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

interface MapperInterface {

  public LocalUser map(final String dn, final String[] fqans) throws CannotMapUserException;
}
