package it.grid.storm.synchcall.surl;

public class UnknownSurlException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 4930389310396856355L;

	public UnknownSurlException() {
	}

	public UnknownSurlException(String message) {
		super(message);
	}

	public UnknownSurlException(Throwable cause) {

		super(cause);
	}

	public UnknownSurlException(String message, Throwable cause) {

		super(message, cause);
	}

}
