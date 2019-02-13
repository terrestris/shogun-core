package de.terrestris.shogun2.importer.communication;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTDataStore extends AbstractRESTEntity {

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
    public RESTDataStore() {

    }

    /**
     * @param name
     * @param type
     */
    public RESTDataStore(String name, String type) {
        this.name = name;
        this.type = type;
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
