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

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.service.ApplicationService;

/**
 *
 * @author Kai Volland
 * @author Nils Bühner
 * 
 * @param <E>
 */
public class AbstractRestControllerTest<E> {

	private MockMvc mockMvc;

	@Mock
	private ApplicationService applicationServiceMock;

	@InjectMocks
	private ApplicationRestController applicationRestController;

	@Before
	public void setUp() {

		// Process mock annotations
		MockitoAnnotations.initMocks(this);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(applicationRestController)
				.build();
	}

	@Test
	public void findAllEntities_shouldReturn_ListOfEntitiesAndOK()
			throws Exception {
		String firstAppName = "Application 0";
		String secondAppName = "Application 1";

		String firstAppDesc = "Description of Application 0";
		String secondAppDesc = "Description of Application 1";

		Application first = new Application(firstAppName, firstAppDesc);
		Application second = new Application(secondAppName, secondAppDesc);

		when(applicationServiceMock.findAll()).thenReturn(
				Arrays.asList(first, second));

		mockMvc.perform(get("/applications"))
			.andExpect(status().isOk())
			.andExpect(
				content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].name", is(firstAppName)))
			.andExpect(jsonPath("$[0].description", is(firstAppDesc)))
			.andExpect(jsonPath("$[1].name", is(secondAppName)))
			.andExpect(jsonPath("$[1].description", is(secondAppDesc)));

		verify(applicationServiceMock, times(1)).findAll();
		verifyNoMoreInteractions(applicationServiceMock);
	}
}
