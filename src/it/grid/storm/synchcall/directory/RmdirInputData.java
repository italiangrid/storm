/**
 * This class represents the Rmdir Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;

public class RmdirInputData
{
    private GridUserInterface auth = null;
    private TSURL surl = null;
    private ArrayOfTExtraInfo storageSystemInfo = null;
    private Boolean recursive;

    public RmdirInputData()
    {
    }

    public RmdirInputData(GridUserInterface auth, TSURL surl, ArrayOfTExtraInfo storageSystemInfo, Boolean recursive)
                    throws InvalidRmdirInputAttributeException
    {
        boolean ok = (surl != null);
        if (!ok) throw new InvalidRmdirInputAttributeException(surl);

        this.auth = auth;
        this.surl = surl;
        this.storageSystemInfo = storageSystemInfo;
        this.recursive = recursive;
    }

    /**
     * Method that SURL specified in SRM request.
     */

    public TSURL getSurl()
    {
        return surl;
    }

    public void setSurl(TSURL surl)
    {
        this.surl = surl;
    }

    /**
     * Set User
     */
    public void setUser(GridUserInterface user)
    {
        this.auth = user;
    }

    /**
     * get User
     */
    public GridUserInterface getUser()
    {
        return this.auth;
    }

    /**
     * Get RecursiveFlag
     */
    public Boolean getRecursiveFlag()
    {
        return recursive;
    }

    /**
     * Set RecursiveFlag
     */
    public void setRecursiveFlag(Boolean flag)
    {
        this.recursive = flag;
    }

    /**
     * Get RecursiveFlag
     */
    public ArrayOfTExtraInfo getStorageSystemInfo()
    {
        return storageSystemInfo;
    }

    /**
     * Set RecursiveFlag
     */
    public void setStorageSystemInfo(ArrayOfTExtraInfo storageSystemInfo)
    {
        this.storageSystemInfo = storageSystemInfo;
    }
}
