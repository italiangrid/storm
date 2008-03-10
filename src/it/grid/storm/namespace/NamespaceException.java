package it.grid.storm.namespace;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class NamespaceException
    extends Exception {
    public NamespaceException() {
        super();
    }

    public NamespaceException(String message) {
        super(message);
    }

    public NamespaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NamespaceException(Throwable cause) {
        super(cause);
    }
}
