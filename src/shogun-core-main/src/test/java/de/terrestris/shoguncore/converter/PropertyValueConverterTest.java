package de.terrestris.shoguncore.converter;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Nils Bühner
 */
public class PropertyValueConverterTest {

    /**
     * The converter class to test.
     */
    private final PropertyValueConverter converter = new PropertyValueConverter();

    /**
     * Tests whether a string will remain a string.
     */
    @Test
    public void convertToEntityAttribute_convertsToString() {

        String testValue = "test";
        Object convertedValue = converter.convertToEntityAttribute(testValue);

        assertTrue(convertedValue instanceof String);
        assertTrue(testValue.equals(convertedValue));
    }

    /**
     * Tests whether an integer "string" will be converted to a Long value.
     */
    @Test
    public void convertToEntityAttribute_convertsToLong() {

        String testValue = "42";
        String negativeTestValue = "-42";

        Object convertedValue = converter.convertToEntityAttribute(testValue);
        Object convertedNegativeValue = converter.convertToEntityAttribute(negativeTestValue);

        assertTrue(convertedValue instanceof Long);
        assertTrue(((Long) convertedValue) == 42);

        assertTrue(convertedNegativeValue instanceof Long);
        assertTrue(((Long) convertedNegativeValue) == -42);
    }

    /**
     * Tests whether an float/double "string" will be converted to a Double value.
     */
    @Test
    public void convertToEntityAttribute_convertsToDouble() {
        String testValue = "17.42";
        String negativeTestValue = "-17.42";

        Object convertedValue = converter.convertToEntityAttribute(testValue);
        Object convertedNegativeValue = converter.convertToEntityAttribute(negativeTestValue);

        assertTrue(convertedValue instanceof Double);
        double val = Double.valueOf((Double) convertedValue);
        assertTrue(Math.abs(17.42 - val) <= 1E-8);
        assertTrue(convertedNegativeValue instanceof Double);
        val = Double.valueOf((Double) convertedNegativeValue);
        assertTrue(Math.abs(17.42 + val) <= 1E-8);
    }

    /**
     * Tests whether an boolean "string" will be converted to a Boolean value.
     */
    @Test
    public void convertToEntityAttribute_convertsToBoolean() {

        String testValue = "true";
        String testValueCaseInsensitive = "faLSe";

        Object convertedValue = converter.convertToEntityAttribute(testValue);
        Object convertedValueCaseInsensitive = converter.convertToEntityAttribute(testValueCaseInsensitive);

        assertTrue(convertedValue instanceof Boolean);
        assertTrue(((Boolean) convertedValue) == true);

        assertTrue(convertedValueCaseInsensitive instanceof Boolean);
        assertTrue(((Boolean) convertedValueCaseInsensitive) == false);
    }

    /**
     * Tests whether a string will be converted to the correct string.
     */
    @Test
    public void convertToDatabaseColumn_convertsStringToString() {

        String testValue = "test";
        String convertedValue = converter.convertToDatabaseColumn(testValue);

        assertTrue(testValue.equals(convertedValue));
    }

    /**
     * Tests whether an integer will be converted to the correct string.
     */
    @Test
    public void convertToDatabaseColumn_convertsIntegerToString() {

        int testValue = 42;
        int negativeTestValue = -42;

        String convertedValue = converter.convertToDatabaseColumn(testValue);
        String convertedNegativeValue = converter.convertToDatabaseColumn(negativeTestValue);

        assertTrue("42".equals(convertedValue));
        assertTrue("-42".equals(convertedNegativeValue));
    }

    /**
     * Tests whether a double will be converted to the correct string.
     */
    @Test
    public void convertToDatabaseColumn_convertsDoubleToString() {

        double testValue = 17.42;
        double negativeTestValue = -17.42;

        String convertedValue = converter.convertToDatabaseColumn(testValue);
        String convertedNegativeValue = converter.convertToDatabaseColumn(negativeTestValue);

        assertTrue("17.42".equals(convertedValue));
        assertTrue("-17.42".equals(convertedNegativeValue));
    }

    /**
     * Tests whether a boolean will be converted to the correct string.
     */
    @Test
    public void convertToDatabaseColumn_convertsBooleanToString() {

        boolean testValue = true;
        String convertedValue = converter.convertToDatabaseColumn(testValue);

        assertTrue("true".equals(convertedValue));
    }

}
