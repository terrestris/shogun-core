package de.terrestris.shogun2.util.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class OgcMessageTest {

	public static final String ENDPOINT = "Shinji:Kagawa";

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

	public static final String RULE_ALLOW = "ALLOW";
	public static final String RULE_DENY = "DENY";
	public static final String RULE_MODIFY = "MODIFY";

	@Test
	public void can_be_instantiated() {

		OgcMessage message;

		message = new OgcMessage();

		assertNotNull(message);

		message = new OgcMessage(SERVICE_WMS, OPERATION_GET_MAP, ENDPOINT, RULE_ALLOW, RULE_ALLOW);

		assertEquals(SERVICE_WMS, message.getService());
		assertEquals(OPERATION_GET_MAP, message.getOperation());
		assertEquals(ENDPOINT, message.getEndPoint());
		assertEquals(RULE_ALLOW, message.getRequestRule());
		assertEquals(RULE_ALLOW, message.getResponseRule());
	}

	@Test
	public void is_wms() {
		OgcMessage message = new OgcMessage(SERVICE_WMS, null, null, null, null);
		assertEquals(true, message.isWms());
	}

	@Test
	public void is_wfs() {
		OgcMessage message = new OgcMessage(SERVICE_WFS, null, null, null, null);
		assertEquals(true, message.isWfs());
	}

	@Test
	public void is_wcs() {
		OgcMessage message = new OgcMessage(SERVICE_WCS, null, null, null, null);
		assertEquals(true, message.isWcs());
	}

	@Test
	public void is_wms_get_capabilities() {
		OgcMessage message = new OgcMessage(SERVICE_WMS, OPERATION_GET_CAPABILITIES, null, null, null);
		assertEquals(true, message.isWmsGetCapabilities());
	}

	@Test
	public void is_wms_get_map() {
		OgcMessage message = new OgcMessage(SERVICE_WMS, OPERATION_GET_MAP, null, null, null);
		assertEquals(true, message.isWmsGetMap());
	}

	@Test
	public void is_wms_get_feature_info() {
		OgcMessage message = new OgcMessage(SERVICE_WMS, OPERATION_GET_FEATURE_INFO, null, null, null);
		assertEquals(true, message.isWmsGetFeatureInfo());
	}

	@Test
	public void is_wms_describe_layer() {
		OgcMessage message = new OgcMessage(SERVICE_WMS, OPERATION_DESCRIBE_LAYER, null, null, null);
		assertEquals(true, message.isWmsDescribeLayer());
	}

	@Test
	public void is_wms_get_legend_graphic() {
		OgcMessage message = new OgcMessage(SERVICE_WMS, OPERATION_GET_LEGEND_GRAPHIC, null, null, null);
		assertEquals(true, message.isWmsGetLegendGraphic());
	}

	@Test
	public void is_wms_get_styles() {
		OgcMessage message = new OgcMessage(SERVICE_WMS, OPERATION_GET_STYLES, null, null, null);
		assertEquals(true, message.isWmsGetStyles());
	}

	@Test
	public void is_wfs_get_capabilities() {
		OgcMessage message = new OgcMessage(SERVICE_WFS, OPERATION_GET_CAPABILITIES, null, null, null);
		assertEquals(true, message.isWfsGetCapabilities());
	}

	@Test
	public void is_wfs_describe_feature_type() {
		OgcMessage message = new OgcMessage(SERVICE_WFS, OPERATION_DESCRIBE_FEATURE_TYPE, null, null, null);
		assertEquals(true, message.isWfsDescribeFeatureType());
	}

	@Test
	public void is_wfs_get_feature() {
		OgcMessage message = new OgcMessage(SERVICE_WFS, OPERATION_GET_FEATURE, null, null, null);
		assertEquals(true, message.isWfsGetFeature());
	}

	@Test
	public void is_wfs_lock_feature() {
		OgcMessage message = new OgcMessage(SERVICE_WFS, OPERATION_LOCK_FEATURE, null, null, null);
		assertEquals(true, message.isWfsLockFeature());
	}

	@Test
	public void is_wfs_transaction() {
		OgcMessage message = new OgcMessage(SERVICE_WFS, OPERATION_TRANSACTION, null, null, null);
		assertEquals(true, message.isWfsTransaction());
	}

	@Test
	public void is_wcs_get_capabilities() {
		OgcMessage message = new OgcMessage(SERVICE_WCS, OPERATION_GET_CAPABILITIES, null, null, null);
		assertEquals(true, message.isWcsGetCapabilities());
	}

	@Test
	public void is_wcs_describe_coverage() {
		OgcMessage message = new OgcMessage(SERVICE_WCS, OPERATION_DESCRIBE_COVERAGE, null, null, null);
		assertEquals(true, message.isWcsDescribeCoverage());
	}

	@Test
	public void is_wcs_get_coverage() {
		OgcMessage message = new OgcMessage(SERVICE_WCS, OPERATION_GET_COVERAGE, null, null, null);
		assertEquals(true, message.isWcsGetCoverage());
	}

	@Test
	public void is_request_allowed() {
		OgcMessage message = new OgcMessage(null, null, null, RULE_ALLOW, null);
		assertEquals(true, message.isRequestAllowed());
	}

	@Test
	public void is_response_allowed() {
		OgcMessage message = new OgcMessage(null, null, null, null, RULE_ALLOW);
		assertEquals(true, message.isResponseAllowed());
	}

	@Test
	public void is_request_denied() {
		OgcMessage message = new OgcMessage(null, null, null, RULE_DENY, null);
		assertEquals(true, message.isRequestDenied());
	}

	@Test
	public void is_response_denied() {
		OgcMessage message = new OgcMessage(null, null, null, null, RULE_DENY);
		assertEquals(true, message.isResponseDenied());
	}

	@Test
	public void is_request_modified() {
		OgcMessage message = new OgcMessage(null, null, null, RULE_MODIFY, null);
		assertEquals(true, message.isRequestModified());
	}

	@Test
	public void is_response_modified() {
		OgcMessage message = new OgcMessage(null, null, null, null, RULE_MODIFY);
		assertEquals(true, message.isResponseModified());
	}

}
