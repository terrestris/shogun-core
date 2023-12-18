package de.terrestris.shoguncore.util.application;

import org.apache.http.client.utils.URIBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Daniel Koch
 */
public class ShogunCoreContextUtil {

    /**
     * Returns the full webapplication URI from a given request.
     * <p>
     * Example:
     * <p>
     * The following GET-request:
     * http://localhost:8080/mapmavin/user/resetPassword.action
     * will result in
     * http://localhost:8080/mapmavin/
     *
     * @param request
     * @return
     * @throws URISyntaxException
     */
    public static final URI getApplicationURIFromRequest(HttpServletRequest request)
        throws URISyntaxException {

        URI appURI = null;

        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        String path = request.getContextPath();

        appURI = new URIBuilder()
            .setScheme(scheme)
            .setHost(host)
            .setPort(port)
            .setPath(path)
            .build();

        return appURI;
    }

}
