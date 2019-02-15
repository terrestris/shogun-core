package de.terrestris.shoguncore.importer.communication;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTTargetDataStore extends AbstractRESTEntity {

    /**
     *
     */
    private RESTDataStore dataStore;

    /**
     * Default constructor.
     */
    public RESTTargetDataStore() {

    }

    /**
     * @param name
     * @param type
     */
    public RESTTargetDataStore(String name, String type) {
        this.dataStore = new RESTDataStore(name, type);
    }

    /**
     * @return the dataStore
     */
    public RESTDataStore getDataStore() {
        return dataStore;
    }

    /**
     * @param dataStore the dataStore to set
     */
    public void setDataStore(RESTDataStore dataStore) {
        this.dataStore = dataStore;
    }

}
