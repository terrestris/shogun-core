package de.terrestris.shoguncore.util.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class Response {

    /**
     *
     */
    private HttpStatus statusCode;

    /**
     *
     */
    private HttpHeaders headers;

    /**
     *
     */
    private byte[] body;

    /**
     *
     */
    public Response() {

    }

    /**
     * @param statusCode
     * @param headers
     * @param body
     */
    public Response(HttpStatus statusCode, HttpHeaders headers, byte[] body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body != null ? Arrays.copyOf(body, body.length) : null;
    }

    /**
     * @return the statusCode
     */
    public HttpStatus getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the headers
     */
    public HttpHeaders getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    /**
     * @return the body
     */
    @SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
    public byte[] getBody() {
        if (body == null) {
            return null;
        }
        return Arrays.copyOf(body, body.length);
    }

    /**
     * @param body the body to set
     */
    public void setBody(byte[] body) {
        if (body == null) {
            this.body = null;
            return;
        }
        this.body = Arrays.copyOf(body, body.length);
    }

    /**
     *
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("statusCode", getStatusCode())
            .append("headers", getHeaders())
            .append("body", getBody())
            .toString();
    }
}
