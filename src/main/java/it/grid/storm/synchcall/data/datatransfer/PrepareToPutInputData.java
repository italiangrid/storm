package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSizeInBytes;

public interface PrepareToPutInputData extends FileTransferInputData {

	/**
	 * @return the overwriteMode
	 */
	public TOverwriteMode getOverwriteMode();

	/**
	 * @return the fileSize
	 */
	public TSizeInBytes getFileSize();

	/**
	 * @return
	 */
	public TLifeTimeInSeconds getDesiredFileLifetime();

	/**
	 * @param desiredFileLifetime
	 */
	public void setDesiredFileLifetime(TLifeTimeInSeconds desiredFileLifetime);

	/**
	 * @param fileSize
	 */
	public void setFileSize(TSizeInBytes fileSize);

	/**
	 * @param overwriteMode
	 */
	public void setOverwriteMode(TOverwriteMode overwriteMode);

}
