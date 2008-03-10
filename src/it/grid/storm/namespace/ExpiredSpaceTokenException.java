package it.grid.storm.namespace;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ExpiredSpaceTokenException
    extends Exception {
    public ExpiredSpaceTokenException() {
        super();
    }

    public ExpiredSpaceTokenException(String message) {
        super(message);
    }

    public ExpiredSpaceTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredSpaceTokenException(Throwable cause) {
        super(cause);
    }
}
