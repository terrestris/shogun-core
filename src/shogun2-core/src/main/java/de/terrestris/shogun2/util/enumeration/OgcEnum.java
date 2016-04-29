package de.terrestris.shogun2.util.enumeration;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class OgcEnum {

	/**
	 * A enum type for the allowed service format.
	 */
	public static enum Service {
		SERVICE("SERVICE");

		private final String value;

		/**
		 * Enum constructor
		 *
		 * @param value
		 */
		private Service(String value) {
			this.value = value;
		}

		/**
		 * Static method to get an enum based on a string value.
		 * This method is annotated with {@link JsonCreator},
		 * which allows the client to send case insensitive string
		 * values (like "jSon"), which will be converted to the
		 * correct enum value.
		 *
		 * @param inputValue
		 * @return
		 */
		@JsonCreator
		public static Service fromString(String inputValue) {
			if (inputValue != null) {
				for (Service type : Service.values()) {
					if (inputValue.equalsIgnoreCase(type.value)) {
						return type;
					}
				}
			}
			return null;
		}

		/**
		 * This method is annotated with {@link JsonValue},
		 * so that jackson will serialize the enum value to
		 * the (lowercase) {@link #value}.
		 */
		@Override
		@JsonValue
		public String toString() {
			return value;
		}
	}

	/**
	 * A enum type for the allowed operation format.
	 */
	public static enum Operation {
		OPERATION("REQUEST");

		private final String value;

		/**
		 * Enum constructor
		 *
		 * @param value
		 */
		private Operation(String value) {
			this.value = value;
		}

		/**
		 * Static method to get an enum based on a string value.
		 * This method is annotated with {@link JsonCreator},
		 * which allows the client to send case insensitive string
		 * values (like "jSon"), which will be converted to the
		 * correct enum value.
		 *
		 * @param inputValue
		 * @return
		 */
		@JsonCreator
		public static OperationType fromString(String inputValue) {
			if (inputValue != null) {
				for (OperationType type : OperationType.values()) {
					if (inputValue.equalsIgnoreCase(type.value)) {
						return type;
					}
				}
			}
			return null;
		}

		/**
		 * This method is annotated with {@link JsonValue},
		 * so that jackson will serialize the enum value to
		 * the (lowercase) {@link #value}.
		 */
		@Override
		@JsonValue
		public String toString() {
			return value;
		}
	}

	/**
	 * A enum type for the allowed endPoint format.
	 */
	public static enum EndPoint {
		LAYERS("LAYERS"),
		LAYER("LAYER"),
		NAMESPACE("NAMESPACE");

		private final String value;

		/**
		 * Enum constructor
		 *
		 * @param value
		 */
		private EndPoint(String value) {
			this.value = value;
		}

		/**
		 * Static method to get an enum based on a string value.
		 * This method is annotated with {@link JsonCreator},
		 * which allows the client to send case insensitive string
		 * values (like "jSon"), which will be converted to the
		 * correct enum value.
		 *
		 * @param inputValue
		 * @return
		 */
		@JsonCreator
		public static EndPoint fromString(String inputValue) {
			if (inputValue != null) {
				for (EndPoint type : EndPoint.values()) {
					if (inputValue.equalsIgnoreCase(type.value)) {
						return type;
					}
				}
			}
			return null;
		}

		/**
		 * This method is annotated with {@link JsonValue},
		 * so that jackson will serialize the enum value to
		 * the (lowercase) {@link #value}.
		 */
		@Override
		@JsonValue
		public String toString() {
			return value;
		}

		/**
		 * Returns all enum values as string array.
		 *
		 * @return
		 */
		public static String[] getAllValues() {
			EndPoint[] endPoints = values();
			String[] values = new String[endPoints.length];

			for (int i = 0; i < endPoints.length; i++) {
				values[i] = endPoints[i].value;
			}

			return values;
		}
	}

	/**
	 * A enum type for the allowed service type format.
	 */
	public static enum ServiceType {
		WMS("WMS"),
		WFS("WFS"),
		WCS("WCS");

		private final String value;

		/**
		 * Enum constructor
		 *
		 * @param value
		 */
		private ServiceType(String value) {
			this.value = value;
		}

		/**
		 * Static method to get an enum based on a string value.
		 * This method is annotated with {@link JsonCreator},
		 * which allows the client to send case insensitive string
		 * values (like "jSon"), which will be converted to the
		 * correct enum value.
		 *
		 * @param inputValue
		 * @return
		 */
		@JsonCreator
		public static ServiceType fromString(String inputValue) {
			if (inputValue != null) {
				for (ServiceType type : ServiceType.values()) {
					if (inputValue.equalsIgnoreCase(type.value)) {
						return type;
					}
				}
			}
			return null;
		}

		/**
		 * This method is annotated with {@link JsonValue},
		 * so that jackson will serialize the enum value to
		 * the (lowercase) {@link #value}.
		 */
		@Override
		@JsonValue
		public String toString() {
			return value;
		}
	}

	/**
	 * A enum type for the allowed operation type format.
	 */
	public static enum OperationType {
		GET_MAP("GetMap"),
		GET_CAPABILITIES("GetCapabilities"),
		GET_FEATURE_INFO("GetFeatureInfo"),
		DESCRIBE_LAYER("DescribeLayer"),
		GET_LEGEND_GRAPHIC("GetLegendGraphic"),
		GET_STYLES("GetStyles"),
		DESCRIBE_FEATURE_TYPE("DescribeFeatureType"),
		GET_FEATURE("GetFeature"),
		LOCK_FEATURE("LockFeature"),
		TRANSACTION("Transaction"),
		DESCRIBE_COVERAGE("DescribeCoverage"),
		GET_COVERAGE("GetCoverage");

		private final String value;

		/**
		 * Enum constructor
		 *
		 * @param value
		 */
		private OperationType(String value) {
			this.value = value;
		}

		/**
		 * Static method to get an enum based on a string value.
		 * This method is annotated with {@link JsonCreator},
		 * which allows the client to send case insensitive string
		 * values (like "jSon"), which will be converted to the
		 * correct enum value.
		 *
		 * @param inputValue
		 * @return
		 */
		@JsonCreator
		public static OperationType fromString(String inputValue) {
			if (inputValue != null) {
				for (OperationType type : OperationType.values()) {
					if (inputValue.equalsIgnoreCase(type.value)) {
						return type;
					}
				}
			}
			return null;
		}

		/**
		 * This method is annotated with {@link JsonValue},
		 * so that jackson will serialize the enum value to
		 * the (lowercase) {@link #value}.
		 */
		@Override
		@JsonValue
		public String toString() {
			return value;
		}
	}

	/**
	 * A map that contains a set of {@link OperationType}s for any possible
	 * {@link ServiceType}. See also the opposite collection
	 * {@link SERVICETYPES_BY_OPERATION}.
	 */
	public static Map<ServiceType, Set<OperationType>> OPERATIONS_BY_SERVICETYPE;
	static {
		Map<ServiceType, Set<OperationType>> map = new HashMap<ServiceType, Set<OperationType>>();

		Set<OperationType> wmsOps = new HashSet<OperationType>();
		wmsOps.add(OperationType.GET_CAPABILITIES);
		wmsOps.add(OperationType.GET_MAP);
		wmsOps.add(OperationType.GET_FEATURE_INFO);
		wmsOps.add(OperationType.DESCRIBE_LAYER);
		wmsOps.add(OperationType.GET_LEGEND_GRAPHIC);
		wmsOps.add(OperationType.GET_STYLES);

		Set<OperationType> wfsOps = new HashSet<OperationType>();
		wfsOps.add(OperationType.GET_CAPABILITIES);
		wfsOps.add(OperationType.DESCRIBE_FEATURE_TYPE);
		wfsOps.add(OperationType.GET_FEATURE);
		wfsOps.add(OperationType.LOCK_FEATURE);
		wfsOps.add(OperationType.TRANSACTION);

		Set<OperationType> wcsOps = new HashSet<OperationType>();
		wcsOps.add(OperationType.GET_CAPABILITIES);
		wcsOps.add(OperationType.DESCRIBE_COVERAGE);
		wcsOps.add(OperationType.GET_COVERAGE);

		map.put(ServiceType.WMS, Collections.unmodifiableSet(wmsOps));
		map.put(ServiceType.WFS, Collections.unmodifiableSet(wfsOps));
		map.put(ServiceType.WCS, Collections.unmodifiableSet(wcsOps));

		// store it in the lookup
		OPERATIONS_BY_SERVICETYPE = Collections.unmodifiableMap(map);
	}

	/**
	 * A map that contains a set of {@link ServiceType}s for any possible
	 * {@link OperationType}. See also the opposite collection
	 * {@link OPERATIONS_BY_SERVICETYPE}.
	 */
	public static Map<OperationType, Set<ServiceType>> SERVICETYPES_BY_OPERATION;
	static {
		Map<OperationType, Set<ServiceType>> map = new HashMap<OperationType, Set<ServiceType>>();

		// A set containing only the WMS ServiceType
		Set<ServiceType> wmsSet = new HashSet<ServiceType>();
		wmsSet.add(ServiceType.WMS);
		wmsSet = Collections.unmodifiableSet(wmsSet);

		// A set containing only the WFS ServiceType
		Set<ServiceType> wfsSet = new HashSet<ServiceType>();
		wfsSet.add(ServiceType.WFS);
		wfsSet = Collections.unmodifiableSet(wfsSet);

		// A set containing only the WCS ServiceType
		Set<ServiceType> wcsSet = new HashSet<ServiceType>();
		wcsSet.add(ServiceType.WCS);
		wcsSet = Collections.unmodifiableSet(wcsSet);

		// A set containing the WMS, WFS and WCS ServiceTypes
		Set<ServiceType> getCapSet = new HashSet<ServiceType>();
		getCapSet.add(ServiceType.WMS);
		getCapSet.add(ServiceType.WFS);
		getCapSet.add(ServiceType.WCS);
		getCapSet = Collections.unmodifiableSet(getCapSet);

		// look up all WMS operations from the previously created map
		Set<OperationType> wmsOperations = OPERATIONS_BY_SERVICETYPE.get(ServiceType.WMS);
		// look up all WFS operations from the previously created map
		Set<OperationType> wfsOperations = OPERATIONS_BY_SERVICETYPE.get(ServiceType.WFS);
		// look up all WCS operations from the previously created map
		Set<OperationType> wcsOperations = OPERATIONS_BY_SERVICETYPE.get(ServiceType.WCS);

		// put all ServiceTypes for the GetCapability operation
		map.put(OperationType.GET_CAPABILITIES, getCapSet);
		// for WMS operations, put the WMS set, unless it's the GetCapability op
		for(OperationType wmsOperation : wmsOperations) {
			if (!OperationType.GET_CAPABILITIES.equals(wmsOperation)) {
				map.put(wmsOperation, wmsSet);
			}
		}
		// for WFS operations, put the WFS set, unless it's the GetCapability op
		for(OperationType wfsOperation : wfsOperations) {
			if (!OperationType.GET_CAPABILITIES.equals(wfsOperation)) {
				map.put(wfsOperation, wfsSet);
			}
		}
		// for WCS operations, put the WCS set, unless it's the GetCapability op
		for(OperationType wcsOperation : wcsOperations) {
			if (!OperationType.GET_CAPABILITIES.equals(wcsOperation)) {
				map.put(wcsOperation, wcsSet);
			}
		}
		// store it in the lookup
		SERVICETYPES_BY_OPERATION = Collections.unmodifiableMap(map);
	}

}
