package it.grid.storm.authz.sa.conf;

public class AuthzDBReaderException extends Exception {

    public AuthzDBReaderException() {
        super();
    }

    public AuthzDBReaderException(String message) {
        super(message);
    }

    public AuthzDBReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthzDBReaderException(Throwable cause) {
        super(cause);
    }
}
