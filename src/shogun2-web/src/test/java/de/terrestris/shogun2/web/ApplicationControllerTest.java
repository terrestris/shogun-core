package de.terrestris.shogun2.web;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import de.terrestris.shogun2.web.ApplicationController;

/**
 * @author Nils Bühner
 *
 */
public class ApplicationControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ApplicationService applicationServiceMock;

	@InjectMocks
	private ApplicationController applicationController;

	@Before
	public void setUp() {

		// Process mock annotations
		MockitoAnnotations.initMocks(this);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(applicationController)
				.build();
	}

	@Test
	public void findAllApplications_shouldReturnAllApplicationEntries()
			throws Exception {

		String firstAppName = "Application 0";
		String secondAppName = "Application 1";

		String firstAppDesc = "Description of Application 0";
		String secondAppDesc = "Description of Application 1";

		Application first = new Application(firstAppName, firstAppDesc);
		Application second = new Application(secondAppName, secondAppDesc);

		when(applicationServiceMock.findAllApplications()).thenReturn(
				Arrays.asList(first, second));

		// Perform and test the GET-Request
		mockMvc.perform(get("/application/findAll.action"))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name", is(firstAppName)))
				.andExpect(jsonPath("$[0].description", is(firstAppDesc)))
				.andExpect(jsonPath("$[1].name", is(secondAppName)))
				.andExpect(jsonPath("$[1].description", is(secondAppDesc)));

		verify(applicationServiceMock, times(1)).findAllApplications();
		verifyNoMoreInteractions(applicationServiceMock);
	}
}
