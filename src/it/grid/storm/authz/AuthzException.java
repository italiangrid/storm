/**
 * 
 */
package it.grid.storm.authz;

/**
 * @author zappi
 *
 */
public class AuthzException extends Exception {
    
    public AuthzException() {
        super();
    }

    public AuthzException(String message) {
        super(message);
    }

    public AuthzException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthzException(Throwable cause) {
        super(cause);
    }
}
