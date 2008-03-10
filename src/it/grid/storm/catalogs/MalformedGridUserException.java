package it.grid.storm.catalogs;

/**
 * This class represents an Exception thrown when the RequestSummaryCatalog
 * cannot create a VomsGridUser with the available data.
 *
 * @author EGRID - ICTP Trieste
 * @date   June, 2005
 * @version 1.0
 */
public class MalformedGridUserException extends Exception {

    public String toString() {
        return "MalformedGridUserException";
    }
}
