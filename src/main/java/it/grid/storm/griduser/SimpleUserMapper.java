/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;

public class SimpleUserMapper implements MapperInterface {

  private static final Logger log = GridUserManager.log;

  public SimpleUserMapper() {}

  public LocalUser map(String dn, String[] fqans) throws CannotMapUserException {

    LocalUser localUser = null;
    int uid = 0;
    int gid = 0;
    try {
      String retrieveUserCmd = "id -r -u";
      String userIdStr = getOutput(retrieveUserCmd);
      // log.debug("Output = "+userIdStr);
      uid = Integer.parseInt(userIdStr);
    } catch (CannotMapUserException e) {
      log.error(e.getMessage(), e);
      throw e;
    } catch (NumberFormatException nfe) {
      log.error("Getting UID returns a result different from a integer");
      throw new CannotMapUserException(nfe);
    }

    try {
      String retrieveUserCmd = "id -r -g";
      String groupIdStr = getOutput(retrieveUserCmd);
      gid = Integer.parseInt(groupIdStr);
    } catch (CannotMapUserException ex) {
      log.error("Unable to retrieve Group ID from the system.", ex);
      throw ex;
    } catch (NumberFormatException nfe) {
      log.error("Getting GID returns a result different from a integer");
      throw new CannotMapUserException(nfe);
    }

    localUser = new LocalUser(uid, gid);
    return localUser;
  }

  private String getOutput(String command) throws CannotMapUserException {

    String result = null;
    try {
      Process child = Runtime.getRuntime().exec(command);
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));

      String line;
      int row = 0;
      while ((line = stdInput.readLine()) != null) {
        boolean lineOk = processOutput(row, line);
        if (lineOk) {
          result = line;
          break;
        }
        row++;
      }

      // process the Errors
      String errLine;
      while ((errLine = stdError.readLine()) != null) {
        log.warn("User Info Command Output contains an ERROR message {}", errLine);
        throw new CannotMapUserException(errLine);
      }

    } catch (IOException ex) {
      log.error("getUserInfo (id) I/O Exception: {}", ex);
      throw new CannotMapUserException(ex);
    }
    return result;
  }

  private boolean processOutput(int row, String line) {
    boolean result = false;
    if (row >= 0) {
      result = true;
    }
    return result;
  }
}
