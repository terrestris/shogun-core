package de.terrestris.shogun2.util.json;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author Nils Bühner
 */
public class ShogunCoreJsonObjectMapperTest {

    /**
     * The object mapper to test.
     */
    private final ObjectMapper objectMapper = new ShogunCoreJsonObjectMapper();

    /**
     * Tests whether the JodaModule is registered.
     */
    @Test
    public void testModules() {

        List<Module> modules = ShogunCoreJsonObjectMapper.findModules();

        assertEquals(2, modules.size());
        assertThat(modules.get(0), instanceOf(JodaModule.class));
        assertThat(modules.get(1), instanceOf(JtsModule.class));
    }

    /**
     * Tests whether the dateFormat is ISO8601
     */
    @Test
    public void testDateFormat() {

        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();

        DateFormat serializationDateFormat = serializationConfig.getDateFormat();
        DateFormat deserializationDateFormat = deserializationConfig.getDateFormat();

        assertThat(serializationDateFormat, instanceOf(StdDateFormat.class));
        assertThat(deserializationDateFormat, instanceOf(StdDateFormat.class));
    }

    /**
     * Tests whether the correct TimeZone is set.
     */
    @Test
    public void testTimezone() {

        TimeZone expectedTimeZone = TimeZone.getDefault();

        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();

        TimeZone serializationTimeZone = serializationConfig.getTimeZone();
        TimeZone deserializationTimeZone = deserializationConfig.getTimeZone();

        assertEquals(expectedTimeZone, serializationTimeZone);
        assertEquals(expectedTimeZone, deserializationTimeZone);
    }

    /**
     * Tests whether the dates are not serialized as timestamps, but as
     * their textual representation.
     */
    @Test
    public void testDateSerialization() {

        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();

        boolean writesDatesAsTimestamps = serializationConfig.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        assertFalse(writesDatesAsTimestamps);
    }

}
