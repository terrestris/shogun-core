/**
 *
 */
package de.terrestris.shogun2.importer.transform;

import java.util.List;

import de.terrestris.shogun2.importer.transform.RESTTransform;

/**
 * terrestris GmbH & Co. KG
 *
 * @author ahenn
 * @date 01.04.2016
 * <p>
 * Importer transform task representing gdal_translate, a tool which converts
 * raster data between different formats
 * @see <a href="http://www.gdal.org/gdal_translate.html">GDAL (gdal_translate) documentation</a>
 * Requires <code>gdal_translate</code> to be inside the PATH used by the web container running GeoServer.
 */
public class RESTGdalTranslateTransform extends RESTTransform {

    /**
     *
     */
    public static final String TYPE_NAME = "GdalTranslateTransform";

    private List<String> options;

    /**
     * Default constructor; sets <code>type</code> of
     * {@link RESTTransform} to "GdalTranslateTransform"
     */
    public RESTGdalTranslateTransform() {
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
