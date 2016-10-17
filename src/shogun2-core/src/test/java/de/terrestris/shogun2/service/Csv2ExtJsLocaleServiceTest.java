package de.terrestris.shogun2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Nils Bühner
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-locale-service.xml" })
public class Csv2ExtJsLocaleServiceTest {

	@Autowired
	protected Csv2ExtJsLocaleService csv2localeService;

	private static final String CSV_TEST = "test";

	private static final String CSV_NONEXISTING = "nonexistingcsv";

	private static final String CSV_EMPTY = "empty";

	private static final String CSV_INVALID = "invalid";

	private static final String CSV_COL_COMPONENT_EMPTY = "component_empty";

	private static final String CSV_COL_FIELD_EMPTY = "field_empty";

	private static final String CSV_COL_FIELD_UNSUPPORTED_ARRAY = "field_unsupported_array";

	private static final String LOCALE_DE = "de";

	private static final String LOCALE_FR = "fr";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void workAsExpected() throws Exception {

		Map<String, Object> resultMap = csv2localeService.getAllComponentsForLocale(CSV_TEST, LOCALE_DE);

		final Set<String> keySet = resultMap.keySet();

		assertEquals(3, keySet.size());

		final String someCmpKey = "MyProject.some.Component";
		final String anotherCmpKey = "MyProject.another.Component";
		final String yetAnotherCmpKey = "MyProject.yet.another.Component";

		assertTrue(resultMap.containsKey(someCmpKey));
		assertTrue(resultMap.containsKey(anotherCmpKey));
		assertTrue(resultMap.containsKey(yetAnotherCmpKey));

		// "MyProject.some.Component"
		Map<String, Object> someCmpVal = (Map<String, Object>) resultMap.get(someCmpKey);
		assertEquals(1, someCmpVal.keySet().size());

		Map<String, Object> someCmpValConfig = (Map<String, Object>) someCmpVal.get("config");
		assertEquals(1, someCmpValConfig.keySet().size());

		Map<String, Object> someCmpValConfigData = (Map<String, Object>) someCmpValConfig.get("data");
		assertEquals(3, someCmpValConfigData.keySet().size());

		assertEquals("de:title;and something after a semicolon", someCmpValConfigData.get("title"));
		assertEquals("de:tooltip", someCmpValConfigData.get("tooltip"));
		assertEquals("{0} Zeile(n) ausgewählt", someCmpValConfigData.get("dragText"));

		// "MyProject.another.Component"
		Map<String, Object> anotherCmpVal = (Map<String, Object>) resultMap.get(anotherCmpKey);
		assertEquals(2, anotherCmpVal.keySet().size());
		assertEquals("de:foo", anotherCmpVal.get("justonefield"));

		Map<String, Object> anotherCmpValData = (Map<String, Object>) anotherCmpVal.get("data");
		assertEquals(2, anotherCmpValData.keySet().size());
		assertEquals("de:data.a", anotherCmpValData.get("a"));
		assertEquals("de:data.b", anotherCmpValData.get("b"));

		// "MyProject.yet.another.Component"
		Map<String, Object> yetAnotherCmpVal = (Map<String, Object>) resultMap.get(yetAnotherCmpKey);
		assertEquals(1, yetAnotherCmpVal.keySet().size());

		Map<String, Object> yetAnotherCmpValConfig = (Map<String, Object>) yetAnotherCmpVal.get("config");
		assertEquals(1, yetAnotherCmpValConfig.keySet().size());

		String[] yetAnotherCmpValConfigArray = (String[]) yetAnotherCmpValConfig.get("myArray");
		assertEquals(4, yetAnotherCmpValConfigArray.length);

		assertEquals("de:v1", yetAnotherCmpValConfigArray[0]);
		assertEquals("v2", yetAnotherCmpValConfigArray[1]);
		assertEquals("v3", yetAnotherCmpValConfigArray[2]);
		assertEquals(" ...", yetAnotherCmpValConfigArray[3]);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void throwExceptionWhenCsvResourceDoesNotExist() throws Exception {
		expectedException.expect(Exception.class);
		expectedException.expectMessage("CSV locale resource for " + CSV_NONEXISTING + " does not exist.");

		csv2localeService.getAllComponentsForLocale(CSV_NONEXISTING, LOCALE_DE);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void throwExceptionWhenCsvResourceIsEmpty() throws Exception {
		expectedException.expect(Exception.class);
		expectedException.expectMessage("CSV locale file seems to be empty.");

		csv2localeService.getAllComponentsForLocale(CSV_EMPTY, LOCALE_DE);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void throwExceptionWhenCsvResourceIsInvalid() throws Exception {
		expectedException.expect(Exception.class);
		expectedException.expectMessage("CSV locale file is invalid: Not enough columns.");

		csv2localeService.getAllComponentsForLocale(CSV_INVALID, LOCALE_DE);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void throwExceptionWhenCsvResourceIsMissingRequestedLocale() throws Exception {
		expectedException.expect(Exception.class);
		expectedException.expectMessage("Could not find locale " + LOCALE_FR + " in CSV file");

		csv2localeService.getAllComponentsForLocale(CSV_TEST, LOCALE_FR);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void throwExceptionWhenCsvResourceIsMissingComponentEntry() throws Exception {
		expectedException.expect(Exception.class);
		expectedException.expectMessage("Missing component entry in CSV line " + 3);

		csv2localeService.getAllComponentsForLocale(CSV_COL_COMPONENT_EMPTY, LOCALE_DE);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void throwExceptionWhenCsvResourceIsMissingFieldEntry() throws Exception {
		expectedException.expect(Exception.class);
		expectedException.expectMessage("Missing field entry in CSV line " + 3);

		csv2localeService.getAllComponentsForLocale(CSV_COL_FIELD_EMPTY, LOCALE_DE);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void throwExceptionWhenCsvResourceContainsUnsupportedArrayUsage() throws Exception {
		final String field = "config[].myArray[]";
		expectedException.expect(Exception.class);
		expectedException.expectMessage("Invalid field description '" + field
				+ "': '[]' may only occure once at the end, but not before");

		csv2localeService.getAllComponentsForLocale(CSV_COL_FIELD_UNSUPPORTED_ARRAY, LOCALE_DE);
	}

}
