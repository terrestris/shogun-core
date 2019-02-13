/**
 *
 */
package de.terrestris.shogun2.util.interceptor;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHeaders;

/**
 * Utility class for basic auth based requests in the geoserver interceptor context.
 * Accepts a user and password via the constructor, which will be used to add an
 * appropriate basic auth header to the requests.
 *
 * Credits go to https://stackoverflow.com/a/2811841 and
 * https://stackoverflow.com/a/44200124
 *
 * @author Nils Bühner
 *
 */
public class BasicAuthHeaderRequest extends MutableHttpServletRequest {

	/**
	 *
	 */
	public final String encoding;

	/**
	 *
	 * @param request
	 * @param user
	 * @param password
	 */
	public BasicAuthHeaderRequest(HttpServletRequest request, String user, String password) {
		super(request);
		this.encoding = "Basic " + Base64.getEncoder().encodeToString(user.concat(":").concat(password).getBytes());
	}

	/**
	 *
	 */
	@Override
	public String getHeader(String name) {

		if (name.equals(HttpHeaders.AUTHORIZATION)) {
			return encoding;
		}
		return super.getHeader(name);
	}

	/**
	 *
	 */
	@Override
	public Enumeration<String> getHeaders(String name) {
		if (name.equals(HttpHeaders.AUTHORIZATION)) {
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
		list.add(HttpHeaders.AUTHORIZATION);
		return Collections.enumeration(list);
	}

}
