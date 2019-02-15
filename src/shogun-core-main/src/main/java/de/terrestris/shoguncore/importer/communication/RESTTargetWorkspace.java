package de.terrestris.shoguncore.importer.communication;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTTargetWorkspace extends AbstractRESTEntity {

    /**
     *
     */
    private RESTWorkspace workspace;

    /**
     * Default constructor.
     */
    public RESTTargetWorkspace() {

    }

    /**
     * @param name
     */
    public RESTTargetWorkspace(String name) {
        this.workspace = new RESTWorkspace(name);
    }

    /**
     * @return the workspace
     */
    public RESTWorkspace getWorkspace() {
        return workspace;
    }

    /**
     * @param workspace the workspace to set
     */
    public void setWorkspace(RESTWorkspace workspace) {
        this.workspace = workspace;
    }

}
