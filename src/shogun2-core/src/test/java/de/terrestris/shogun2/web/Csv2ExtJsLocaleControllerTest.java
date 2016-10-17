package de.terrestris.shogun2.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.terrestris.shogun2.service.Csv2ExtJsLocaleService;

/**
 * @author Nils Bühner
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-locale-controller.xml" })
public class Csv2ExtJsLocaleControllerTest {

	/**
	 *
	 */
	private MockMvc mockMvc;

	/**
	 *
	 */
	@Autowired
	private Csv2ExtJsLocaleService csv2localeService;

	/**
	 * The controller to test
	 */
	private Csv2ExtJsLocaleController csv2localeController = new Csv2ExtJsLocaleController();

	private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

	private static final String CONTROLLER_ERROR_PREFIX = "Could not generate an EXT JS locale JSON from a CSV: ";

	private static final String CSV_TEST = "test";

	private static final String CSV_NONEXISTING = "nonexistingcsv";

	private static final String CSV_EMPTY = "empty";

	private static final String CSV_INVALID = "invalid";

	private static final String CSV_COL_COMPONENT_EMPTY = "component_empty";

	private static final String CSV_COL_FIELD_EMPTY = "field_empty";

	private static final String CSV_COL_FIELD_UNSUPPORTED_ARRAY = "field_unsupported_array";

	private static final String LOCALE_DE = "de";

	private static final String LOCALE_FR = "fr";

	@Before
	public void setUp() {

		csv2localeController.setService(csv2localeService);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(csv2localeController).build();
	}

	@Test
	public void worksAsExpected() throws Exception {

		// Perform and test the GET-Request
		mockMvc.perform(get("/locale/" + CSV_TEST + "/" + LOCALE_DE + ".json"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.*", hasSize(3)))
				.andExpect(jsonPath("$['MyProject.another.Component'].*", hasSize(2)))
				.andExpect(jsonPath("$['MyProject.another.Component'].justonefield", is("de:foo")))
				.andExpect(jsonPath("$['MyProject.another.Component'].data.*", hasSize(2)))
				.andExpect(jsonPath("$['MyProject.another.Component'].data.a", is("de:data.a")))
				.andExpect(jsonPath("$['MyProject.another.Component'].data.b", is("de:data.b")))
				.andExpect(jsonPath("$['MyProject.some.Component'].*", hasSize(1)))
				.andExpect(jsonPath("$['MyProject.some.Component'].config.*", hasSize(1)))
				.andExpect(jsonPath("$['MyProject.some.Component'].config.data.*", hasSize(3)))
				.andExpect(jsonPath("$['MyProject.some.Component'].config.data.title", is("de:title;and something after a semicolon")))
				.andExpect(jsonPath("$['MyProject.some.Component'].config.data.tooltip", is("de:tooltip")))
				.andExpect(jsonPath("$['MyProject.some.Component'].config.data.dragText", is("{0} Zeile(n) ausgewählt")))
				.andExpect(jsonPath("$['MyProject.yet.another.Component'].*", hasSize(1)))
				.andExpect(jsonPath("$['MyProject.yet.another.Component'].config.*", hasSize(1)))
				.andExpect(jsonPath("$['MyProject.yet.another.Component'].config.myArray", hasSize(4)))
				.andExpect(jsonPath("$['MyProject.yet.another.Component'].config.myArray[0]", is("de:v1")))
				.andExpect(jsonPath("$['MyProject.yet.another.Component'].config.myArray[1]", is("v2")))
				.andExpect(jsonPath("$['MyProject.yet.another.Component'].config.myArray[2]", is("v3")))
				.andExpect(jsonPath("$['MyProject.yet.another.Component'].config.myArray[3]", is(" ...")));
	}

	@Test
	public void getErrorMsgWhenCsvResourceDoesNotExist() throws Exception {

		// Perform and test the GET-Request
		mockMvc.perform(get("/locale/" + CSV_NONEXISTING + "/" + LOCALE_DE + ".json"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.message", is(
						CONTROLLER_ERROR_PREFIX + "CSV locale resource for " + CSV_NONEXISTING + " does not exist.")));
	}

	@Test
	public void getErrorMsgWhenCsvResourceIsEmpty() throws Exception {

		// Perform and test the GET-Request
		mockMvc.perform(get("/locale/" + CSV_EMPTY + "/" + LOCALE_DE + ".json"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.message", is(
						CONTROLLER_ERROR_PREFIX + "CSV locale file seems to be empty.")));
	}

	@Test
	public void getErrorMsgWhenCsvResourceIsInvalid() throws Exception {

		// Perform and test the GET-Request
		mockMvc.perform(get("/locale/" + CSV_INVALID + "/" + LOCALE_DE + ".json"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.message", is(
						CONTROLLER_ERROR_PREFIX + "CSV locale file is invalid: Not enough columns.")));
	}

	@Test
	public void getErrorMsgWhenCsvResourceIsMissingRequestedLocale() throws Exception {

		// Perform and test the GET-Request
		mockMvc.perform(get("/locale/" + CSV_TEST + "/" + LOCALE_FR + ".json"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.message", is(
						CONTROLLER_ERROR_PREFIX + "Could not find locale " + LOCALE_FR + " in CSV file")));
	}

	@Test
	public void getErrorMsgWhenCsvResourceIsMissingComponentEntry() throws Exception {

		// Perform and test the GET-Request
		mockMvc.perform(get("/locale/" + CSV_COL_COMPONENT_EMPTY + "/" + LOCALE_DE + ".json"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.message", is(
						CONTROLLER_ERROR_PREFIX + "Missing component entry in CSV line " + 3)));
	}

	@Test
	public void getErrorMsgWhenCsvResourceIsMissingFieldEntry() throws Exception {

		// Perform and test the GET-Request
		mockMvc.perform(get("/locale/" + CSV_COL_FIELD_EMPTY + "/" + LOCALE_DE + ".json"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.message", is(
						CONTROLLER_ERROR_PREFIX + "Missing field entry in CSV line " + 3)));
	}

	@Test
	public void getErrorMsgWhenCsvResourceContainsUnsupportedArrayUsage() throws Exception {

		final String field = "config[].myArray[]";

		// Perform and test the GET-Request
		mockMvc.perform(get("/locale/" + CSV_COL_FIELD_UNSUPPORTED_ARRAY + "/" + LOCALE_DE + ".json"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.message", is(CONTROLLER_ERROR_PREFIX + "Invalid field description '" + field
						+ "': '[]' may only occure once at the end, but not before")));
	}
}
