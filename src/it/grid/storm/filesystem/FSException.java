package it.grid.storm.filesystem;

public class FSException extends Exception {

    public FSException() {
        super();
    }

    public FSException(String message) {
        super(message);
    }

    public FSException(String message, Throwable cause) {
        super(message, cause);
    }

    public FSException(Throwable cause) {
        super(cause);
    }
    
}
