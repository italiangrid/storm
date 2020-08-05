/**
 * 
 */
package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * @author Michele Dibenedetto
 * 
 */
public class IdentityPtPData extends AnonymousPtPData implements
	IdentityInputData {

	private final GridUserInterface auth;

	/**
	 * @param requestToken
	 * @param fromSURL
	 * @param lifeTime
	 * @param dirOption
	 * @param desiredProtocols
	 * @param fileSize
	 * @param status
	 * @param transferURL
	 * @throws InvalidPtGDataAttributesException
	 */
	public IdentityPtPData(GridUserInterface auth, TSURL SURL,
		TLifeTimeInSeconds pinLifetime, TLifeTimeInSeconds fileLifetime,
		TFileStorageType fileStorageType, TSpaceToken spaceToken,
		TSizeInBytes expectedFileSize, TURLPrefix transferProtocols,
		TOverwriteMode overwriteOption, TReturnStatus status, TTURL transferURL)
		throws InvalidPtPDataAttributesException,
		InvalidFileTransferDataAttributesException,
		InvalidSurlRequestDataAttributesException, IllegalArgumentException {

		super(SURL, pinLifetime, fileLifetime, fileStorageType, spaceToken,
			expectedFileSize, transferProtocols, overwriteOption, status, transferURL);
		if (auth == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: auth=" + auth);
		}
		this.auth = auth;
	}

	@Override
	public GridUserInterface getUser() {

		return auth;
	}

	@Override
	public String getPrincipal() {

		return this.auth.getDn();
	}

}
