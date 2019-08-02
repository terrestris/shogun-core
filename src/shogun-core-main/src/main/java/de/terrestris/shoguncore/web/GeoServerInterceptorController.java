package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.service.GeoServerInterceptorService;
import de.terrestris.shoguncore.util.data.ResultSet;
import de.terrestris.shoguncore.util.model.Response;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @param <S>
 * @author Daniel Koch
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 */
@Controller
public class GeoServerInterceptorController<S extends GeoServerInterceptorService> {

    /**
     * The Logger.
     */
    private static final Logger LOG = getLogger(GeoServerInterceptorController.class);

    /**
     *
     */
    protected S service;

    /**
     *
     */
    public static final String ERROR_MESSAGE = "Error while requesting a " +
        "GeoServer resource: ";

    /**
     * @param request
     */
    @RequestMapping(value = {"/geoserver.action", "/geoserver.action/{endpoint}"}, method = {
        RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> interceptGeoServerRequest( HttpServletRequest request, @PathVariable(value="endpoint", required = false) Optional<String> endpoint ) {
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus responseStatus = HttpStatus.OK;
        byte[] responseBody = null;
        Response httpResponse = null;

        try {
            LOG.trace("Trying to intercept a GeoServer resource.");

            httpResponse = this.service.interceptGeoServerRequest(request, endpoint, true);

            responseStatus = httpResponse.getStatusCode();
            responseBody = httpResponse.getBody();
            responseHeaders = httpResponse.getHeaders();

            LOG.trace("Successfully intercepted a GeoServer resource.");

            return new ResponseEntity<byte[]>(responseBody,
                responseHeaders, responseStatus);

        } catch (Exception e) {
            LOG.error(ERROR_MESSAGE + e.getMessage());

            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> responseMsg = ResultSet.error(
                ERROR_MESSAGE + e.getMessage());

            return new ResponseEntity<Map<String, Object>>(responseMsg,
                responseHeaders, responseStatus);
        }

    }

    /**
     * A geoserver.action endpoint that does not pass headers such as Basic Authorization headers
     *
     * @param request
     */
    @RequestMapping(value = {"/geoserver-noauth.action", "/geoserver-noauth.action/{endpoint}"}, method = {
        RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> interceptGeoServerRequestWithoutAuth( HttpServletRequest request, @PathVariable(value="endpoint", required = false) Optional<String> endpoint ) {
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus responseStatus = HttpStatus.OK;
        byte[] responseBody = null;
        Response httpResponse = null;

        try {
            LOG.trace("Trying to intercept a GeoServer resource.");

            httpResponse = this.service.interceptGeoServerRequest(request, endpoint, false);

            responseStatus = httpResponse.getStatusCode();
            responseBody = httpResponse.getBody();
            responseHeaders = httpResponse.getHeaders();

            LOG.trace("Successfully intercepted a GeoServer resource.");

            return new ResponseEntity<byte[]>(responseBody,
                responseHeaders, responseStatus);

        } catch (Exception e) {
            LOG.error(ERROR_MESSAGE + e.getMessage());

            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> responseMsg = ResultSet.error(
                ERROR_MESSAGE + e.getMessage());

            return new ResponseEntity<Map<String, Object>>(responseMsg,
                responseHeaders, responseStatus);
        }

    }

    /**
     * @param service the service to set
     */
    @Autowired
    public void setService(S service) {
        this.service = service;
    }
}
