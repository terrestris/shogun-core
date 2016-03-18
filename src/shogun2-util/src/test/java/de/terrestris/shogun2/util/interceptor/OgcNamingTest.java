package de.terrestris.shogun2.util.interceptor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class OgcNamingTest {

	public static final String PARAMETER_SERVICE = "SERVICE";
	public static final String PARAMETER_OPERATION = "REQUEST";
	public static final String[] PARAMETER_ENDPOINT = {"LAYERS", "LAYER", "NAMESPACE"};

	public static final String SERVICE_WMS = "WMS";
	public static final String SERVICE_WFS = "WFS";
	public static final String SERVICE_WCS = "WCS";

	public static final String OPERATION_GET_MAP = "GetMap";
	public static final String OPERATION_GET_CAPABILITIES = "GetCapabilities";
	public static final String OPERATION_GET_FEATURE_INFO = "GetFeatureInfo";
	public static final String OPERATION_DESCRIBE_LAYER = "DescribeLayer";
	public static final String OPERATION_GET_LEGEND_GRAPHIC = "GetLegendGraphic";
	public static final String OPERATION_GET_STYLES = "GetStyles";
	public static final String OPERATION_DESCRIBE_FEATURE_TYPE = "DescribeFeatureType";
	public static final String OPERATION_GET_FEATURE = "GetFeature";
	public static final String OPERATION_LOCK_FEATURE = "LockFeature";
	public static final String OPERATION_TRANSACTION = "Transaction";
	public static final String OPERATION_DESCRIBE_COVERAGE = "DescribeCoverage";
	public static final String OPERATION_GET_COVERAGE = "GetCoverage";

	@Test
	public void can_be_instantiated() {
		OgcNaming ogcNaming = new OgcNaming();
		assertNotNull(ogcNaming);
	}

	@Test
	public void correct_ogc_values_present() {
		assertEquals(PARAMETER_SERVICE, OgcNaming.PARAMETER_SERVICE);
		assertEquals(PARAMETER_OPERATION, OgcNaming.PARAMETER_OPERATION);
		assertArrayEquals(PARAMETER_ENDPOINT, OgcNaming.PARAMETER_ENDPOINT);

		assertEquals(SERVICE_WMS, OgcNaming.SERVICE_WMS);
		assertEquals(SERVICE_WFS, OgcNaming.SERVICE_WFS);
		assertEquals(SERVICE_WCS, OgcNaming.SERVICE_WCS);

		assertEquals(OPERATION_GET_MAP, OgcNaming.OPERATION_GET_MAP);
		assertEquals(OPERATION_GET_CAPABILITIES, OgcNaming.OPERATION_GET_CAPABILITIES);
		assertEquals(OPERATION_GET_FEATURE_INFO, OgcNaming.OPERATION_GET_FEATURE_INFO);
		assertEquals(OPERATION_DESCRIBE_LAYER, OgcNaming.OPERATION_DESCRIBE_LAYER);
		assertEquals(OPERATION_GET_LEGEND_GRAPHIC, OgcNaming.OPERATION_GET_LEGEND_GRAPHIC);
		assertEquals(OPERATION_GET_STYLES, OgcNaming.OPERATION_GET_STYLES);
		assertEquals(OPERATION_DESCRIBE_FEATURE_TYPE, OgcNaming.OPERATION_DESCRIBE_FEATURE_TYPE);
		assertEquals(OPERATION_GET_FEATURE, OgcNaming.OPERATION_GET_FEATURE);
		assertEquals(OPERATION_LOCK_FEATURE, OgcNaming.OPERATION_LOCK_FEATURE);
		assertEquals(OPERATION_TRANSACTION, OgcNaming.OPERATION_TRANSACTION);
		assertEquals(OPERATION_DESCRIBE_COVERAGE, OgcNaming.OPERATION_DESCRIBE_COVERAGE);
		assertEquals(OPERATION_GET_COVERAGE, OgcNaming.OPERATION_GET_COVERAGE);
	}
}
