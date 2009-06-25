package it.grid.storm.dmapi;

public class FileNotFoundException extends DMAPIException {

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
