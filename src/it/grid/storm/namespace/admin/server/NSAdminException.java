package it.grid.storm.namespace.admin.server;

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
public class NSAdminException
    extends Exception {
    public NSAdminException() {
        super();
    }

    public NSAdminException(String message) {
        super(message);
    }

    public NSAdminException(String message, Throwable cause) {
        super(message, cause);
    }

    public NSAdminException(Throwable cause) {
        super(cause);
    }
}
