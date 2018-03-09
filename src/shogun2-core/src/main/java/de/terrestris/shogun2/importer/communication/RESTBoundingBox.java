package de.terrestris.shogun2.importer.communication;

import de.terrestris.shogun2.importer.communication.AbstractRESTEntity;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class RESTBoundingBox extends AbstractRESTEntity {

    /**
     *
     */
    private String minx;

    /**
     *
     */
    private String miny;

    /**
     *
     */
    private String maxx;

    /**
     *
     */
    private String maxy;

    /**
     *
     */
    private String crs;

    /**
     * Default constructor.
     */
    public RESTBoundingBox() {

    }

    /**
     * @param minx
     * @param miny
     * @param maxx
     * @param maxy
     * @param crs
     */
    public RESTBoundingBox(String minx, String miny, String maxx, String maxy,
                           String crs) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
        this.crs = crs;
    }

    /**
     * @return the minx
     */
    public String getMinx() {
        return minx;
    }

    /**
     * @param minx the minx to set
     */
    public void setMinx(String minx) {
        this.minx = minx;
    }

    /**
     * @return the miny
     */
    public String getMiny() {
        return miny;
    }

    /**
     * @param miny the miny to set
     */
    public void setMiny(String miny) {
        this.miny = miny;
    }

    /**
     * @return the maxx
     */
    public String getMaxx() {
        return maxx;
    }

    /**
     * @param maxx the maxx to set
     */
    public void setMaxx(String maxx) {
        this.maxx = maxx;
    }

    /**
     * @return the maxy
     */
    public String getMaxy() {
        return maxy;
    }

    /**
     * @param maxy the maxy to set
     */
    public void setMaxy(String maxy) {
        this.maxy = maxy;
    }

    /**
     * @return the crs
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }
}
