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

public class MvInputData {
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
