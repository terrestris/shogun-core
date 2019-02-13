package de.terrestris.shogun2.importer.communication;

import com.fasterxml.jackson.annotation.JsonRootName;

import de.terrestris.shogun2.importer.transform.RESTTransformChain;


/**
 * A task represents a unit of work to the importer needed to register one
 * new layer, or alter an existing one, and contains the following information:
 * <p>
 * * The data being imported
 * * The target store that is the destination of the import
 * * The target layer
 * * The data of a task, referred to as its source, is the data to be
 * processed as part of the task.
 * * The transformations that we need to apply to the data before it gets
 * imported
 * <p>
 * This data comes in a variety of forms including:
 * <p>
 * * A spatial file (Shapefile, GeoTiff, KML, etc...)
 * * A directory of spatial files
 * * A table in a spatial database
 * * A remote location that the server will download data from
 * <p>
 * A task is classified as either “direct” or “indirect”. A direct task is one
 * in which the data being imported requires no transformation to be imported.
 * It is imported directly. An example of such a task is one that involves
 * simply importing an existing Shapefile as is. An indirect task is one that
 * does require a transformation to the original import data. An example of an
 * indirect task is one that involves importing a Shapefile into an existing
 * PostGIS database. Another example of indirect task might involve taking a
 * CSV file as an input, turning a x and y column into a Point, remapping a
 * string column into a timestamp, and finally import the result into a PostGIS.
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
@JsonRootName(value = "task")
public class RESTImportTask extends AbstractRESTEntity {

    /**
     *
     */
    private Integer id;

    /**
     *
     */
    private String href;

    /**
     *
     */
    private String state;

    /**
     *
     */
    private String updateMode;

    /**
     *
     */
    private RESTData data;

    /**
     *
     */
    private RESTTarget target;

    /**
     *
     */
    private String progress;

    /**
     *
     */
    private RESTLayer layer;

    /**
     *
     */
    private String errorMessage;

    /**
     *
     */
    private RESTTransformChain transformChain;

    /**
     * Default constructor.
     */
    public RESTImportTask() {

    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @return the updateMode
     */
    public String getUpdateMode() {
        return updateMode;
    }

    /**
     * @return the data
     */
    public RESTData getData() {
        return data;
    }

    /**
     * @return the target
     */
    public RESTTarget getTarget() {
        return target;
    }

    /**
     * @return the progress
     */
    public String getProgress() {
        return progress;
    }

    /**
     * @return the layer
     */
    public RESTLayer getLayer() {
        return layer;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return the transformChain
     */
    public RESTTransformChain getTransformChain() {
        return transformChain;
    }

}
