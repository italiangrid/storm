/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.sa;

import java.util.List;

public interface AuthzDBReaderInterface {

  public void addAuthzDB(String dbFileName) throws AuthzDBReaderException;

  public List<String> getAuthzDBNames();

  public void onChangeAuthzDB(String authzDBName) throws AuthzDBReaderException;

  public AuthzDBInterface getAuthzDB(String authzDBName) throws AuthzDBReaderException;

  public long getLastParsed(String dbFileName) throws AuthzDBReaderException;
}
