/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;

/**
 * 
 */
public class StormLcmapsJNAMapper implements MapperInterface {

  private static final Logger log = LoggerFactory.getLogger(StormLcmapsJNAMapper.class);

  private final Object lock = new Object();

  private static final String LCMAPS_DEFAULT_LOG_FILE = "/var/log/lcmaps.log";
  private static final String LCMAPS_LOG_FILE_PATH_ENV_VARIABLE = "LCMAPS_LOG_FILE";

  private static final Short LCMAPS_LOG_TYPE = Short.valueOf("1");

  private static final String LCMAPS_LOG_FILE;

  static {
    String lcmapsLogFile = System.getenv(LCMAPS_LOG_FILE_PATH_ENV_VARIABLE);
    if (lcmapsLogFile == null) {
      lcmapsLogFile = LCMAPS_DEFAULT_LOG_FILE;
    }
    LCMAPS_LOG_FILE = lcmapsLogFile.trim();
  }

  private void fail(LcmapsError error) throws CannotMapUserException {
    log.error(error.getMessage());
    throw new CannotMapUserException(error.getMessage());
  }

  public LocalUser map(final String dn, final String[] fqans) throws CannotMapUserException {

    int uid = -1;
    int gid = -1;
    synchronized (lock) {

      if (LcmapsInterface.INSTANCE.lcmaps_init_and_logfile(LCMAPS_LOG_FILE, null, LCMAPS_LOG_TYPE) != 0) {
        fail(LcmapsError.INIT_FAILURE);
      }
 
      lcmaps_account_info_t lcmapsAccount = new lcmaps_account_info_t();
      if (LcmapsAccountInterface.INSTANCE.lcmaps_account_info_init(lcmapsAccount) != 0) {
        fail(LcmapsError.ACCOUNT_INITIALIZATION_FAILURE);
      }

      log.debug("LCMAPS account info retrieved: {}", lcmapsAccount);

      if (LcmapsPoolindexInterface.INSTANCE.lcmaps_return_account_without_gsi(dn, fqans, fqans.length, lcmapsAccount) != 0) {
        if (LcmapsAccountInterface.INSTANCE.lcmaps_account_info_clean(lcmapsAccount) != 0) {
          log.warn("LCMAPS error on cleaning account object");
        }
        fail(LcmapsError.RETURN_ACCOUNT_FAILED);
      }

      uid = lcmapsAccount.uid;

      if ((lcmapsAccount.npgid + lcmapsAccount.nsgid) == 0) {
        if (LcmapsAccountInterface.INSTANCE.lcmaps_account_info_clean(lcmapsAccount) != 0) {
          log.warn("LCMAPS error on cleaning account object");
        }
        fail(LcmapsError.NO_GIDS_RETURNED);
      }

      if (lcmapsAccount.npgid > 0) {
        gid = lcmapsAccount.pgid_list.getValue();
      } else {
        if (lcmapsAccount.nsgid > 0) {
          gid = lcmapsAccount.sgid_list.getValue();
        } else {
          if (LcmapsAccountInterface.INSTANCE.lcmaps_account_info_clean(lcmapsAccount) != 0) {
            log.warn("LCMAPS error on cleaning account object");
          }
          fail(LcmapsError.UNREACHIBLE_CODE);
        }
      }
      
      if (LcmapsAccountInterface.INSTANCE.lcmaps_account_info_clean(lcmapsAccount) != 0) {
        log.warn("LCMAPS error on cleaning account object");
      }
    }
    return new LocalUser(uid, gid);
  }
}
