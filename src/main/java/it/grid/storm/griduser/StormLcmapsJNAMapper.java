/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

import it.grid.storm.jna.lcmaps.StormLcmapsLibrary;
import it.grid.storm.jna.lcmaps.StormLcmapsLibrary.Errors;
import java.nio.IntBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Michele Dibenedetto */
public class StormLcmapsJNAMapper implements MapperInterface {

  private static final Logger log = LoggerFactory.getLogger(StormLcmapsJNAMapper.class);

  private final Object lock = new Object();

  private final String LCMAPS_DEFAULT_LOG_FILE = "/var/log/lcmaps.log";

  private final String LCMAPS_LOG_FILE_PATH_ENV_VARIABLE = "LCMAPS_LOG_FILE";

  private static final StormLcmapsJNAMapper instance = new StormLcmapsJNAMapper();

  private StormLcmapsJNAMapper() {}

  public static StormLcmapsJNAMapper getInstance() {

    return instance;
  }

  private String getLcmapsLogFile() {

    String lcmapsLogFile = System.getenv(LCMAPS_LOG_FILE_PATH_ENV_VARIABLE);
    if (lcmapsLogFile == null) {
      lcmapsLogFile = LCMAPS_DEFAULT_LOG_FILE;
    }
    return lcmapsLogFile.trim();
  }

  public LocalUser map(final String dn, final String[] fqans) throws CannotMapUserException {

    IntBuffer userId = IntBuffer.allocate(1), groupId = IntBuffer.allocate(1);
    int retVal;
    synchronized (lock) {
      retVal =
          StormLcmapsLibrary.INSTANCE.map_user(getLcmapsLogFile(), dn, fqans, 1, userId, groupId);
    }
    if (retVal != 0) {
      Errors error = StormLcmapsLibrary.Errors.getError(retVal);
      if (!error.equals(Errors.UNKNOW_ERROR)) {
        log.error(
            "Unable to call successfully native map_user() method. " + "Return value is {}", error);
      } else {
        log.error(
            "Unable to call successfully native map_user() method. " + "Unknown return value: {}",
            retVal);
      }
      throw new CannotMapUserException("LCMAPS error, cannot map user credentials to local user.");
    }
    LocalUser localUser = new LocalUser(userId.get(), new int[] {groupId.get()}, 1);
    return localUser;
  }
}
