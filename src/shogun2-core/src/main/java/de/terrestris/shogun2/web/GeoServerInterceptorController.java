package de.terrestris.shogun2.web;

import de.terrestris.shogun2.service.GeoServerInterceptorService;
import de.terrestris.shogun2.util.data.ResultSet;
import de.terrestris.shogun2.util.model.Response;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

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
    private static final String ERROR_MESSAGE = "Error while requesting a " +
        "GeoServer resource: ";

    /**
     * @param request
     */
    @RequestMapping(value = "/geoserver.action", method = {
        RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> interceptGeoServerRequest(HttpServletRequest request) {

        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus responseStatus = HttpStatus.OK;
        byte[] responseBody = null;
        Response httpResponse = null;

        try {

            LOG.trace("Trying to intercept a GeoServer resource.");

            httpResponse = this.service.interceptGeoServerRequest(request);

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
