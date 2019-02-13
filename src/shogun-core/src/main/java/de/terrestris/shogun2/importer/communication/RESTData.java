package de.terrestris.shogun2.importer.communication;

/**
 * A data is the description of the source data of a import (overall) or a
 * task. In case the import has a global data definition, this normally refers
 * to an aggregate store such as a directory or a database, and the data
 * associated to the tasks refers to a single element inside such aggregation,
 * such as a single file or table.
 * <p>
 * A import can have a "data" representing the source of the data to be
 * imported. The data can be of different types, in particular: "file",
 * "directory", "mosaic", "database" and "remote". During the import
 * initialization the importer will scan the contents of said resource, and
 * generate import tasks for each data found in it.
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTData extends AbstractRESTEntity {

    /**
     *
     */
    private String type;

    /**
     *
     */
    private String format;

    /**
     *
     */
    private String file;

    /**
     *
     */
    private String location;

    /**
     * Default constructor.
     */
    public RESTData() {

    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

}
