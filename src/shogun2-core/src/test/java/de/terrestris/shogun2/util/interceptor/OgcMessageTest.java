package de.terrestris.shogun2.util.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.terrestris.shogun2.util.enumeration.InterceptorEnum;
import de.terrestris.shogun2.util.enumeration.OgcEnum;
/**
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
@SuppressWarnings("static-method")
public class OgcMessageTest {

	public static final String ENDPOINT = "Shinji:Kagawa";

	@Test
	public void can_be_instantiated() {

		OgcMessage message;

		message = new OgcMessage();

		assertNotNull(message);

		message = new OgcMessage(
				OgcEnum.ServiceType.WMS,
				OgcEnum.OperationType.GET_MAP,
				ENDPOINT,
				InterceptorEnum.RuleType.ALLOW,
				InterceptorEnum.RuleType.ALLOW
		);

		assertEquals(OgcEnum.ServiceType.WMS, message.getService());
		assertEquals(OgcEnum.OperationType.GET_MAP, message.getOperation());
		assertEquals(ENDPOINT, message.getEndPoint());
		assertEquals(InterceptorEnum.RuleType.ALLOW, message.getRequestRule());
		assertEquals(InterceptorEnum.RuleType.ALLOW, message.getResponseRule());
	}

	@Test
	public void is_wms() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS,
				null, null, null, null);
		assertEquals(true, message.isWms());
	}

	@Test
	public void is_wfs() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WFS,
				null, null, null, null);
		assertEquals(true, message.isWfs());
	}

	@Test
	public void is_wcs() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WCS,
				null, null, null, null);
		assertEquals(true, message.isWcs());
	}

	@Test
	public void is_wps() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WPS,
				null, null, null, null);
		assertEquals(true, message.isWps());
	}

	@Test
	public void is_w3ds() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.W3DS,
				null, null, null, null);
		assertEquals(true, message.isW3ds());
	}

	@Test
	public void is_wms_get_capabilities() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS,
				OgcEnum.OperationType.GET_CAPABILITIES, null, null, null);
		assertEquals(true, message.isWmsGetCapabilities());
	}

	@Test
	public void is_wms_get_map() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS,
				OgcEnum.OperationType.GET_MAP, null, null, null);
		assertEquals(true, message.isWmsGetMap());
	}

	@Test
	public void is_wms_get_feature_info() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS,
				OgcEnum.OperationType.GET_FEATURE_INFO, null, null, null);
		assertEquals(true, message.isWmsGetFeatureInfo());
	}

	@Test
	public void is_wms_describe_layer() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS,
				OgcEnum.OperationType.DESCRIBE_LAYER, null, null, null);
		assertEquals(true, message.isWmsDescribeLayer());
	}

	@Test
	public void is_wms_get_legend_graphic() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS,
				OgcEnum.OperationType.GET_LEGEND_GRAPHIC, null, null, null);
		assertEquals(true, message.isWmsGetLegendGraphic());
	}

	@Test
	public void is_wms_get_styles() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS,
				OgcEnum.OperationType.GET_STYLES, null, null, null);
		assertEquals(true, message.isWmsGetStyles());
	}

	@Test
	public void is_wfs_get_capabilities() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WFS,
				OgcEnum.OperationType.GET_CAPABILITIES, null, null, null);
		assertEquals(true, message.isWfsGetCapabilities());
	}

	@Test
	public void is_wfs_describe_feature_type() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WFS,
				OgcEnum.OperationType.DESCRIBE_FEATURE_TYPE, null, null, null);
		assertEquals(true, message.isWfsDescribeFeatureType());
	}

	@Test
	public void is_wfs_get_feature() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WFS,
				OgcEnum.OperationType.GET_FEATURE, null, null, null);
		assertEquals(true, message.isWfsGetFeature());
	}

	@Test
	public void is_wfs_lock_feature() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WFS,
				OgcEnum.OperationType.LOCK_FEATURE, null, null, null);
		assertEquals(true, message.isWfsLockFeature());
	}

	@Test
	public void is_wfs_transaction() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WFS,
				OgcEnum.OperationType.TRANSACTION, null, null, null);
		assertEquals(true, message.isWfsTransaction());
	}

	@Test
	public void is_wcs_get_capabilities() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WCS,
				OgcEnum.OperationType.GET_CAPABILITIES, null, null, null);
		assertEquals(true, message.isWcsGetCapabilities());
	}

	@Test
	public void is_wcs_describe_coverage() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WCS,
				OgcEnum.OperationType.DESCRIBE_COVERAGE, null, null, null);
		assertEquals(true, message.isWcsDescribeCoverage());
	}

	@Test
	public void is_wcs_get_coverage() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WCS,
				OgcEnum.OperationType.GET_COVERAGE, null, null, null);
		assertEquals(true, message.isWcsGetCoverage());
	}

	@Test
	public void is_wps_get_capabilities() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WPS,
				OgcEnum.OperationType.GET_CAPABILITIES, null, null, null);
		assertEquals(true, message.isWpsGetCapabilities());
	}

	@Test
	public void is_w3ds_get_capabilities() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.W3DS,
				OgcEnum.OperationType.GET_CAPABILITIES, null, null, null);
		assertEquals(true, message.isW3dsGetCapabilities());
	}

	@Test
	public void is_w3ds_get_scene() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.W3DS,
				OgcEnum.OperationType.GET_SCENE, null, null, null);
		assertEquals(true, message.isW3dsGetScene());
	}

	@Test
	public void is_w3ds_get_feature_info() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.W3DS,
				OgcEnum.OperationType.GET_FEATURE_INFO, null, null, null);
		assertEquals(true, message.isW3dsGetFeatureInfo());
	}

	@Test
	public void is_w3ds_get_layer_info() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.W3DS,
				OgcEnum.OperationType.GET_LAYER_INFO, null, null, null);
		assertEquals(true, message.isW3dsGetLayerInfo());
	}

	@Test
	public void is_w3ds_get_tile() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.W3DS,
				OgcEnum.OperationType.GET_TILE, null, null, null);
		assertEquals(true, message.isW3dsGetTile());
	}

	@Test
	public void is_wps_describe_process() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WPS,
				OgcEnum.OperationType.DESCRIBE_PROCESS, null, null, null);
		assertEquals(true, message.isWpsDescribeProcess());
	}

	@Test
	public void is_wps_execute() {
		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WPS,
				OgcEnum.OperationType.EXECUTE, null, null, null);
		assertEquals(true, message.isWpsExecute());
	}

	@Test
	public void is_request_allowed() {
		OgcMessage message = new OgcMessage(null, null, null,
				InterceptorEnum.RuleType.ALLOW, null);
		assertEquals(true, message.isRequestAllowed());
	}

	@Test
	public void is_response_allowed() {
		OgcMessage message = new OgcMessage(null, null, null, null,
				InterceptorEnum.RuleType.ALLOW);
		assertEquals(true, message.isResponseAllowed());
	}

	@Test
	public void is_request_denied() {
		OgcMessage message = new OgcMessage(null, null, null,
				InterceptorEnum.RuleType.DENY, null);
		assertEquals(true, message.isRequestDenied());
	}

	@Test
	public void is_response_denied() {
		OgcMessage message = new OgcMessage(null, null, null, null,
				InterceptorEnum.RuleType.DENY);
		assertEquals(true, message.isResponseDenied());
	}

	@Test
	public void is_request_modified() {
		OgcMessage message = new OgcMessage(null, null, null,
				InterceptorEnum.RuleType.MODIFY, null);
		assertEquals(true, message.isRequestModified());
	}

	@Test
	public void is_response_modified() {
		OgcMessage message = new OgcMessage(null, null, null, null,
				InterceptorEnum.RuleType.MODIFY);
		assertEquals(true, message.isResponseModified());
	}

}
