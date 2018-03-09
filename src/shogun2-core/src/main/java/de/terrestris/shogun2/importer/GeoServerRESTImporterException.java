package de.terrestris.shogun2.importer;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class GeoServerRESTImporterException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public GeoServerRESTImporterException() {
    }

    /**
     * @param message
     */
    public GeoServerRESTImporterException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public GeoServerRESTImporterException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public GeoServerRESTImporterException(String message, Throwable cause) {
        super(message, cause);
    }

}
