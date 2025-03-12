/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser.mapper;

import it.grid.storm.jna.lcmaps.LcmapsAccountInterface;
import it.grid.storm.jna.lcmaps.LcmapsError;
import it.grid.storm.jna.lcmaps.LcmapsInterface;
import it.grid.storm.jna.lcmaps.LcmapsPoolindexInterface;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.jna.lcmaps.LcmapsAccountInfoT;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jna.LastErrorException;

public class LcmapsMapper implements MapperInterface {

  private static final Object lock = new Object();

  private static final Logger log = LoggerFactory.getLogger(LcmapsMapper.class);

  private static final String LCMAPS_DEFAULT_LOG_FILE = "/var/log/lcmaps.log";
  private static final String LCMAPS_LOG_FILE_PATH_ENV_VARIABLE = "LCMAPS_LOG_FILE";
  private static final short LCMAPS_LOG_TYPE = 3;
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

  public LocalUser map(String dn, String[] fqans) throws CannotMapUserException {

    log.debug("Mapping user with dn = {} and fqans='{}'", dn, ArrayUtils.toString(fqans));
    log.debug("Lcmaps log file is {}", LCMAPS_LOG_FILE);
    LcmapsAccountInfoT.ByReference account = new LcmapsAccountInfoT.ByReference();
    int uid = -1;
    int[] gids = null;

    synchronized (LcmapsMapper.lock) {

      try {

        log.debug("Initializing Lcmaps and logfile");
        if (LcmapsInterface.INSTANCE.lcmaps_init_and_logfile(LCMAPS_LOG_FILE, null, LCMAPS_LOG_TYPE) != 0) {
          fail(LcmapsError.INIT_FAILURE);
        }
        log.debug("Initializing Lcmaps account info");
  
        if (LcmapsAccountInterface.INSTANCE.lcmaps_account_info_init(account) != 0) {
          fail(LcmapsError.ACCOUNT_INITIALIZATION_FAILURE);
        }
        int numFqans = (fqans == null ? 0 : fqans.length);

        if (LcmapsPoolindexInterface.INSTANCE.lcmaps_return_account_without_gsi(dn, fqans,
            numFqans, 0, account) != 0) {
          fail(LcmapsError.RETURN_ACCOUNT_FAILED);
        }

        if (account.uid < 0) {
          log.error("Negative uid returned by lcmaps: {}", account.uid);
          fail(LcmapsError.RETURN_ACCOUNT_FAILED);
        }
        if (account.npgid < 0 || account.nsgid < 0) {
          log.error("Negative primary or secondary gid array size. npgid: {} nsgid: {}",
              account.npgid, account.nsgid);
          fail(LcmapsError.RETURN_ACCOUNT_FAILED);
        }
        int numGids = account.npgid + account.nsgid;
        if (numGids > account.npgid) {
          gids = new int[numGids];
          int index = 0;
          if (account.npgid > 0) {
            for (int id : account.pgid_list.getIntArray(0, account.npgid)) {
              gids[index] = id;
              index++;
            }
          } else {
            log.warn("No primary gid returned by Lcmaps! Mapping error");
          }
          for (int id : account.sgid_list.getIntArray(0, account.nsgid)) {
            gids[index] = id;
            index++;
          }
        } else {
          if (account.npgid > 0) {
            System.arraycopy(account.pgid_list.getIntArray(0, account.npgid), 0, gids, 0, account.npgid);
          }
        }

      } catch (LastErrorException e) {
        log.error("Unable to map user dn <{}> fqans <{}>. Error: {}. Error code: {}", dn,
            ArrayUtils.toString(fqans), e.getMessage(), e.getErrorCode(), e);
        fail(LcmapsError.RETURN_ACCOUNT_FAILED);
      } finally {
        if (LcmapsAccountInterface.INSTANCE.lcmaps_account_info_clean(account) != 0) {
          log.warn("LCMAPS error on cleaning account object");
        }
      }
    }
    log.info("Mapped user to : <uid={},gids={}>", account.uid, ArrayUtils.toString(gids));
    return new LocalUser(uid, gids, gids.length);

  }
}
