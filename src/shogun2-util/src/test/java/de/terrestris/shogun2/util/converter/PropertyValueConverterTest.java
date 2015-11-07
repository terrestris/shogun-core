package de.terrestris.shogun2.util.converter;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Nils Bühner
 *
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

		assertTrue(testValue.equals(convertedValue));
	}

	/**
	 * Tests whether an integer "string" will be converted to a Long value.
	 */
	@Test
	public void convertToEntityAttribute_convertsToLong() {

		String testValue = "42";
		Object convertedValue = converter.convertToEntityAttribute(testValue);

		assertTrue(convertedValue instanceof Long);
		assertTrue(((Long)convertedValue) == 42);
	}

	/**
	 * Tests whether an float/double "string" will be converted to a Double value.
	 */
	@Test
	public void convertToEntityAttribute_convertsToDouble() {

		String testValue = "17.42";
		Object convertedValue = converter.convertToEntityAttribute(testValue);

		assertTrue(convertedValue instanceof Double);
		assertTrue(((Double)convertedValue) == 17.42);
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
		assertTrue(((Boolean)convertedValue) == true);

		assertTrue(convertedValueCaseInsensitive instanceof Boolean);
		assertTrue(((Boolean)convertedValueCaseInsensitive) == false);
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
		String convertedValue = converter.convertToDatabaseColumn(testValue);

		assertTrue("42".equals(convertedValue));
	}

	/**
	 * Tests whether a double will be converted to the correct string.
	 */
	@Test
	public void convertToDatabaseColumn_convertsDoubleToString() {

		double testValue = 17.42;
		String convertedValue = converter.convertToDatabaseColumn(testValue);

		assertTrue("17.42".equals(convertedValue));
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
