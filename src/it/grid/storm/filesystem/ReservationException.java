package it.grid.storm.filesystem;

/**
 * Super class that represents a generic reservation exception.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    May 2006
 */
public class ReservationException extends Exception {

    private String error = "";

    /**
     * Public constructor requiring a String explaining the nature of the error.
     * If the String is null, then an empty one is used instead.
     */
    public ReservationException(String error) {
        if (error!=null) this.error=error;
    }

    public String toString() {
        return error;
    }
}
