package de.terrestris.shogun2.importer.communication;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

import de.terrestris.shogun2.importer.communication.AbstractRESTEntity;
import de.terrestris.shogun2.importer.communication.RESTData;
import de.terrestris.shogun2.importer.communication.RESTImportTask;
import de.terrestris.shogun2.importer.communication.RESTTargetDataStore;
import de.terrestris.shogun2.importer.communication.RESTTargetWorkspace;

/**
 * An import refers to the top level object and is a "session" like entity the
 * state of the entire import. It maintains information relevant to the import
 * as a whole such as user information, timestamps along with optional
 * information that is uniform along all tasks, such as a target workspace, the
 * shared input data (e.g. a directory, a database). An import is made of any
 * number of task objects.
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
@JsonRootName(value = "import")
public class RESTImport extends AbstractRESTEntity {

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
    private RESTTargetWorkspace targetWorkspace;

    /**
     *
     */
    private RESTTargetDataStore targetStore;

    /**
     *
     */
    private RESTData data;

    /**
     *
     */
    private List<RESTImportTask> importTasks;

    /**
     * Default constructor.
     */
    public RESTImport() {

    }

    /**
     * @return the targetWorkspace
     */
    public RESTTargetWorkspace getTargetWorkspace() {
        return targetWorkspace;
    }

    /**
     * @param targetWorkspace the targetWorkspace to set
     */
    public void setTargetWorkspace(RESTTargetWorkspace targetWorkspace) {
        this.targetWorkspace = targetWorkspace;
    }

    /**
     * @return the targetStore
     */
    public RESTTargetDataStore getTargetStore() {
        return targetStore;
    }

    /**
     * @param targetStore the targetStore to set
     */
    public void setTargetStore(RESTTargetDataStore targetStore) {
        this.targetStore = targetStore;
    }

    /**
     * @return the data
     */
    public RESTData getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(RESTData data) {
        this.data = data;
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
     * @return the importTasks
     */
    public List<RESTImportTask> getImportTasks() {
        return importTasks;
    }

}
