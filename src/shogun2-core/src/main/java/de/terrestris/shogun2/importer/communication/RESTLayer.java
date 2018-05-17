package de.terrestris.shogun2.importer.communication;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
@JsonRootName(value = "layer")
public class RESTLayer extends AbstractRESTEntity {

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String href;

    /**
     *
     */
    private String title;

    /**
     *
     */
    private String description;

    /**
     *
     */
    private String originalName;

    /**
     *
     */
    private String nativeName;

    /**
     *
     */
    private String srs;

    /**
     *
     */
    private RESTBoundingBox bbox;

    /**
     *
     */
    private List<RESTAttribute> attributes;

    /**
     *
     */
    private RESTStyle style;

    /**
     * Default constructor.
     */
    public RESTLayer() {

    }

    /**
     * @param name
     * @param href
     * @param title
     * @param originalName
     * @param nativeName
     * @param srs
     * @param bbox
     * @param attributes
     * @param style
     */
    public RESTLayer(String name, String href, String title, String description,
                     String originalName, String nativeName, String srs,
                     RESTBoundingBox bbox, List<RESTAttribute> attributes,
                     RESTStyle style) {
        this.name = name;
        this.href = href;
        this.title = title;
        this.description = description;
        this.originalName = originalName;
        this.nativeName = nativeName;
        this.srs = srs;
        this.bbox = bbox;
        this.attributes = attributes;
        this.style = style;
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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the originalName
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @param originalName the originalName to set
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    /**
     * @return the nativeName
     */
    public String getNativeName() {
        return nativeName;
    }

    /**
     * @param nativeName the nativeName to set
     */
    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    /**
     * @return the srs
     */
    public String getSrs() {
        return srs;
    }

    /**
     * @param srs the srs to set
     */
    public void setSrs(String srs) {
        this.srs = srs;
    }

    /**
     * @return the bbox
     */
    public RESTBoundingBox getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     */
    public void setBbox(RESTBoundingBox bbox) {
        this.bbox = bbox;
    }

    /**
     * @return the attributes
     */
    public List<RESTAttribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(List<RESTAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the style
     */
    public RESTStyle getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(RESTStyle style) {
        this.style = style;
    }

}
