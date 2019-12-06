package de.terrestris.shoguncore.util.interceptor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Utility class for basic auth based requests in the geoserver interceptor context.
 * Accepts a user and password via the constructor, which will be used to add an
 * appropriate basic auth header to the requests.
 * <p>
 * Credits go to https://stackoverflow.com/a/2811841 and
 * https://stackoverflow.com/a/44200124
 *
 * @author Nils Bühner
 */
public class GeoserverAuthHeaderRequest extends MutableHttpServletRequest {

    private final String encoding;

    /**
     * Constructs a new servlet request with an additional x-geoserver-credentials header containing the given
     * username/password as HTTP basic auth encoded value.
     *
     * @param request  the original request
     * @param user     the geoserver user name
     * @param password the password
     */
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    public GeoserverAuthHeaderRequest(HttpServletRequest request, String user, String password) {
        super(request);
        this.encoding = "Basic " + Base64.getEncoder().encodeToString(user.concat(":").concat(password).getBytes());
    }

    /**
     *
     */
    @Override
    public String getHeader(String name) {
        if (name.equals("x-geoserver-credentials")) {
            return encoding;
        }
        return super.getHeader(name);
    }

    /**
     *
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        if (name.equalsIgnoreCase("x-geoserver-credentials")) {
            List<String> list = new ArrayList<>();
            list.add(encoding);
            return Collections.enumeration(list);
        }
        return super.getHeaders(name);
    }

    /**
     *
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> list = Collections.list(super.getHeaderNames());
        list.add("x-geoserver-credentials");
        return Collections.enumeration(list);
    }

}
