package de.terrestris.shogun2.importer.communication;

import com.fasterxml.jackson.annotation.JsonRootName;

/***
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
@JsonRootName(value = "dataStore")
public class RESTStore extends AbstractRESTEntity {

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String type;

    /**
     * Default constructor.
     */
    public RESTStore() {

    }

    /**
     * @param name
     */
    public RESTStore(String name) {
        super();
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

}
