package de.terrestris.shogun2.util.interceptor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * An implementation of HttpServletRequestWrapper.
 *
 * @see http://stackoverflow.com/questions/10210645/http-servlet-request-lose-params-from-post-body-after-read-it-once
 *
 * @author Daniel Koch
 *
 */
public class MutableHttpServletRequest extends HttpServletRequestWrapper {

	/**
	 * Holds custom parameter mapping
	 */
	private Map<String, String[]> customParameters;

	/**
	 *
	 */
	private String customRequestURI;

	/**
	 *
	 */
	private ByteArrayOutputStream cachedInputStream;

	/**
	 *
	 * @param request
	 */
	public MutableHttpServletRequest(HttpServletRequest request) {
		super(request);
		this.customRequestURI = request.getRequestURI();
		this.customParameters = new HashMap<String, String[]>(
				request.getParameterMap());
	}

	/**
	 *
	 * @param url
	 */
	public void setRequestURI(String url) {
		this.customRequestURI = url;
	}

	/**
	 *
	 * @param url
	 */
	public void setRequestURI(URI uri) {
		this.customRequestURI = uri.toString();
	}

	/**
	 *
	 */
	@Override
	public String getRequestURI() {
		if (this.customRequestURI != null) {
			return this.customRequestURI;
		} else {
			HttpServletRequest request = (HttpServletRequest) super.getRequest();
			return request.getRequestURI();
		}
	}

	/**
	 *
	 * @param key
	 * @param value
	 */
	public void setParameter(String key, String[] value) {
		if (!StringUtils.isEmpty(this.getParameter(key))) {
			this.removeParameter(key);
		}
		this.addParameter(key, value);
	}

	/**
	 *
	 * @param key
	 * @param value
	 */
	public void setParameter(String key, String value) {
		if (!StringUtils.isEmpty(this.getParameter(key))) {
			this.removeParameter(key);
		}
		this.addParameter(key, value);
	}

	/**
	 *
	 * @param key
	 * @param value
	 */
	public void addParameter(String key, String[] value) {
		customParameters.put(key, value);
	}

	/**
	 *
	 * @param key
	 * @param value
	 */
	public void addParameter(String key, String value) {
		String[] values = value.split(",", -1);
		customParameters.put(key, values);
	}

	/**
	 *
	 * @param key
	 */
	public void removeParameter(String key) {
		if (customParameters.get(key) != null) {
			customParameters.remove(key);
		}
	}

	/**
	 *
	 */
	@Override
	public String getParameter(String key) {
		if (customParameters.get(key) != null) {
			return StringUtils.join(customParameters.get(key), ",");
		} else {
			HttpServletRequest request = (HttpServletRequest) super.getRequest();
			return request.getParameter(key);
		}
	}

	/**
	 *
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		return customParameters;
	}

	/**
	 *
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (cachedInputStream == null) {
			cacheInputStream();
		}
		return new CachedServletInputStream(cachedInputStream);
	}

	/**
	 *
	 */
	@Override
	public BufferedReader getReader() throws IOException{
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	/**
	 * Cache the inputstream in order to read it multiple times. For
	 * convenience, I use apache.commons IOUtils
	 */
	private void cacheInputStream() throws IOException {
		cachedInputStream = new ByteArrayOutputStream();
		IOUtils.copy(super.getInputStream(), cachedInputStream);
	}

}
