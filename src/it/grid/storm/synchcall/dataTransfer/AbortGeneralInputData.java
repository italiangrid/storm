/**
 * This class represents the general Abort Input Data associated with the SRM request Abort
 * @author  Magnoni Luca
 * @author  CNAF -INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.dataTransfer;

import org.apache.log4j.Logger;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;

public class AbortGeneralInputData
{  
    private static final Logger log = Logger.getLogger("dataTransfer");
    
    private GridUserInterface auth = null;
    private TRequestToken reqToken = null;
    private ArrayOfSURLs arrayOfSURLs = null;
    
    
    private int type;
  
    public  static int ABORT_REQUEST = 0;
    public static int ABORT_FILES = 1;
    
    public AbortGeneralInputData() {}

    private AbortGeneralInputData(GridUserInterface auth, TRequestToken reqToken, ArrayOfSURLs surlArray, int type)
                    //throws InvalidAbortFilesInputDataAttributeException
    {
        boolean ok = true; //= (!(surlArray == null));
        if (!ok)
            ;//throw new InvalidAbortFilesInputDataAttributeException(surlArray);

        this.auth = auth;
        this.reqToken = reqToken;
        this.arrayOfSURLs = surlArray;
        this.type = type;
    }
    
    public static AbortGeneralInputData make(AbortRequestInputData requestInputData) {
        //Create an AbortFiles data from an AbortRequest data
        //In this case the SURLArray MUST BE null.
        log.debug("abortRequest: Creating general input data from abortRequest inputdata.");
        if(requestInputData == null)
            return null;
        return new AbortGeneralInputData(requestInputData.getUser(), requestInputData.getRequestToken(),
                null, AbortGeneralInputData.ABORT_REQUEST);
        //this.auth = requestInputData.getUser();
        //this.reqToken = requestInputData.getRequestToken();
        //set type
        //this.type = AbortGeneralInputData.ABORT_REQUEST;
    }
    
    public static AbortGeneralInputData make(AbortFilesInputData requestInputData) {
        //Create an AbortGeneral data from an AbortFiles data
        //In this case the SURLArray MUST NOT BE null.
        log.debug("abortRequest: Creating general input data from abortFiles inputdata.");
        if (requestInputData == null)
            return null;
        else 
            return new AbortGeneralInputData(requestInputData.getUser(), requestInputData.getRequestToken(),
                requestInputData.getArrayOfSURLs(), AbortGeneralInputData.ABORT_FILES);
        //this.auth = requestInputData.getUser();
        //this.reqToken = requestInputData.getRequestToken();
        //this.arrayOfSURLs = requestInputData.getArrayOfSURLs();
        
        //Set Type
        //this.type = AbortGeneralInputData.ABORT_FILES;
    }
    
    public TRequestToken getRequestToken()
    {
        return reqToken;
    }

    public void setRequestToken(TRequestToken reqToken)
    {
        this.reqToken = reqToken;
    }

    public GridUserInterface getUser()
    {
        return this.auth;
    }

    public void setUser(GridUserInterface user)
    {
        this.auth = user;
    }
    
    public ArrayOfSURLs getArrayOfSURLs()
    {
        return arrayOfSURLs;
    }

    public void setArrayOfSURLs(ArrayOfSURLs arrayOfSURLs)
    {
        this.arrayOfSURLs = arrayOfSURLs;
    }
    
    public int getType() {
        return type;
    }
}
