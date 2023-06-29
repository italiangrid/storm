/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a PrepareToGetChunkData, that is part of a multifile
 * PrepareToGet srm request. It contains data about: the requestToken, the
 * fromSURL, the requested lifeTime of pinning, the TDirOption which tells
 * whether the requested SURL is a directory and if it must be recursed at all
 * levels, as well as the desired number of levels to recurse, the desired
 * transferProtocols in order of preference, the fileSize, and the transferURL
 * for the supplied SURL.
 * 
 * @author EGRID - ICTP Trieste
 * @date March 21st, 2005
 * @version 3.0
 */
public class AnonymousPtGData extends AnonymousFileTransferData implements
	PtGData {

	private static final Logger log = LoggerFactory
		.getLogger(AnonymousPtGData.class);

	/** requested lifetime of TURL: it is the pin time! */
	protected TLifeTimeInSeconds pinLifeTime;
	/** specifies if the request regards a directory and related info */
	protected TDirOption dirOption;
	/** size of file */
	protected TSizeInBytes fileSize;

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
	public AnonymousPtGData(TSURL SURL, TLifeTimeInSeconds lifeTime,
		TDirOption dirOption, TURLPrefix desiredProtocols, TSizeInBytes fileSize,
		TReturnStatus status, TTURL transferURL)
		throws InvalidPtGDataAttributesException,
		InvalidFileTransferDataAttributesException,
		InvalidSurlRequestDataAttributesException {

		super(SURL, desiredProtocols, status, transferURL);
		if (lifeTime == null || dirOption == null || fileSize == null) {
			log.debug("Invalid arguments: lifeTime={}, dirOption={}, fileSize={}", 
				lifeTime, dirOption, fileSize);
			throw new InvalidPtGDataAttributesException(SURL, lifeTime, dirOption,
				desiredProtocols, fileSize, status, transferURL);

		}
		this.pinLifeTime = lifeTime;
		this.dirOption = dirOption;
		this.fileSize = fileSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.catalogs.PtGData#getPinLifeTime()
	 */
	@Override
	public TLifeTimeInSeconds getPinLifeTime() {

		return pinLifeTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.catalogs.PtGData#getDirOption()
	 */
	@Override
	public TDirOption getDirOption() {

		return dirOption;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.catalogs.PtGData#getFileSize()
	 */
	@Override
	public TSizeInBytes getFileSize() {

		return fileSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.catalogs.PtGData#setFileSize(it.grid.storm.srm.types.TSizeInBytes
	 * )
	 */
	@Override
	public void setFileSize(TSizeInBytes size) {

		if (size != null) {
			fileSize = size;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.catalogs.PtGData#changeStatusSRM_FILE_PINNED(java.lang.String
	 * )
	 */
	@Override
	public void changeStatusSRM_FILE_PINNED(String explanation) {

		setStatus(TStatusCode.SRM_FILE_PINNED, explanation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PtGChunkData [pinLifeTime=");
		builder.append(pinLifeTime);
		builder.append(", dirOption=");
		builder.append(dirOption);
		builder.append(", fileSize=");
		builder.append(fileSize);
		builder.append(", transferProtocols=");
		builder.append(transferProtocols);
		builder.append(", SURL=");
		builder.append(SURL);
		builder.append(", status=");
		builder.append(status);
		builder.append(", transferURL=");
		builder.append(transferURL);
		builder.append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dirOption == null) ? 0 : dirOption.hashCode());
		result = prime * result + ((fileSize == null) ? 0 : fileSize.hashCode());
		result = prime * result
			+ ((pinLifeTime == null) ? 0 : pinLifeTime.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AnonymousPtGData other = (AnonymousPtGData) obj;
		if (dirOption == null) {
			if (other.dirOption != null) {
				return false;
			}
		} else if (!dirOption.equals(other.dirOption)) {
			return false;
		}
		if (fileSize == null) {
			if (other.fileSize != null) {
				return false;
			}
		} else if (!fileSize.equals(other.fileSize)) {
			return false;
		}
		if (pinLifeTime == null) {
			if (other.pinLifeTime != null) {
				return false;
			}
		} else if (!pinLifeTime.equals(other.pinLifeTime)) {
			return false;
		}
		return true;
	}

}
