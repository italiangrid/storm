package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
//import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.griduser.GridUserInterface;

/**
 * This class represents the SummaryData associated with the SRM request. It
 * contains info about: Primary Key of request, TRequestType, TRequestToken,
 * VomsGridUser.
 *
 * @author  EGRID - ICTP Trieste
 * @date    March 18th, 2005
 * @version 4.0
 */
public class RequestSummaryData  {

    private TRequestType requestType = null;    //request type of SRM request
    private TRequestToken requestToken = null;  //TRequestToken of SRM request
    private GridUserInterface gu = null; //VomsGridUser that issued This request
    private long id = -1;           //long representing This object in persistence

    public RequestSummaryData(TRequestType rtype, TRequestToken rtoken, GridUserInterface gu) throws InvalidRequestSummaryDataAttributesException {
        boolean ok = rtype!=null &&
            rtoken!=null &&
            gu!=null;
        if (!ok) throw new InvalidRequestSummaryDataAttributesException(rtype,rtoken,gu);
        this.requestType = rtype;
        this.requestToken = rtoken;
        this.gu = gu;
    }

    /**
     * Method that returns the type of SRM request
     */
    public TRequestType requestType() {
        return requestType;
    }

    /**
     * Method that returns the SRM request TRequestToken
     */
    public TRequestToken requestToken() {
        return requestToken;
    }

    /**
     * Method that returns the VomsGridUser that issued this request
     */
    public GridUserInterface gridUser() {
        return gu;
    }

    /**
     * Method that returns a long corresponding to the identifier of This
     * object in persistence.
     */
    public long primaryKey() {
        return id;
    }

    /**
     * Method used to set the log corresponding to the identifier of This
     * object in persistence.
     */
    public void setPrimaryKey(long l) {
        this.id = l;
    }





    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SummaryRequestData");
        sb.append("ID="); sb.append(id);
        sb.append("; requestType="); sb.append(requestType);
        sb.append("; requestToken="); sb.append(requestToken);
        sb.append("; vomsGridUser="); sb.append(gu);
        sb.append(".");
        return sb.toString();
    }

    public int hashCode() {
        int hash = 17;
        hash = 37*hash + new Long(id).hashCode();
        hash = 37*hash + requestType.hashCode();
        hash = 37*hash + requestToken.hashCode();
        hash = 37*hash + gu.hashCode();
        return hash;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof RequestSummaryData)) return false;
        RequestSummaryData rsd = (RequestSummaryData) o;
        return id==rsd.id &&
            requestType.equals(rsd.requestType) &&
            requestToken.equals(rsd.requestToken) &&
            gu.equals(rsd.gu);
    }
}
