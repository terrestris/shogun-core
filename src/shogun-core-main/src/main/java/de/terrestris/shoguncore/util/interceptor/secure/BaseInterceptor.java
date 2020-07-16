package de.terrestris.shoguncore.util.interceptor.secure;

import de.terrestris.shoguncore.util.application.ShogunCoreContextUtil;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * <p>BaseInterceptor class.</p>
 */
public class BaseInterceptor {

    /**
     *
     */
    private URI appUri = null;

    /**
     * <p>forbidRequest.</p>
     *
     * @param request a {@link de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest} object.
     * @return a {@link de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest} object.
     */
    protected MutableHttpServletRequest forbidRequest(MutableHttpServletRequest request) {
        this.setAppUriFromRequest(request);
        String redirectUri = appUri == null ? "" : appUri.toString();
        request.setRequestURI(redirectUri + "/response/forbidden.action");
        return request;
    }

    /**
     * <p>setAppUriFromRequest.</p>
     *
     * @param request a {@link de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest} object.
     */
    protected void setAppUriFromRequest(MutableHttpServletRequest request) {
        if (appUri == null) {
            try {
                appUri = ShogunCoreContextUtil.getApplicationURIFromRequest(request);
            } catch (URISyntaxException e) {
                // pass
            }
        }
    }
}
