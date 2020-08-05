package it.grid.storm.synchcall.surl;

public class ExpiredTokenException extends RuntimeException {

	/**
     * 
     */
	private static final long serialVersionUID = 1016481239170741542L;

	public ExpiredTokenException() {
	}

	public ExpiredTokenException(String message) {
		super(message);
	}

	public ExpiredTokenException(Throwable cause) {
		super(cause);
	}

	public ExpiredTokenException(String message, Throwable cause) {
		super(message, cause);
	}

}
