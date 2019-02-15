package de.terrestris.shoguncore.util.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class InterceptorEnum {

    /**
     * A enum type for the allowed rule format.
     */
    public static enum RuleType {
        ALLOW("ALLOW"),
        DENY("DENY"),
        MODIFY("MODIFY");

        private final String value;

        /**
         * Enum constructor
         *
         * @param value
         */
        private RuleType(String value) {
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
        public static RuleType fromString(String inputValue) {
            if (inputValue != null) {
                for (RuleType type : RuleType.values()) {
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
