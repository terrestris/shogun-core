package de.terrestris.shogun2.importer.transform;

import java.util.List;

/**
 * terrestris GmbH & Co. KG
 *
 * @author ahenn
 * @date 01.04.2016
 * <p>
 * Importer transform task representing gdalwarp, an image reprojection and warping utility,
 * @see <a href="http://www.gdal.org/gdalwarp.html">GDAL (gdalwarp) documentation</a>
 * Requires <code>gdalwarp</code> to be inside the PATH used by the web container running GeoServer.
 */
public class RESTGdalWarpTransform extends RESTTransform {

    /**
     *
     */
    public static final String TYPE_NAME = "GdalWarpTransform";

    private List<String> options;

    /**
     * Default constructor; sets <code>type</code> of
     * {@link RESTTransform} to "GdalWarpTransform"
     */
    public RESTGdalWarpTransform() {
        super(TYPE_NAME);
    }

    /**
     * @return the options
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions(List<String> options) {
        this.options = options;
    }

}
