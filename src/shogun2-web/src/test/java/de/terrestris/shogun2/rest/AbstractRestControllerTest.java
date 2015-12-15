package de.terrestris.shogun2.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.service.AbstractCrudService;
import de.terrestris.shogun2.util.test.TestUtil;

/**
 *
 * @author Kai Volland
 * @author Nils Bühner
 *
 */
public class AbstractRestControllerTest {

	/**
	 * A private model class for that exists only to test the
	 * {@link AbstractRestController}.
	 *
	 */
	private class TestModel extends PersistentObject {
		private static final long serialVersionUID = 1L;
		private String testValue;
		@SuppressWarnings("unused")
		public String getTestValue() {return testValue;}
		public void setTestValue(String testValue) {this.testValue = testValue;}
	}

	/**
	 * A private REST controller for the {@link TestModel}. This class
	 * only exists in the scope of this test and is used to test the
	 * {@link AbstractRestController}.
	 */
	@RestController
	@RequestMapping("/tests")
	private class TestModelRestController extends AbstractRestController<TestModel> {}

	/**
	 * Spring MVC test support
	 */
	private MockMvc mockMvc;

	/**
	 * The service, whose behavior will be mocked up. It will be injected to the
	 * controller to test.
	 */
	@Mock
	private AbstractCrudService<TestModel> serviceMock;

	/**
	 * The controller that will be tested.
	 */
	@InjectMocks
	private AbstractRestController<TestModel> restController;

	/**
	 * Test setup and init of mocks.
	 */
	@Before
	public void setUp() {

		restController = new TestModelRestController();

		// Process mock annotations
		MockitoAnnotations.initMocks(this);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
	}

	/**
	 * Tests whether the REST findAll interface will return all entities and a
	 * HTTP Status Code 200 (OK).
	 *
	 * @throws Exception
	 */
	@Test
	public void findAllEntities_shouldReturn_ListOfEntitiesAndOK()
			throws Exception {
		String firstValue = "value 1";
		String secondValue = "value 2";

		TestModel first = new TestModel();
		first.setTestValue(firstValue);

		TestModel second = new TestModel();
		second.setTestValue(secondValue);

		when(serviceMock.findAll()).thenReturn(Arrays.asList(first, second));

		// Test GET method
		mockMvc.perform(get("/tests"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].testValue", is(firstValue)))
			.andExpect(jsonPath("$[1].testValue", is(secondValue)));

		verify(serviceMock, times(1)).findAll();
		verifyNoMoreInteractions(serviceMock);
	}

	/**
	 * Tests whether the REST findById interface will return an expected entity
	 * and a HTTP Status Code 200 (OK).
	 * @throws Exception
	 *
	 */
	@Test
	public void findById_shouldReturn_EntityAndOK() throws Exception {
		int id = 42;
		String value = "value";

		TestModel testInstance = new TestModel();
		testInstance.setTestValue(value);

		TestUtil.setIdOnPersistentObject(testInstance, id);

		when(serviceMock.findById(id)).thenReturn(testInstance);

		// Test GET method with ID
		mockMvc.perform(get("/tests/" + id))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id", is(id)))
			.andExpect(jsonPath("$.testValue", is(value)));

		verify(serviceMock, times(1)).findById(id);
		verifyNoMoreInteractions(serviceMock);
	}

}
