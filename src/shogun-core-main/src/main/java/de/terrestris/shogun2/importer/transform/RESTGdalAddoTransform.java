package de.terrestris.shogun2.importer.transform;

import java.util.List;

/**
 * terrestris GmbH & Co. KG
 *
 * @author ahenn
 * @date 01.04.2016
 * <p>
 * Task performing gdaladdo which builds or rebuilds overview images.
 * @see <a href="http://www.gdal.org/gdaladdo.html">GDAL (gdaladdo) documentation</a>
 * Requires <code>gdaladdo</code> to be inside the PATH used by the web container running GeoServer.
 */
public class RESTGdalAddoTransform extends RESTTransform {

    public static final String TYPE_NAME = "GdalAddoTransform";

    private List<String> options;
    private List<Integer> levels;

    /**
     * Default constructor; sets <code>type</code> of
     * {@link RESTTransform} to "GdalAddoTransform"
     */
    public RESTGdalAddoTransform() {
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

    /**
     * @return the levels
     */
    public List<Integer> getLevels() {
        return levels;
    }

    /**
     * @param levels the levels to set
     */
    public void setLevels(List<Integer> levels) {
        this.levels = levels;
    }

}
