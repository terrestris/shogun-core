package de.terrestris.shoguncore.importer.communication;

import java.util.List;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTDataFile extends RESTData {

    /**
     *
     */
    private String format;

    /**
     *
     */
    private String location;

    /**
     *
     */
    private String file;

    /**
     *
     */
    private String href;

    /**
     *
     */
    private String prj;

    /**
     *
     */
    private List<String> other;

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

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

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
     * @return the prj
     */
    public String getPrj() {
        return prj;
    }

    /**
     * @param prj the prj to set
     */
    public void setPrj(String prj) {
        this.prj = prj;
    }

    /**
     * @return the other
     */
    public List<String> getOther() {
        return other;
    }

    /**
     * @param other the other to set
     */
    public void setOther(List<String> other) {
        this.other = other;
    }

}
