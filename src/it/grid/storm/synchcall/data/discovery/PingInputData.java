package it.grid.storm.synchcall.data.discovery;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.synchcall.data.InputData;

/**
 * This class is part of the StoRM project.
 * This class represents the Ping Input Data
 * 
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 *
 * @author lucamag
 * @author Alberto Forti
 * @date May 28, 2008
 *
 */

public class PingInputData implements InputData
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
