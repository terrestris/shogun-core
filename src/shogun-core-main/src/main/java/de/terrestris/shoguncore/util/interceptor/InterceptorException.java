package de.terrestris.shoguncore.util.interceptor;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class InterceptorException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public InterceptorException() {
    }

    /**
     * @param message
     */
    public InterceptorException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InterceptorException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InterceptorException(String message, Throwable cause) {
        super(message, cause);
    }

}
