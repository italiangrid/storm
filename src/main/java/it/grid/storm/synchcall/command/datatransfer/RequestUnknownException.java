package it.grid.storm.synchcall.command.datatransfer;

public class RequestUnknownException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 2766075955119694140L;

	public RequestUnknownException() {

	}

	public RequestUnknownException(String message) {

		super(message);
	}

	public RequestUnknownException(Throwable cause) {

		super(cause);
	}

	public RequestUnknownException(String message, Throwable cause) {

		super(message, cause);
	}

}
