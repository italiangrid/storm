package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousFileTransferInputData extends AbstractInputData implements
	FileTransferInputData {

	private final TSURL surl;
	private final TURLPrefix transferProtocols;
	private TLifeTimeInSeconds desiredPinLifetime = TLifeTimeInSeconds
		.makeEmpty();
	private TSpaceToken targetSpaceToken = TSpaceToken.makeEmpty();

	public AnonymousFileTransferInputData(TSURL surl, TURLPrefix transferProtocols)
		throws IllegalArgumentException {

		if (surl == null || transferProtocols == null) {
			throw new IllegalArgumentException(
				"Unable to create PrepareToPutInputData. Received nul parameters: surl = "
					+ surl + " , transferProtocols = " + transferProtocols);
		}
		this.surl = surl;
		this.transferProtocols = transferProtocols;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.datatransfer.FileTransferInputData#getSurl()
	 */
	@Override
	public TSURL getSurl() {

		return surl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.datatransfer.FileTransferInputData#
	 * getTransferProtocols()
	 */
	@Override
	public TURLPrefix getTransferProtocols() {

		return transferProtocols;
	}

	@Override
	public void setTargetSpaceToken(TSpaceToken targetSpaceToken) {

		this.targetSpaceToken = targetSpaceToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.datatransfer.FileTransferInputData#
	 * getTargetSpaceToken()
	 */
	@Override
	public TSpaceToken getTargetSpaceToken() {

		return targetSpaceToken;
	}

	@Override
	public TLifeTimeInSeconds getDesiredPinLifetime() {

		return desiredPinLifetime;
	}

	@Override
	public void setDesiredPinLifetime(TLifeTimeInSeconds desiredPinLifetime) {

		this.desiredPinLifetime = desiredPinLifetime;
	}

}
