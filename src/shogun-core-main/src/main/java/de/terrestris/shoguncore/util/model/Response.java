package de.terrestris.shoguncore.util.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

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
    public byte[] getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

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
        this.body = body;
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
