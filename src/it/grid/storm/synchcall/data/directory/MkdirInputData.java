package it.grid.storm.synchcall.data.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;

/**
 * This class is part of the StoRM project.
 * 
 * This class represents the Mkdir Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * 
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * @author lucamag
 * @date May 27, 2008
 *
 */


public class MkdirInputData implements InputData {
    private GridUserInterface auth = null;
    private TSURL surl = null;
    private ArrayOfTExtraInfo storageSystemInfo = null;

    public MkdirInputData() {
    }

    public MkdirInputData(GridUserInterface auth, TSURL surl, ArrayOfTExtraInfo extraInfoArray)
            throws InvalidMkdirInputAttributeException 
    {
        boolean ok = (!(surl == null));
        if (!ok) {
            throw new InvalidMkdirInputAttributeException(surl);
        }
        this.auth = auth;
        this.surl = surl;
    }

    /**
     * Method that SURL specified in SRM request.
     */

    public TSURL getSurl() {
        return surl;
    }

    public void setSurlInfo(TSURL surl) {
        this.surl = surl;
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
