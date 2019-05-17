package de.terrestris.shoguncore.importer.transform;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTDateFormatTransform extends RESTTransform {

    /**
     *
     */
    private String href;

    /**
     *
     */
    private String field;

    /**
     *
     */
    private String format;

    /**
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

}
