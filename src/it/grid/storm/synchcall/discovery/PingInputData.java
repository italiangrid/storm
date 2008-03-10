/**
 * This class represents the Ping Input Data
 * @author  Alberto Forti
 * @author  CNAF-INFN Bologna
 * @date    Feb 2007
 * @version 1.0
 */

package it.grid.storm.synchcall.discovery;

import it.grid.storm.griduser.GridUserInterface;

public class PingInputData
{
    private GridUserInterface requestor = null;
    private String authorizationID = null;

    public PingInputData() {
        authorizationID = "";
    }

    public PingInputData(GridUserInterface gridUser, String authorizationID) {
        this.requestor = gridUser;
        if (authorizationID == null)
            this.authorizationID = "";
        else
            this.authorizationID = authorizationID;
    }

    /**
     * Set the Requestor
     * @param gridUser GridUserInterface
     */
    public void setRequestor(GridUserInterface gridUser) {
      this.requestor = gridUser;
    }

    /**
     * Get the Requestor
     * @return GridUserInterface
     */
    public GridUserInterface getRequestor() {
      return this.requestor;
    }

    /**
     * Set the authorizationID.
     * @param authorizationID String
     */
    public void setAuthorizationID(String authorizationID) {
        this.authorizationID = authorizationID;
    }

    /**
     * Get the authorizatioID.
     * @return String
     */
    public String getAuthorizationID() {
        return this.authorizationID;
    }

    public String toString() {
      return "";
    }

}
