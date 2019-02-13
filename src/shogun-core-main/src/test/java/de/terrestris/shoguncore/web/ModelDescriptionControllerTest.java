package de.terrestris.shoguncore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import de.terrestris.shoguncore.service.ModelDescriptionService;
import de.terrestris.shoguncore.util.data.ResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test for {@link ModelDescriptionController}
 *
 * @author Andre Henn
 */
public class ModelDescriptionControllerTest {

    private MockMvc mockMvc;

    @Mock(name = "modelDescriptionService")
    private ModelDescriptionService modelDescriptionService;

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        ModelDescriptionController modelDescriptionController = new ModelDescriptionController();
        modelDescriptionController.setModelDescriptionService(modelDescriptionService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(modelDescriptionController).build();
    }

    @Test
    public void getJsonSchemaSuccessMap() throws Exception {
        final String className = "Module";
        final Class<?> foundClass = Class.forName("de.terrestris.shoguncore.model.module.".concat(className));
        final JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(new ObjectMapper());
        final JsonSchema schema = schemaGen.generateSchema(foundClass);

        Mockito.when(modelDescriptionService.getJsonSchema(
                Matchers.any(String.class))
        ).thenReturn(schema);

        String MODELDESCRIPTION_ENDPOINT = "/describeModel/asJson/"+className+".action";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(MODELDESCRIPTION_ENDPOINT)
                .param("className", className))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Assert.assertEquals("Returned body matched mocked one.",
                objectMapper.writeValueAsString(ResultSet.success(schema)), content);

        Mockito.verify(modelDescriptionService, Mockito.times(1)).
                getJsonSchema(Matchers.any(String.class));
        Mockito.verifyNoMoreInteractions(modelDescriptionService);
    }

    @Test
    public void getJsonSchemaErrorMap() throws Exception {
        final String className = "Module";
        final String errorMsg = "Model description (json) for model " + className + " is null.";

        Mockito.when(modelDescriptionService.getJsonSchema(
                Matchers.any(String.class))
        ).thenReturn(null);

        String MODELDESCRIPTION_ENDPOINT = "/describeModel/asJson/"+className+".action";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(MODELDESCRIPTION_ENDPOINT)
                .param("className", className))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Assert.assertEquals("Returned body matched mocked one.",
                objectMapper.writeValueAsString(ResultSet.error(errorMsg)), content);

        Mockito.verify(modelDescriptionService, Mockito.times(1)).
                getJsonSchema(Matchers.any(String.class));
        Mockito.verifyNoMoreInteractions(modelDescriptionService);
    }

}
