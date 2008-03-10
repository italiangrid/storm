/**
 * This class represents the Mkdir Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TSURL;

public class MkdirInputData {
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
