package it.grid.storm.catalogs;

/**
 * Class that represents data of an asynchrnous Request, regardless of whether
 * it is a Put, Get or Copy, in the Persistence Layer: this is all raw data
 * referring to the request proper, that is, String and primitive types.
 *
 * @author  EGRID ICTP
 * @version 2.0
 * @date    June 2005
 */
public class RequestSummaryDataTO {

    private long id = -1; //id of request in persistence
    private String requestType = "";    //request type
    private String requestToken = "";   //request token
    private String clientDN = "";       //DN that issued request
    private String vomsAttributes = ""; //String containing all VOMS attributes

    private boolean empty=true;

    public boolean isEmpty() {
        return empty;
    }

    public long primaryKey() {
        return id;
    }

    public void setPrimaryKey(long l) {
        empty = false;
        id = l;
    }

    public String requestType() {
        return requestType;
    }

    public void setRequestType(String s) {
        empty=false;
        requestType = s;
    }

    public String requestToken() {
        return requestToken;
    }

    public void setRequestToken(String s) {
        empty=false;
        requestToken = s;
    }

    public String clientDN() {
        return clientDN;
    }

    public void setClientDN(String s) {
        empty = false;
        clientDN = s;
    }

    public String vomsAttributes() {
        return vomsAttributes;
    }

    public void setVomsAttributes(String s) {
        empty = false;
        vomsAttributes = s;
    }



    public String toString() {
        return id + " " + requestType + " " + requestToken + " " + " " + clientDN + " " + vomsAttributes;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof RequestSummaryDataTO)) return false;
        RequestSummaryDataTO to = (RequestSummaryDataTO) o;
        if (empty && to.empty) return true;
        return !empty && !to.empty && (id==to.id) && requestType.equals(to.requestType) && requestToken.equals(to.requestToken) &&
            clientDN.equals(to.clientDN) && vomsAttributes.equals(to.vomsAttributes);
    }

    public int hashCode() {
        if (empty) return 0;
        int hash = 17;
        hash = 37*hash + new Long(id).hashCode();
        hash = 37*hash + requestType.hashCode();
        hash = 37*hash + requestToken.hashCode();
        hash = 37*hash + clientDN.hashCode();
        hash = 37*hash + vomsAttributes.hashCode();
        return hash;
    }
     
}
