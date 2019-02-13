package de.terrestris.shogun2.util.interceptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * An inputstream which reads the cached request body and has mutable
 * request URI and params.
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 * @see http://stackoverflow.com/questions/10210645/http-servlet-request-lose-params-from-post-body-after-read-it-once
 */
public class CachedServletInputStream extends ServletInputStream {

    /**
     *
     */
    private ByteArrayInputStream input;

    /**
     * Create a new input stream from the cached request body
     */
    public CachedServletInputStream(ByteArrayOutputStream cachedBytes) {
        input = new ByteArrayInputStream(cachedBytes.toByteArray());
    }

    /**
     *
     */
    @Override
    public int read() throws IOException {
        return input.read();
    }

    /**
     *
     */
    @Override
    public boolean isFinished() {
        return false;
    }

    /**
     *
     */
    @Override
    public boolean isReady() {
        return false;
    }

    /**
     *
     */
    @Override
    public void setReadListener(ReadListener readListener) {
    }
}
