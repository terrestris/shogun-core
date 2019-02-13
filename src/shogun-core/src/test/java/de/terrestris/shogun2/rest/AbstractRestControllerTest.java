package de.terrestris.shogun2.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.helper.IdHelper;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.service.AbstractCrudService;
import de.terrestris.shogun2.util.json.ShogunCoreJsonObjectMapper;

/**
 * @author Kai Volland
 * @author Nils Bühner
 */
public class AbstractRestControllerTest {

    /**
     * A private model class for that exists only to test the
     * {@link AbstractRestController}. This inner class has to be static,
     * otherwise Jackson will not be able to de-/serialize.
     */
    private static class TestModel extends PersistentObject {
        private static final long serialVersionUID = 1L;
        private String testValue;

        private TestModel() {
        }

        @SuppressWarnings("unused")
        public String getTestValue() {
            return testValue;
        }

        public void setTestValue(String testValue) {
            this.testValue = testValue;
        }
    }

    /**
     * A private REST controller for the {@link TestModel}. This class only
     * exists in the scope of this test and is used to test the
     * {@link AbstractRestController}.
     */
    @RestController
    @RequestMapping("/tests")
    private class TestModelRestController<E extends TestModel, D extends GenericHibernateDao<E, Integer>, S extends AbstractCrudService<E, D>>
        extends AbstractRestController<E, D, S> {

        @SuppressWarnings("unchecked")
        public TestModelRestController() {
            super((Class<E>) TestModel.class);
        }

        @Override
        public void setService(S service) {
            this.service = service;
        }
    }

    /**
     * Object mapper used to write JSON
     */
    private final ObjectMapper objectMapper = new ShogunCoreJsonObjectMapper();

    /**
     * Spring MVC test support
     */
    private MockMvc mockMvc;

    /**
     * The service, whose behavior will be mocked up. It will be injected to the
     * controller to test.
     */
    @Mock
    private AbstractCrudService<TestModel, GenericHibernateDao<TestModel, Integer>> serviceMock;

    /**
     * The controller that will be tested.
     */
    @InjectMocks
    private AbstractRestController<TestModel, GenericHibernateDao<TestModel, Integer>, AbstractCrudService<TestModel, GenericHibernateDao<TestModel, Integer>>> restController;

    /**
     * Test setup and init of mocks.
     */
    @Before
    public void setUp() {

        restController = new TestModelRestController<TestModel, GenericHibernateDao<TestModel, Integer>, AbstractCrudService<TestModel, GenericHibernateDao<TestModel, Integer>>>();

        restController.objectMapper = objectMapper;

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

        TestModel first = buildTestInstanceWithValue(firstValue);

        TestModel second = buildTestInstanceWithValue(secondValue);

        MultiValueMap<String, String> emptyMap = new LinkedMultiValueMap<>();
        when(serviceMock.findAllRestricted(emptyMap)).thenReturn(Arrays.asList(first, second));

        // Test GET method
        mockMvc.perform(get("/tests"))
            .andExpect(status().isOk())
            .andExpect(
                content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].testValue", is(firstValue)))
            .andExpect(jsonPath("$[1].testValue", is(secondValue)));

        verify(serviceMock, times(1)).findAllRestricted(emptyMap);
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST findById interface will return an expected entity
     * and a HTTP Status Code 200 (OK).
     *
     * @throws Exception
     */
    @Test
    public void findById_shouldReturn_EntityAndOK() throws Exception {
        int id = 42;
        String value = "find value";

        TestModel testInstance = buildTestInstanceWithValue(value);

        IdHelper.setIdOnPersistentObject(testInstance, id);

        when(serviceMock.findById(id)).thenReturn(testInstance);

        // Test GET method with ID
        mockMvc.perform(get("/tests/" + id))
            .andExpect(status().isOk())
            .andExpect(
                content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id", is(id)))
            .andExpect(jsonPath("$.testValue", is(value)));

        verify(serviceMock, times(1)).findById(id);
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST findById interface will return HTTP Status Code
     * 404 (NOT FOUND), if an exception occured.
     *
     * @throws Exception
     */
    @Test
    public void findById_shouldReturn_NotFound() throws Exception {
        int id = 42;

        when(serviceMock.findById(id)).thenThrow(new RuntimeException());

        // Test GET method with ID
        mockMvc.perform(get("/tests/" + id)).andExpect(status().isNotFound());

        verify(serviceMock, times(1)).findById(id);
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST save/create interface will work as expected, i.e.
     * return the created entity and a HTTP Status Code 201 (CREATED).
     *
     * @throws Exception
     */
    @Test
    public void save_shouldReturn_EntityAndCreated() throws Exception {
        final int id = 42;
        String value = "save value";

        TestModel withoutId = buildTestInstanceWithValue(value);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                TestModel entity = (TestModel) invocation.getArguments()[0];
                IdHelper.setIdOnPersistentObject(entity, id);
                return null;
            }
        }).when(serviceMock).saveOrUpdate(any(TestModel.class));

        // Test POST method with JSON payload
        mockMvc.perform(
            post("/tests").contentType(MediaType.APPLICATION_JSON).content(
                asJson(withoutId)))
            .andExpect(status().isCreated())
            .andExpect(
                content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id", is(id)))
            .andExpect(jsonPath("$.testValue", is(value)));

        verify(serviceMock, times(1)).saveOrUpdate(any(TestModel.class));
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST save/create interface (POST) return a HTTP Status
     * Code 400 (BAD REQUEST), if the payload already has an ID, which is not
     * allowed in the create/save case.
     *
     * @throws Exception
     */
    @Test
    public void save_shouldReturn_BadRequestDueToGivenId() throws Exception {
        int id = 42;
        String value = "save value";

        TestModel withId = buildTestInstanceWithIdAndValue(id, value);

        // Test POST method with JSON payload
        mockMvc.perform(
            post("/tests").contentType(MediaType.APPLICATION_JSON).content(
                asJson(withId))).andExpect(status().isBadRequest());

        verify(serviceMock, times(0)).saveOrUpdate(any(TestModel.class));
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST save/create (POST) interface return a HTTP Status
     * Code 400 (BAD REQUEST), if a {@link RuntimeException} occured on
     * saveOrUpdate.
     *
     * @throws Exception
     */
    @Test
    public void save_shouldReturn_BadRequestDueToRuntimeException()
        throws Exception {
        String value = "save value";

        TestModel payload = buildTestInstanceWithValue(value);

        doThrow(new RuntimeException()).when(serviceMock).saveOrUpdate(any(TestModel.class));

        // Test POST method with JSON payload
        mockMvc.perform(
            post("/tests").contentType(MediaType.APPLICATION_JSON).content(
                asJson(payload))).andExpect(status().isBadRequest());

        verify(serviceMock, times(1)).saveOrUpdate(any(TestModel.class));
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST update (PUT) interface will work as expected, i.e.
     * return the updated entity and a HTTP Status Code 200 (OK).
     *
     * @throws Exception
     */
    @Test
    public void update_shouldReturn_EntityAndOK() throws Exception {
        int id = 42;
        final String originalValue = "original value";
        final String updatedValue = "updated value";

        TestModel originalObject = buildTestInstanceWithIdAndValue(id,
            originalValue);
        TestModel updatedObject = buildTestInstanceWithIdAndValue(id,
            updatedValue);

        when(serviceMock.findById(id)).thenReturn(originalObject);

        when(serviceMock.updatePartialWithJsonNode(
            same(originalObject),
            any(JsonNode.class),
            any(ObjectMapper.class)))
            .thenReturn(updatedObject);

        // Test PUT method with JSON payload
        mockMvc.perform(
            put("/tests/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(asJson(updatedObject)))
            .andExpect(status().isOk())
            .andExpect(
                content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id", is(id)))
            .andExpect(jsonPath("$.testValue", is(updatedValue)));

        verify(serviceMock, times(1)).findById(id);
        verify(serviceMock, times(1)).updatePartialWithJsonNode(
            same(originalObject), any(JsonNode.class), any(ObjectMapper.class));
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST update (PUT) interface will return a HTTP Status
     * Code 400 (BAD REQUEST), if the uri ID path variable does not match the ID
     * value of the payload JSON.
     *
     * @throws Exception
     */
    @Test
    public void update_shouldReturn_BadRequestIfIdsDoNotMatch()
        throws Exception {
        int uriId = 17;
        int payloadId = 42;
        String updatedValue = "updated value";

        TestModel updatedObject = buildTestInstanceWithIdAndValue(payloadId,
            updatedValue);

        // Test PUT method with JSON payload
        mockMvc.perform(
            put("/tests/" + uriId).contentType(MediaType.APPLICATION_JSON)
                .content(asJson(updatedObject))).andExpect(
            status().isBadRequest());

        verify(serviceMock, times(0)).saveOrUpdate(any(TestModel.class));
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST update (PUT) interface will return a HTTP Status
     * Code 404 (NOT FOUND), if a {@link RuntimeException} occured on
     * saveOrUpdate.
     *
     * @throws Exception
     */
    @Test
    public void update_shouldReturn_NotFoundDueToRuntimeException()
        throws Exception {
        int id = 42;
        String originalValue = "original value";
        String updatedValue = "updated value";

        TestModel originalObject = buildTestInstanceWithIdAndValue(id,
            originalValue);
        TestModel updatedObject = buildTestInstanceWithIdAndValue(id,
            updatedValue);

        when(serviceMock.findById(id)).thenReturn(originalObject);

        doThrow(new RuntimeException()).when(serviceMock).updatePartialWithJsonNode(
            same(originalObject),
            any(JsonNode.class),
            any(ObjectMapper.class));

        // Test PUT method with JSON payload
        mockMvc.perform(
            put("/tests/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(asJson(updatedObject))).andExpect(
            status().isNotFound());

        verify(serviceMock, times(1)).findById(id);
        verify(serviceMock, times(1)).updatePartialWithJsonNode(
            same(originalObject),
            any(JsonNode.class),
            any(ObjectMapper.class));
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST delete (DELETE) interface will work as expected,
     * i.e. HTTP Status Code 204 (NO CONTENT).
     *
     * @throws Exception
     */
    @Test
    public void delete_shouldWorkAsExpectedAndReturn_NoContent()
        throws Exception {
        int id = 42;
        String value = "deleted value";

        TestModel entityToDelete = buildTestInstanceWithIdAndValue(id, value);

        when(serviceMock.findById(id)).thenReturn(entityToDelete);
        doNothing().when(serviceMock).delete(entityToDelete);

        // Test DELETE method
        mockMvc.perform(delete("/tests/" + id)).andExpect(
            status().isNoContent());

        verify(serviceMock, times(1)).findById(id);
        verify(serviceMock, times(1)).delete(entityToDelete);
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Tests whether the REST delete (DELETE) interface will return a HTTP
     * Status Code 404 (NOT FOUND).
     *
     * @throws Exception
     */
    @Test
    public void delete_shouldReturn_NotFoundDueToRuntimeException()
        throws Exception {
        int id = 42;
        String value = "deleted value";

        TestModel entityToDelete = buildTestInstanceWithIdAndValue(id, value);

        when(serviceMock.findById(id)).thenReturn(entityToDelete);
        doThrow(new RuntimeException()).when(serviceMock).delete(entityToDelete);

        // Test DELETE method
        mockMvc.perform(delete("/tests/" + id)).andExpect(
            status().isNotFound());

        verify(serviceMock, times(1)).findById(id);
        verify(serviceMock, times(1)).delete(entityToDelete);
        verifyNoMoreInteractions(serviceMock);
    }

    /**
     * Helper method to build a test instance without ID, but a value.
     *
     * @param value
     * @return
     */
    private TestModel buildTestInstanceWithValue(String value) {
        TestModel tm = new TestModel();
        tm.setTestValue(value);
        return tm;
    }

    /**
     * Helper method to build a test instance with ID and value
     *
     * @param value
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    private TestModel buildTestInstanceWithIdAndValue(int id, String value)
        throws Exception {
        TestModel tm = buildTestInstanceWithValue(value);

        IdHelper.setIdOnPersistentObject(tm, id);

        return tm;
    }

    /**
     * Helper method to get the JSON representation of a given {@link TestModel}
     *
     * @param object
     * @return JSON representation of the passed object
     * @throws JsonProcessingException
     */
    private String asJson(TestModel object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

}
