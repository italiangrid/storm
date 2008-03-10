/**
 * This class represents the Rm Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.directory;

import java.util.Vector;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;

public class RmInputData
{
    private GridUserInterface auth = null;
    ArrayOfSURLs surlarray = null;
    ArrayOfTExtraInfo infoarray = null;

    public RmInputData()
    {
    }

    public RmInputData(GridUserInterface auth, ArrayOfSURLs surlArray, ArrayOfTExtraInfo infoArray) throws InvalidRmInputAttributeException
    {
        boolean ok = (!(surlArray == null));
        if (!ok) throw new InvalidRmInputAttributeException(surlArray);
        
        this.auth = auth;
        this.surlarray = surlArray;
        this.infoarray = infoArray;
    }

    /**
     * Method that SURL specified in SRM request.
     */

    public ArrayOfSURLs getSurlArray()
    {
        return surlarray;
    }

    public void setSurlInfo(ArrayOfSURLs surlArray)
    {
        this.surlarray = surlArray;
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

}
