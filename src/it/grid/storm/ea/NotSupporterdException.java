package it.grid.storm.ea;

public class NotSupporterdException extends ExtendedAttributesException {

    private static final long serialVersionUID = 8787160226577436742L;

    public NotSupporterdException() {
    }

    public NotSupporterdException(String message) {
        super(message);
    }

    public NotSupporterdException(Throwable cause) {
        super(cause);
    }

    public NotSupporterdException(String message, Throwable cause) {
        super(message, cause);
    }

}
