package it.grid.storm.synchcall.data.discovery;

import it.grid.storm.synchcall.data.InputData;


public interface PingInputData extends InputData
{

    /**
     * Get the authorizatioID.
     * @return String
     */
    public String getAuthorizationID();

}
