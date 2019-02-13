package de.terrestris.shogun2.importer.communication;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTAttribute extends AbstractRESTEntity {

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String binding;

    /**
     * Default constructor.
     */
    public RESTAttribute() {

    }

    /**
     * @param name
     * @param binding
     */
    public RESTAttribute(String name, String binding) {
        this.name = name;
        this.binding = binding;
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
     * @return the binding
     */
    public String getBinding() {
        return binding;
    }

    /**
     * @param binding the binding to set
     */
    public void setBinding(String binding) {
        this.binding = binding;
    }

}
