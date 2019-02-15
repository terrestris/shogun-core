package de.terrestris.shoguncore.importer.transform;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
//@JsonRootName
public class RESTReprojectTransform extends RESTTransform {

    /**
     * constant field for transform type name
     */
    public static final String TYPE_NAME = "ReprojectTransform";

    /**
     *
     */
    private String href;

    /**
     *
     */
    private String source;

    /**
     *
     */
    private String target;

    /**
     * Default constructor; sets <code>type</code> of
     * {@link RESTTransform} to "ReprojectTransform"
     */
    public RESTReprojectTransform() {
        super(TYPE_NAME);
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

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

}
