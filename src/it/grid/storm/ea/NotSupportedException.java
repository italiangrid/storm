package it.grid.storm.ea;

public class NotSupportedException extends ExtendedAttributesException {

    private static final long serialVersionUID = 8787160226577436742L;

    public NotSupportedException() {
    }

    public NotSupportedException(String message) {
        super(message);
    }

    public NotSupportedException(Throwable cause) {
        super(cause);
    }

    public NotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

}
