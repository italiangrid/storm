package it.grid.storm.synchcall.data.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * This class represents the Mv Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * @author lucamag
 * @date May 28, 2008
 *
 */
public class MvInputData implements InputData {
    private GridUserInterface auth = null;
    private TSURL fromSURL = null;
    private TSURL toSURL = null;
    private ArrayOfTExtraInfo storageSystemInfo = null;

    public MvInputData() {
    }

    public MvInputData(GridUserInterface auth, TSURL fromSURL, TSURL toSURL, ArrayOfTExtraInfo extraInfoArray)
            throws InvalidMvInputAttributeException 
    {
        boolean ok = (fromSURL != null)&&(toSURL != null);
        if (!ok) {
            throw new InvalidMvInputAttributeException(toSURL, fromSURL);
        }
        this.auth = auth;
        this.fromSURL = fromSURL;
        this.toSURL = toSURL;
    }

    /**
     * Method that get/set SURL specified in SRM request.
     */

    public TSURL getFromSurl() {
        return fromSURL;
    }

    public TSURL getToSurl() {
        return toSURL;
    }

    public void setSurlInfo(TSURL surl) {
        this.fromSURL = fromSURL;
    }

    /**
     * Set User
     */
    public void setUser(GridUserInterface user) {
        this.auth = user;
    }

    /**
     * get User
     */
    public GridUserInterface getUser() {
        return this.auth;
    }
    
    /**
    * Set storageSystemInfo
    */
   public void setStorageSystemInfo(ArrayOfTExtraInfo storageSystemInfo) {
       this.storageSystemInfo = storageSystemInfo;
   }

   /**
    * get storageSystemInfo
    */
   public ArrayOfTExtraInfo getStorageSystemInfo() {
       return this.storageSystemInfo;
   }

}
