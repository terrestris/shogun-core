package de.terrestris.shoguncore.importer.transform;

import java.util.List;

import de.terrestris.shoguncore.importer.communication.AbstractRESTEntity;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTTransformChain extends AbstractRESTEntity {

    /**
     *
     */
    private String type;

    /**
     *
     */
    private List<RESTTransform> transforms;

    /**
     * Default constructor.
     */
    public RESTTransformChain() {

    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the transforms
     */
    public List<RESTTransform> getTransforms() {
        return transforms;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param transforms the transforms to set
     */
    public void setTransforms(List<RESTTransform> transforms) {
        this.transforms = transforms;
    }

}
