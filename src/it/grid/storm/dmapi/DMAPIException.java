package it.grid.storm.dmapi;

public class DMAPIException extends Exception {

    private static final long serialVersionUID = 1484068785050730930L;

    public DMAPIException() {
    }

    public DMAPIException(String message) {
        super(message);
    }

    public DMAPIException(Throwable cause) {
        super(cause);
    }

    public DMAPIException(String message, Throwable cause) {
        super(message, cause);
    }

}
