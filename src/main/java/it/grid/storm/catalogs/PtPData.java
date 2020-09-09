package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

public interface PtPData extends FileTransferData {

	/**
	 * Method that returns the space token supplied for this chunk of the srm
	 * request.
	 */
	public TSpaceToken getSpaceToken();

	/**
	 * Method that returns the requested pin life time for this chunk of the srm
	 * request.
	 */
	public TLifeTimeInSeconds pinLifetime();

	/**
	 * Method that returns the requested file life time for this chunk of the srm
	 * request.
	 */
	public TLifeTimeInSeconds fileLifetime();

	/**
	 * Method that returns the fileStorageType for this chunk of the srm request.
	 */
	public TFileStorageType fileStorageType();

	/**
	 * Method that returns the knownSizeOfThisFile supplied with this chunk of the
	 * srm request.
	 */
	public TSizeInBytes expectedFileSize();

	/**
	 * Method that returns the overwriteOption specified in the srm request.
	 */
	public TOverwriteMode overwriteOption();

	/**
	 * Method that sets the status of this request to SRM_SPACE_AVAILABLE; it
	 * needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_SPACE_AVAILABLE(String explanation);

	/**
	 * Method that sets the status of this request to SRM_DUPLICATION_ERROR; it
	 * needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_DUPLICATION_ERROR(String explanation);

}
