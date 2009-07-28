package it.grid.storm.ea;


public class FileNotFoundException extends ExtendedAttributesException {

    private static final long serialVersionUID = -4633331132951511667L;

    public FileNotFoundException() {
    }

    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(Throwable cause) {
        super(cause);
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
