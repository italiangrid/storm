package it.grid.storm.authz.sa.test;

import it.grid.storm.authz.*;
import it.grid.storm.authz.sa.*;
import it.grid.storm.authz.sa.model.*;
import it.grid.storm.griduser.*;
import org.apache.commons.logging.Log;

public class MockSpaceAuthz implements SpaceAuthzInterface {

  private final Log log = AuthzDirector.getLogger();

  public MockSpaceAuthz() {
  }

  /**
   * authorize
   *
   * @param guser GridUserInterface
   * @param srmSpaceOp SRMSpaceRequest
   * @return boolean
   */
  public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {
    log.debug("MOCK Space Authz : Authorize = Always TRUE");
    return true;
  }

  /**
   * refreshAuthzDB
   *
   * @todo Implement this it.grid.storm.authz.SpaceAuthzInterface method
   */
  public void refreshAuthzDB() {
    log.debug("MOCK Space Authz : Refresh DB : ;) ");
  }

  /**
   * setAuthzDB
   *
   * @param authzDB AuthzDBInterface
   */
  public void setAuthzDB(AuthzDBInterface authzDB) {
     log.debug("MOCK Space Authz : Set Authz DB :D ");
  }
}
