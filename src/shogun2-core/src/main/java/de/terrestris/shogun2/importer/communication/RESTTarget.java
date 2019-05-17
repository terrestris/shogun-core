package de.terrestris.shogun2.importer.communication;

import de.terrestris.shogun2.importer.communication.AbstractRESTEntity;
import de.terrestris.shogun2.importer.communication.RESTDataStore;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTTarget extends AbstractRESTEntity {

    /**
     *
     */
    private String href;

    /**
     *
     */
    private RESTDataStore dataStore;

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
