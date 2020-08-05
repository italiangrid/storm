package it.grid.storm.synchcall.surl;

public class UnknownTokenException extends RuntimeException {

	private static final long serialVersionUID = -9056770694204136172L;

	public UnknownTokenException() {
	}

	public UnknownTokenException(String message) {

		super(message);
	}

	public UnknownTokenException(Throwable cause) {

		super(cause);
	}

	public UnknownTokenException(String message, Throwable cause) {

		super(message, cause);
	}

}
