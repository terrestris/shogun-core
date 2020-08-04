package de.terrestris.shoguncore.util.interceptor;

import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Johannes Weskamm
 * @author terrestris GmbH & Co. KG
 */
public class WmtsUtil {

    /**
     * The Logger.
     */
    private static final Logger LOG = getLogger(WmtsUtil.class);

    /**
     * Check if given request is a RESTful WMTS call
     * @param mutableRequest
     * @return
     */
    public static boolean isRestfulWmtsRequest(MutableHttpServletRequest mutableRequest) {
        String url = mutableRequest.getRequestURL().toString();
        // check for presence of tilematrixset, tilematrix, tilecol and tilerow
        boolean defaultRestfulRequest = Pattern.compile("[^\\/]*\\/[^\\/]*[^\\/]\\/\\d+\\/\\d+").matcher(url).find();
        boolean isWmtsUrl = Pattern.compile(".*/geoserver.action/.*/wmts/\\d+/(.*)").matcher(url).matches();
        return defaultRestfulRequest && isWmtsUrl;
    }

    /**
     * Check if given request is a RESTful GetFeatureInfo call
     * @param mutableRequest
     * @return
     */
    public static boolean isRestfulWmtsGetFeatureinfo(MutableHttpServletRequest mutableRequest) {
        if (!isRestfulWmtsRequest(mutableRequest)) {
            return false;
        }
        String url = mutableRequest.getRequestURL().toString();
        // weak test if we have at least 4 digits in the path for tilecol, tilerow, i and j
        return !isRestfulWmtsGetTile(mutableRequest) &&
            Pattern.compile("\\/\\d+\\/\\d+\\/\\d+\\/\\d+").matcher(url).find();
    }

    /**
     * Check if given request is a RESTful GetTile call
     * @param mutableRequest
     * @return
     */
    public static boolean isRestfulWmtsGetTile(MutableHttpServletRequest mutableRequest) {
        if (!isRestfulWmtsRequest(mutableRequest)) {
            return false;
        }
        String url = mutableRequest.getRequestURL().toString();
        String format = mutableRequest.getParameterIgnoreCase("format");
        if (format == null) {
            if (url.endsWith(".png") ||
                url.endsWith(".jpg") ||
                url.endsWith(".jpeg") ||
                url.endsWith(".gif")) {
                return true;
            }
        } else if (format.toLowerCase(Locale.ROOT).contains("image")) {
            // GeoServer way of handling image format in RESTful requests
            return true;
        }
        return false;
    }

    /**
     * Return the layer id for the given request
     * @param mutableRequest
     * @return
     */
    public static String getLayerId(MutableHttpServletRequest mutableRequest) {
        try {
            String part = mutableRequest.getRequestURL().toString().split("/wmts/")[1];
            return part.split("/")[0];
        } catch (Exception e) {
            return null;
        }
    }
}
