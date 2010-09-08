package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TStatusCode;

/**
 * Class that represents some of the fileds in a row in the Persistence Layer:
 * this is all raw data referring to the ReducedPtGChunkData proper, that is
 * String and primitive types.
 *
 * @author  EGRID ICTP
 * @version 1.0
 * @date    November, 2006
 */
public class ReducedPtGChunkDataTO {
    private long primaryKey = -1; //ID primary key of record in DB
    private String fromSURL = " ";
//  TODO MICHELE USER_SURL added new fields
    private String normalizedStFN = null;
    private Integer surlUniqueID = null;
    
    private int status = StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED);
    private String errString = " ";

    public long primaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long n) {
        primaryKey = n;
    }

    public String fromSURL() {
        return fromSURL;
    }

    public void setFromSURL(String s) {
        fromSURL=s;
    }

    /**
	 * @param normalizedStFN the normalizedStFN to set
	 */
	public void setNormalizedStFN(String normalizedStFN) {

		this.normalizedStFN = normalizedStFN;
	}

	/**
	 * @return the normalizedStFN
	 */
	public String normalizedStFN() {

		return normalizedStFN;
	}

	/**
	 * @param surlUniqueID the sURLUniqueID to set
	 */
	public void setSurlUniqueID(Integer surlUniqueID) {

		this.surlUniqueID = surlUniqueID;
	}

	/**
	 * @return the sURLUniqueID
	 */
	public Integer surlUniqueID() {

		return surlUniqueID;
	}

	public int status() {
        return status;
    }

    public void setStatus(int n) {
        status = n;
    }

    public String errString() {
        return errString;
    }

    public void setErrString(String s) {
        errString = s;
    }

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(primaryKey);
		sb.append(" ");
		sb.append(fromSURL);
		sb.append(" ");
		sb.append(normalizedStFN);
		sb.append(" ");
		sb.append(surlUniqueID);
		sb.append(" ");
		sb.append(status);
		sb.append(" ");
		sb.append(errString);
		sb.append(" ");
		return sb.toString();
	}
}
