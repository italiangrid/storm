package it.grid.storm.info.model;

import it.grid.storm.srm.types.TSizeInBytes;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author R.Zappi
 * @version 1.0
 */
public class VOInfoStatusData {

    private String voInfoLocalIdentifier = null;
    private TSizeInBytes usedSpaceNearLine = TSizeInBytes.makeEmpty();
    private TSizeInBytes availableSpaceNearLine = TSizeInBytes.makeEmpty();
    private TSizeInBytes ReservedSpaceNearLine = TSizeInBytes.makeEmpty();
    private TSizeInBytes usedSpaceOnLine = TSizeInBytes.makeEmpty();
    private TSizeInBytes availableSpaceOnLine = TSizeInBytes.makeEmpty();
    private TSizeInBytes ReservedSpaceOnLine = TSizeInBytes.makeEmpty();


    public VOInfoStatusData() {
    }

    public void setVOInfoLocalID(String voInfoLocalID) {
        this.voInfoLocalIdentifier = voInfoLocalID;
    }

    public String getVOInfoLocalID() {
        return this.voInfoLocalIdentifier;
    }



}
