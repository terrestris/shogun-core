package de.terrestris.shoguncore.importer.communication;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
@JsonRootName(value = "workspace")
public class RESTWorkspace extends AbstractRESTEntity {

    /**
     *
     */
    private String name;

    /**
     * Default constructor.
     */
    public RESTWorkspace() {

    }

    /**
     * @param name
     */
    public RESTWorkspace(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
