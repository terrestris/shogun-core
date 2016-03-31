package de.terrestris.shogun2.util.enumeration;

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

}
