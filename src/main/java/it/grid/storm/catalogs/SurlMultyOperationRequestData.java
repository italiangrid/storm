package it.grid.storm.catalogs;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusStore;
import it.grid.storm.synchcall.surl.TokenDuplicationException;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

public abstract class SurlMultyOperationRequestData extends SurlRequestData
	implements SynchMultyOperationRequestData {

	private static final Logger log = LoggerFactory
		.getLogger(SurlMultyOperationRequestData.class);

	private TRequestToken generatedRequestToken = TRequestToken.getRandom();

	private boolean stored = false;

	public SurlMultyOperationRequestData(TSURL surl, TReturnStatus status)
		throws InvalidSurlRequestDataAttributesException {

		super(surl, status);
	}

	public synchronized void store() {

		if (stored) {
			return;
		}
		try {
			while (!stored) {
				try {
					if (this instanceof IdentityInputData) {
						SurlStatusStore.getInstance().store(
							generatedRequestToken,
							((IdentityInputData) this).getUser(),
							buildSurlStatusMap(SURL, status.getStatusCode(),
								status.getExplanation()));
					} else {
						SurlStatusStore.getInstance().store(
							generatedRequestToken,
							buildSurlStatusMap(SURL, status.getStatusCode(),
								status.getExplanation()));
					}
					stored = true;
				} catch (TokenDuplicationException e) {
					generatedRequestToken = TRequestToken.getRandom();
				}
			}
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage(), e);
			throw new IllegalStateException("Unexpected IllegalArgumentException: "
				+ e.getMessage());
		}
	}

	private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL surl,
		TStatusCode code, String explanation) throws IllegalArgumentException {

		if (surl == null || code == null) {
			throw new IllegalArgumentException(
				"Unable to build the status, null arguments: surl=" + surl
					+ " statusCode=" + code);
		}
		HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(
			1);
		surlStatusMap.put(surl, new TReturnStatus(code, explanation));
		return surlStatusMap;
	}

	@Override
	public TRequestToken getGeneratedRequestToken() {

		return generatedRequestToken;
	}

	@Override
	public TRequestToken getRequestToken() {

		return getGeneratedRequestToken();
	}

	/**
	 * Method used to set the Status associated to this chunk. If status is null,
	 * then nothing gets set!
	 */
	@Override
	public final void setStatus(TReturnStatus status) {

		super.setStatus(status);
		if (!(this instanceof PersistentChunkData)) {
			try {
				if (status.getExplanation() == null) {
					SurlStatusStore.getInstance().update(generatedRequestToken,
						this.SURL, status.getStatusCode());
				} else {
					SurlStatusStore.getInstance().update(generatedRequestToken,
						this.SURL, status.getStatusCode(), status.getExplanation());
				}
			} catch (IllegalArgumentException e) {
				// Never thrown
				throw new IllegalStateException("Unexpected IllegalArgumentException "
					+ "in updating status store: " + e.getMessage());
			} catch (UnknownTokenException e) {
				log.warn("Received an UnknownTokenException, probably the token has "
					+ "expired, unable to update its status in the store: {}", 
					e.getMessage());
			} catch (ExpiredTokenException e) {
				log.warn("Received an ExpiredTokenException. The token is expired, "
					+ "unable to update its status in the store: {}", e.getMessage());
			} catch (UnknownSurlException e) {
				log.warn("Received an UnknownSurlException, probably the token has "
					+ "expired, unable to update its status in the store: {}", 
					e.getMessage());
			}
		}
	}

	@Override
	protected final void setStatus(TStatusCode statusCode, String explanation) {

		super.setStatus(statusCode, explanation);
		if (!(this instanceof PersistentChunkData)) {
			try {
				if (explanation == null) {
					SurlStatusStore.getInstance().update(generatedRequestToken,
						this.SURL, statusCode);
				} else {
					SurlStatusStore.getInstance().update(generatedRequestToken,
						this.SURL, statusCode, explanation);
				}
			} catch (IllegalArgumentException e) {
				log.error(e.getMessage(), e);
				// Never thrown
				throw new IllegalStateException("Unexpected IllegalArgumentException "
					+ "in updating status store: " + e.getMessage());
			} catch (UnknownTokenException e) {
				// Never thrown
				log.warn("Received an UnknownTokenException, probably the token has "
					+ "expired, unable to update its status in the store: {}", 
					e.getMessage());
			} catch (ExpiredTokenException e) {
				log.warn("Received an ExpiredTokenException. The token is expired, "
					+ "unable to update its status in the store: {}", e.getMessage());
			} catch (UnknownSurlException e) {
				log.warn("Received an UnknownSurlException, probably the token has "
					+ "expired, unable to update its status in the store: {}", 
					e.getMessage());
			}
		}
	}

}
