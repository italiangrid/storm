/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import com.google.common.collect.Maps;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SURLStatusStore;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SurlMultyOperationRequestData extends SurlRequestData
    implements SynchMultyOperationRequestData {

  private static final Logger log = LoggerFactory.getLogger(SurlMultyOperationRequestData.class);

  private TRequestToken generatedRequestToken = TRequestToken.getRandom();

  private boolean stored = false;

  public SurlMultyOperationRequestData(TSURL surl, TReturnStatus status)
      throws InvalidSurlRequestDataAttributesException {

    super(surl, status);
  }

  public synchronized void store() {

    if (stored) {
      return;
    }
    GridUserInterface user = null;
    if (this instanceof IdentityInputData) {
      user = ((IdentityInputData) this).getUser();
    }
    SURLStatusStore.INSTANCE.store(generatedRequestToken, user, buildSurlStatusMap(SURL, status));
    stored = true;
  }

  private static Map<TSURL, TReturnStatus> buildSurlStatusMap(TSURL surl, TReturnStatus status) {

    if (surl == null || status == null) {
      throw new IllegalArgumentException(
          "Unable to build the status, null arguments: surl=" + surl + " status=" + status);
    }
    Map<TSURL, TReturnStatus> surlStatusMap = Maps.newHashMap();
    surlStatusMap.put(surl, status);
    return surlStatusMap;
  }

  @Override
  public TRequestToken getGeneratedRequestToken() {

    return generatedRequestToken;
  }

  @Override
  public TRequestToken getRequestToken() {

    return getGeneratedRequestToken();
  }

  /**
   * Method used to set the Status associated to this chunk. If status is null, then nothing gets
   * set!
   */
  @Override
  public final void setStatus(TReturnStatus status) {

    super.setStatus(status);
    if (!(this instanceof PersistentChunkData)) {
      try {
        SURLStatusStore.INSTANCE.update(generatedRequestToken, this.SURL, status);
      } catch (UnknownTokenException e) {
        log.warn(
            "Received an UnknownTokenException, probably the token has "
                + "expired, unable to update its status in the store: {}",
            e.getMessage());
      } catch (ExpiredTokenException e) {
        log.warn(
            "Received an ExpiredTokenException. The token is expired, "
                + "unable to update its status in the store: {}",
            e.getMessage());
      } catch (UnknownSurlException e) {
        log.warn(
            "Received an UnknownSurlException, probably the token has "
                + "expired, unable to update its status in the store: {}",
            e.getMessage());
      }
    }
  }

  @Override
  protected final void setStatus(TStatusCode statusCode, String explanation) {

    super.setStatus(statusCode, explanation);
    if (!(this instanceof PersistentChunkData)) {
      try {
        SURLStatusStore.INSTANCE.update(generatedRequestToken, this.SURL, super.getStatus());
      } catch (UnknownTokenException e) {
        // Never thrown
        log.warn(
            "Received an UnknownTokenException, probably the token has "
                + "expired, unable to update its status in the store: {}",
            e.getMessage());
      } catch (ExpiredTokenException e) {
        log.warn(
            "Received an ExpiredTokenException. The token is expired, "
                + "unable to update its status in the store: {}",
            e.getMessage());
      } catch (UnknownSurlException e) {
        log.warn(
            "Received an UnknownSurlException, probably the token has "
                + "expired, unable to update its status in the store: {}",
            e.getMessage());
      }
    }
  }
}
